package com.example.productservice.exception;

public class GatewayTimeout504Exception extends RuntimeException{
    public GatewayTimeout504Exception(String description) {
        super(description);
    }
}
