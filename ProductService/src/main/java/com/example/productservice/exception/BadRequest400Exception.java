package com.example.productservice.exception;

public class BadRequest400Exception extends RuntimeException{
    public BadRequest400Exception(String description) {
        super(description);
    }
}
