package com.example.productservice.exception;

public class UnAuthorized401Exception extends RuntimeException{
    public UnAuthorized401Exception(String description) {
        super(description);
    }
}
