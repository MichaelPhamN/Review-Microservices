package com.example.orderservice.exception;

public class Forbidden403Exception extends RuntimeException{
    public Forbidden403Exception(String description) {
        super(description);
    }
}
