package com.example.productservice.service;

import com.example.productservice.entity.Product;
import com.example.productservice.model.ProductRequest;
import com.example.productservice.model.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts();
    List<ProductResponse> getProductsByPriceBetween(double minPrice, double maxPrice);
    List<ProductResponse> getProductsByPriceGreaterThan(double price);
    List<ProductResponse> getProductsByPriceLessThan(double price);
    ProductResponse getProductById(long productId);
    List<ProductResponse> getProductsByName(String productName);
    List<ProductResponse> getProductsWithSorting(String fieldName, String sortDirection);
    Page<ProductResponse> getProductsWithPagination(int offset, int limit);
    Page<ProductResponse> getProductsWithSortingAndPagination(String fieldName, String sortDirection, int offset, int limit);
    long addProduct(ProductRequest productRequest);
    List<ProductResponse> addProducts(List<ProductRequest> productRequests);
    ProductResponse editProduct(ProductRequest productRequest, long productId);
    String deleteProductById(long productId);
    String deleteListProducts(List<Long> listIds);
    String deleteAllProducts();
    long countProducts();
    double total();
    boolean checkProductByProductId(long productId);
    boolean checkProductByProductName(String productName);
//    void reduceQuantity(long productId, long quantity);
}
