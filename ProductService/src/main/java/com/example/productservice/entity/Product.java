package com.example.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productId;

    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "PRODUCT_DESCRIPTION")
    private String productDescription;
    @Column(name = "PRODUCT_TYPE")
    private String productType;
    @Column(name = "PRICE")
    private double price;
    @Column(name = "QUANTITY")
    private long quantity;

    public Product(String productName, String productDescription, String productType, double price, long quantity) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productType = productType;
        this.price = price;
        this.quantity = quantity;
    }
}
