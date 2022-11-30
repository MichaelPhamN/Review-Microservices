package com.example.productservice.exception;

public class BadGateway502Exception extends RuntimeException{
    public BadGateway502Exception(String description) {
        super(description);
    }
}
