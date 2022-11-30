package com.example.productservice.constant;

public class URIConstant {
    //getProducts
    public static final String GET = "/api/product";
    //getProductById
    public static final String GET_BY_ID = "/api/product/{id}";
    //getProductByName
    public static final String GET_BY_NAME = "/api/products";
    //getProductByPrice
    public static final String GET_PRODUCT_BY_PRICE = "/api/productByPriceBetween";
    //getProductByPrice
    public static final String GET_PRODUCT_BY_PRICE_LESS_THAN = "/api/productByPriceLessThan";
    public static final String GET_PRODUCT_BY_PRICE_GREATER_THAN = "/api/productByPriceGreaterThan";
    //addProduct
    public static final String POST = "/api/product";
    //addProducts
    public static final String POST_ALL = "/api/products";
    //editProduct
    public static final String PUT = "/api/product";
    //deleteProductById
    public static final String DELETE_BY_ID = "/api/product/{id}";
    //deleteListProducts
    public static final String DELETE_LIST = "/api/productByIds";
    //deleteAllProducts
    public static final String DELETE_ALL = "/api/products";
    //checkProductById
    public static final String CHECK_BY_ID = "/api/product/{id}";
    //getProductByName
    public static final String CHECK_BY_NAME = "/api/product";
}
