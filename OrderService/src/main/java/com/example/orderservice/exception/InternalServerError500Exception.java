package com.example.orderservice.exception;

public class InternalServerError500Exception extends RuntimeException{
    public InternalServerError500Exception(String description) {
        super(description);
    }
}
