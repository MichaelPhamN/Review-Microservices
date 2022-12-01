package com.example.orderservice.exception;

public class BadRequest400Exception extends RuntimeException{
    public BadRequest400Exception(String description) {
        super(description);
    }
}
