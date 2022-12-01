package com.example.productservice.service.impl;

import com.example.productservice.entity.Product;
import com.example.productservice.exception.BadRequest400Exception;
import com.example.productservice.model.ProductRequest;
import com.example.productservice.model.ProductResponse;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductResponse> getProducts() {
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("Getting list product {}", products);
        return products;
    }

    @Override
    public List<ProductResponse> getProductsByPriceBetween(double minPrice, double maxPrice) {
        List<ProductResponse> productResponses =  productRepository.findProductByPriceBetween(minPrice, maxPrice).stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("Getting list product {} with price between {} and {}", productResponses, minPrice, maxPrice);
        return productResponses;
    }

    @Override
    public List<ProductResponse> getProductsByPriceGreaterThan(double price) {
        List<ProductResponse> productResponses =  productRepository.findProductByPriceGreaterThanEqual(price).stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("Getting list product {} with price greater than {}", productResponses, price);
        return productResponses;
    }

    @Override
    public List<ProductResponse> getProductsByPriceLessThan(double price) {
        List<ProductResponse> productResponses =  productRepository.findProductByPriceLessThan(price).stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("Getting list product {} with price less than {}", productResponses, price);
        return productResponses;
    }

    @Override
    public ProductResponse getProductById(long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BadRequest400Exception("Product is not found"));
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        log.info("Getting product {} by product id {}", product, productId);
        return productResponse;
    }

    @Override
    public List<ProductResponse> getProductsByName(String productName) {
        List<ProductResponse> productResponses = productRepository.findProductByProductNameContainsIgnoreCase(productName).stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("Getting list product {} by product name {}", productResponses, productName);
        return productResponses;
    }

    @Override
    public List<ProductResponse> getProductsWithSorting(String fieldName, String sortDirection) {
        Sort.Direction direction = Sort.Direction.ASC;
        if("desc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }
        List<ProductResponse> productResponses = productRepository.findAll(Sort.by(direction, fieldName)).stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("Getting list product {} by {} {}", productResponses, fieldName, direction);
        return productResponses;
    }

    @Override
    public Page<ProductResponse> getProductsWithPagination(int offset, int limit) {
        Page<ProductResponse> productResponses =  new PageImpl<>(productRepository.findAll(PageRequest.of(offset, limit)).stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList()));
        log.info("Getting list product {} from {} to {}", productResponses, (offset + 1), (offset + 1) * limit);
        return productResponses;
    }

    @Override
    public Page<ProductResponse> getProductsWithSortingAndPagination(String fieldName, String sortDirection, int offset, int limit) {
        Sort.Direction direction = Sort.Direction.ASC;
        if("desc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }
        Page<ProductResponse> productResponses = new PageImpl<>(productRepository.findAll(PageRequest.of(offset, limit).withSort(direction, fieldName)).stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList()));
        log.info("Getting list product {} from {} to {} with {}", productResponses, (offset + 1), (offset + 1) * limit, sortDirection);
        return productResponses;
    }

    @Override
    public long addProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .productName(productRequest.getName())
                .productDescription(productRequest.getDescription())
                .productType(productRequest.getType())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        log.info("Saving Product ...");
        productRepository.save(product);
        log.info("{} is created", product);
        return product.getProductId();
    }

    @Override
    public List<ProductResponse> addProducts(List<ProductRequest> productRequests) {
        List<Product> products = productRequests.stream().map(
                productRequest -> {
                    Product product = Product.builder()
                            .productName(productRequest.getName())
                            .productDescription(productRequest.getDescription())
                            .productType(productRequest.getType())
                            .price(productRequest.getPrice())
                            .quantity(productRequest.getQuantity())
                            .build();
                    return product;
                }).collect(Collectors.toList());
        log.info("Saving Products ...");
        productRepository.saveAll(products);
        List<ProductResponse> productResponses = products.stream().map(
                product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("{} have created", products);
        return productResponses;
    }

    @Override
    public ProductResponse editProduct(ProductRequest productRequest, long productId) {
        Product updateProduct = productRepository.findById(productId).orElseThrow(() -> new BadRequest400Exception("Product is not found"));

        updateProduct.builder()
                .productId(updateProduct.getProductId())
                .productName(productRequest.getName())
                .productDescription(productRequest.getDescription())
                .productType(productRequest.getType())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();

        log.info("Editing Product ...");
        productRepository.save(updateProduct);
        log.info("{} has edited", updateProduct);

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(updateProduct, productResponse);

        return productResponse;
    }

    @Override
    public String deleteProductById(long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BadRequest400Exception("Product is not found"));
        log.info("Deleting Product ...");
        productRepository.delete(product);
        log.info("{} has deleted", product);
        return "Delete product successful";
    }

    @Override
    public String deleteListProducts(List<Long> listIds) {
        listIds.forEach(id -> deleteProductById(id));
        return "Delete list product successful";
    }

    @Override
    public String deleteAllProducts() {
        log.info("Deleting All Products ...");
        productRepository.deleteAll();
        log.info("All Products have been deleted ...");
        return "Delete all products successful";
    }

    @Override
    public long countProducts() {
        long size = productRepository.count();
        log.info("There are {} products in db", size);
        return size;
    }

    @Override
    public double total() {
        double totalPrice = productRepository.getTotal();
        log.info("Total cost {}", totalPrice);
        return totalPrice;
    }

    @Override
    public boolean checkProductByProductId(long productId) {
        boolean isExisted = productRepository.existsById(productId);
        log.info("Product with id {} is {} existed", productId, isExisted ? "" : "not");
        return isExisted;
    }

    @Override
    public boolean checkProductByProductName(String productName) {
        boolean isExisted = productRepository.countProductByProductNameEqualsIgnoreCase(productName) > 0;
        log.info("Product with name {} is {} existed", productName, isExisted ? "" : "not");
        return isExisted;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {

    }
}
