package com.enterprise.cartservice.controller;
import com.enterprise.cartservice.dto.*;
import com.enterprise.cartservice.service.CartService;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/cart") @RequiredArgsConstructor @Slf4j @CrossOrigin(origins="*")
public class CartController {
    private final CartService cartService;
    @PostMapping("/add") public ResponseEntity<ApiResponse<CartResponse>> add(@Valid @RequestBody AddToCartRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Added", cartService.addToCart(req))); }
    @GetMapping("/{userId}") public ResponseEntity<ApiResponse<CartResponse>> get(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("Fetched", cartService.getCart(userId))); }
    @DeleteMapping("/{userId}/clear") public ResponseEntity<ApiResponse<Void>> clear(@PathVariable String userId) {
        cartService.clearCart(userId); return ResponseEntity.ok(ApiResponse.success("Cleared", null)); }
}
