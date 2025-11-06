package com.example.shop.service;

public class ProductInUseException extends RuntimeException {
    public ProductInUseException(String message) {
        super(message);
    }
    public ProductInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}