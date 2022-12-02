package com.example.productservice.integration;

import com.example.productservice.controller.ProductController;
import com.example.productservice.model.ProductResponse;
import com.example.productservice.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class ProductControllerValidationScenarios {
    private int testCase;
    private String description;
    private long productId;
    private ProductResponse productResponse;
    private ProductResponse expectedProductResponse;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Before
    public void setUp() {
        initMocks(this);
    }


    public ProductControllerValidationScenarios(int testCase,
                                                String description,
                                                long productId,
                                                ProductResponse productResponse,
                                                ProductResponse expectedProductResponse) {
        this.testCase = testCase;
        this.description = description;
        this.productId = productId;
        this.productResponse = productResponse;
        this.expectedProductResponse = expectedProductResponse;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> inputValidationScenarios() {
        int i = 0;
        return Arrays.asList(new Object[][]{
                {i++, "TC# 1 | Happy Path", 1, new ProductResponse(1,"iPhone X","Manufactured by Apple","phone",1499.99,6), new ProductResponse(1,"iPhone X","Manufactured by Apple","phone",1499.99,6)},
        });
    }

    @Test
    public void shouldReturnExpectedProductResponse() {
        //given
        when(productService.getProductById(productId)).thenReturn(productResponse);

        //when
        ResponseEntity<ProductResponse> response = productController.getProductById(productId);

        //then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedProductResponse.getProductId(), response.getBody().getProductId());
        assertEquals(expectedProductResponse.getProductName(), response.getBody().getProductName());
        assertEquals(expectedProductResponse.getProductDescription(), response.getBody().getProductDescription());
        assertEquals(expectedProductResponse.getProductType(), response.getBody().getProductType());
        assertEquals(expectedProductResponse.getPrice(), response.getBody().getPrice(), 0.0);
        assertEquals(expectedProductResponse.getQuantity(), response.getBody().getQuantity());

        verify(productService).getProductById(1);
    }
}
