package com.enterprise.cartservice.exception;
public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String m) { super(m); }
}
