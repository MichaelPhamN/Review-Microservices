package com.example.orderservice.exception;

public class NotFound404Exception extends RuntimeException{
    public NotFound404Exception(String description) {
        super(description);
    }
}
