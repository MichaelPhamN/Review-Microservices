package com.example.productservice.model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private String type;
    private double price;
    private long quantity;
}
