package com.example.productservice.controller;

import com.example.productservice.constant.URIConstant;
import com.example.productservice.entity.Product;
import com.example.productservice.exception.BadRequest400Exception;
import com.example.productservice.model.ProductRequest;
import com.example.productservice.model.ProductResponse;
import com.example.productservice.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping(URIConstant.GET)
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
    }

    @GetMapping(URIConstant.GET_BY_ID)
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id) {
        if(id < 0) {
            log.warn("Product Id: {} is invalid", id);
            throw new BadRequest400Exception("Product data is invalid.");
        }
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @GetMapping(URIConstant.GET_BY_NAME)
    public ResponseEntity<List<ProductResponse>> getProductsByName(@RequestParam String productName) {
        if(productName == null || productName.length() == 0) {
            throw new BadRequest400Exception("Product name is invalid.");
        }
        return new ResponseEntity<>(productService.getProductsByName(productName), HttpStatus.OK);
    }

    @GetMapping(URIConstant.GET_PRODUCT_BY_PRICE)
    public ResponseEntity<List<ProductResponse>> getProductsByPriceBetween(@RequestParam double minPrice, @RequestParam double maxPrice) {
        if(minPrice < 0 || maxPrice < 0 || (minPrice > maxPrice)) {
            throw new BadRequest400Exception("Product Price is invalid.");
        }
        return new ResponseEntity<>(productService.getProductsByPriceBetween(minPrice, maxPrice), HttpStatus.OK);
    }

    @GetMapping(URIConstant.GET_PRODUCT_BY_PRICE_LESS_THAN)
    public ResponseEntity<List<ProductResponse>> getProductsByPriceLessThan(@RequestParam double price) {
        if(price < 0) {
            throw new BadRequest400Exception("Product Price is invalid.");
        }
        return new ResponseEntity<>(productService.getProductsByPriceLessThan(price), HttpStatus.OK);
    }

    @GetMapping(URIConstant.GET_PRODUCT_BY_PRICE_GREATER_THAN)
    public ResponseEntity<List<ProductResponse>> getProductsByPriceGreaterThan(@RequestParam double price) {
        if(price < 0) {
            throw new BadRequest400Exception("Product Price is invalid.");
        }
        return new ResponseEntity<>(productService.getProductsByPriceGreaterThan(price), HttpStatus.OK);
    }

    @PostMapping(URIConstant.POST)
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest) {
        if(productRequest == null
                || productRequest.getName() == null
                || productRequest.getName().length() == 0
                || productRequest.getType() == null
                || productRequest.getType().length() == 0
                || productRequest.getPrice() < 0
                || productRequest.getQuantity() < 0) {
            log.warn("Product {} is invalid", productRequest);
            throw new BadRequest400Exception("Product data is invalid.");
        }

        long productId = productService.addProduct(productRequest);
        if (productId < 1) {
            log.warn("Inserted product {} failed", productRequest);
            throw new BadRequest400Exception("Inserted product failed.");
        }

        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    @PostMapping(URIConstant.POST_ALL)
    public ResponseEntity<List<ProductResponse>> addProducts(@RequestBody List<ProductRequest> products) {
        if(products == null) {
            log.warn("List product is null");
            throw new BadRequest400Exception("List product is null.");
        }

        List<ProductResponse> productList = productService.addProducts(products);
        if (productList.size() < 1) {
            log.warn("Inserted product failed");
            throw new BadRequest400Exception("Inserted product failed.");
        }

        return new ResponseEntity<>(productList, HttpStatus.CREATED);
    }

    @PutMapping(URIConstant.PUT)
    public ResponseEntity<ProductResponse> editProduct(@RequestBody ProductRequest productRequest, @PathVariable long productId) {
        if(productRequest == null
                || productId < 0
                || productRequest.getName() == null
                || productRequest.getName().length() == 0
                || productRequest.getType() == null
                || productRequest.getType().length() == 0
                || productRequest.getPrice() < 0
                || productRequest.getQuantity() < 0) {
            log.warn("Product {} is invalid", productRequest);
            throw new BadRequest400Exception("Product data is invalid.");
        }

        return new ResponseEntity<>(productService.editProduct(productRequest, productId), HttpStatus.OK);
    }

    @DeleteMapping(URIConstant.DELETE_BY_ID)
    public ResponseEntity<String> deleteProductById(@PathVariable long id) {
        if(id < 0) {
            log.warn("Product Id: {} is invalid", id);
            throw new BadRequest400Exception("Product data is invalid.");
        }
        return new ResponseEntity<>(productService.deleteProductById(id), HttpStatus.OK);
    }

    @DeleteMapping(URIConstant.DELETE_LIST)
    public ResponseEntity<String> deleteListProducts(@RequestBody List<Long> ids) {
        if(ids.size() < 1) {
            log.warn("List product id is invalid");
            throw new BadRequest400Exception("List product id is invalid.");
        }
        return new ResponseEntity<>(productService.deleteListProducts(ids), HttpStatus.OK);
    }

    @DeleteMapping(URIConstant.DELETE_ALL)
    public ResponseEntity<String> deleteAllProducts() {
        return new ResponseEntity<>(productService.deleteAllProducts(), HttpStatus.OK);
    }

//    @GetMapping(URIConstant.CHECK_BY_ID)
//    public ResponseEntity<Boolean> checkProductByProductId(@PathVariable long id) {
//        if(id < 0) {
//            log.warn("Product Id: {} is invalid", id);
//            throw new BadRequest400Exception("Product data is invalid.");
//        }
//        return new ResponseEntity<>(productService.checkProductByProductId(id), HttpStatus.OK);
//    }
//
//    @GetMapping(URIConstant.CHECK_BY_NAME)
//    public ResponseEntity<Boolean> checkProductByProductId(@RequestParam String productName) {
//        if(productName.length() < 1) {
//            log.warn("Product name is invalid");
//            throw new BadRequest400Exception("Product name is invalid.");
//        }
//        return new ResponseEntity<>(productService.checkProductByProductName(productName), HttpStatus.OK);
//    }
}
