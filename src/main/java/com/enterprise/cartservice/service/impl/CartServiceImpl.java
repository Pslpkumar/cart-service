package com.enterprise.cartservice.service.impl;

import com.enterprise.cartservice.dto.*;
import com.enterprise.cartservice.entity.*;
import com.enterprise.cartservice.exception.*;
import com.enterprise.cartservice.kafka.CartKafkaProducer;
import com.enterprise.cartservice.repository.*;
import com.enterprise.cartservice.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j @Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final WebClient productServiceWebClient;
    private final CartKafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Override
    public CartResponse addToCart(AddToCartRequest req) {
        CompletableFuture<ProductDTO> pf = fetchAsync(req.getProductId());
        CompletableFuture<Boolean> sf = validateAsync(req.getProductId(), req.getQuantity());
        CompletableFuture.allOf(pf, sf).join();

        ProductDTO product = pf.join();
        Boolean ok = sf.join();

        if (!ok) throw new InsufficientStockException("Insufficient stock: " + product.getName());

        Cart cart = cartRepo.findByUserId(req.getUserId())
                .orElseGet(() -> cartRepo.save(Cart.builder().userId(req.getUserId()).build()));

        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(req.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        i -> { i.setQuantity(i.getQuantity() + req.getQuantity()); itemRepo.save(i); },
                        () -> {
                            CartItem n = CartItem.builder()
                                    .cart(cart)
                                    .productId(req.getProductId())
                                    .quantity(req.getQuantity())
                                    .build();
                            itemRepo.save(n);
                            cart.getItems().add(n);
                        }
                );

        kafkaProducer.publishCartEvent(KafkaCartEvent.builder()
                .cartId(cart.getId())
                .productId(req.getProductId())
                .quantity(req.getQuantity())
                .eventType("ITEM_ADDED")
                .timestamp(LocalDateTime.now().toString())
                .build());

        return buildResp(cart, product);
    }

    @Override @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + userId));

        List<CartItemResponse> items = cart.getItems().stream().map(i -> {
            ProductDTO p = syncFetch(i.getProductId());
            return CartItemResponse.builder()
                    .id(i.getId())
                    .productId(i.getProductId())
                    .productName(p.getName())
                    .productPrice(p.getPrice())
                    .quantity(i.getQuantity())
                    .totalPrice(p.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .grandTotal(total)
                .build();
    }

    @Override
    public void clearCart(String userId) {
        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found: " + userId));
        itemRepo.deleteByCartId(cart.getId());
        cart.getItems().clear();
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private ProductDTO syncFetch(Integer id) {
        // Product Service returns ApiResponse<ProductDTO> wrapper — extract "data" field
        Map response = productServiceWebClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("data") == null)
            throw new RuntimeException("Product not found: " + id);

        return objectMapper.convertValue(response.get("data"), ProductDTO.class);
    }

    @Async("cartTaskExecutor")
    public CompletableFuture<ProductDTO> fetchAsync(Integer id) {
        log.info("[Async] fetch product:{} thread:{}", id, Thread.currentThread().getName());
        return CompletableFuture.completedFuture(syncFetch(id));
    }

    @Async("cartTaskExecutor")
    public CompletableFuture<Boolean> validateAsync(Integer id, Integer qty) {
        log.info("[Async] validate product:{} qty:{} thread:{}", id, qty, Thread.currentThread().getName());

        // Product Service returns ApiResponse<Boolean> wrapper — extract "data" field
        Map response = productServiceWebClient.get()
                .uri("/api/products/{id}/validate-stock?quantity={q}", id, qty)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Boolean v = response != null ? (Boolean) response.get("data") : false;
        return CompletableFuture.completedFuture(v != null && v);
    }

    private CartResponse buildResp(Cart cart, ProductDTO product) {
        List<CartItemResponse> items = cart.getItems().stream().map(i ->
                CartItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(product.getName())
                        .productPrice(product.getPrice())
                        .quantity(i.getQuantity())
                        .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build()
        ).collect(Collectors.toList());

        BigDecimal t = items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .grandTotal(t)
                .build();
    }
}