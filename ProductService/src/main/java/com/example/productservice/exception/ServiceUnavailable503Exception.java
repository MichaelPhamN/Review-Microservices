package com.example.productservice.exception;

public class ServiceUnavailable503Exception extends RuntimeException{
    public ServiceUnavailable503Exception(String description) {
        super(description);
    }
}
