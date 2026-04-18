package com.enterprise.cartservice.exception;
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String m) { super(m); }
}
