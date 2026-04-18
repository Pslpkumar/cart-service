package com.enterprise.cartservice.service;
import com.enterprise.cartservice.dto.*;
public interface CartService {
    CartResponse addToCart(AddToCartRequest req);
    CartResponse getCart(String userId);
    void clearCart(String userId);
}
