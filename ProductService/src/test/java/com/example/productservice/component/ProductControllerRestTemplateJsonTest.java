package com.example.productservice.component;

import com.example.productservice.constant.URIConstant;
import com.example.productservice.model.ProductRequest;
import com.example.productservice.model.ProductResponse;
import com.example.productservice.repository.TestH2Repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerRestTemplateJsonTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository h2Repository;

    private HttpHeaders headers;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        headers = new HttpHeaders();
        baseUrl = baseUrl.concat(":").concat(port + "");
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddProductMappingJackson2HttpMessageConverter() {
        baseUrl = baseUrl.concat(URIConstant.POST);
        ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,6);
        restTemplate.setMessageConverters(getJsonMessageConverters());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<ProductRequest> request = new HttpEntity<>(productRequest, headers);

        long productId = restTemplate.postForObject(baseUrl, request, Long.class);

        assertEquals(1, productId);
        assertAll(
                () -> assertNotNull(h2Repository.findAll().get(0)),
                () -> assertEquals(1, h2Repository.findAll().get(0).getProductId()),
                () -> assertEquals("iPhone X", h2Repository.findAll().get(0).getProductName()),
                () -> assertEquals("Manufactured by Apple", h2Repository.findAll().get(0).getProductDescription()),
                () -> assertEquals("phone", h2Repository.findAll().get(0).getProductType()),
                () -> assertEquals(1499.99, h2Repository.findAll().get(0).getPrice()),
                () -> assertEquals(6, h2Repository.findAll().get(0).getQuantity())
        );
    }

    private List<HttpMessageConverter<?>> getJsonMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        return converters;
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO PRODUCT (product_id, product_name, product_description, product_type, price, quantity) VALUES " +
                            "(1,'iPhone X','Manufactured by Apple','phone',1499.99,6), " +
                            "(2,'Galaxy S10','Manufactured by Samsung','phone',1299.99,3), " +
                            "(3,'Pixel 5','Manufactured by Google','phone',1099.99,4), " +
                            "(4,'Dell XPS 15','Manufactured by Dell','laptop',1799.99,6), " +
                            "(5,'HP Envy 13','Manufactured by HP','laptop',1299.99,2), " +
                            "(6,'Lenovo IdeaCentre 5i Gaming Desktop','Manufactured by Lenovo','desktop',999.99,6)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProducts() {
        baseUrl = baseUrl.concat(URIConstant.GET);
        List<ProductResponse> productResponses = convertJsonToObject(restTemplate.getForObject(baseUrl, List.class));
        assertNotNull(productResponses);
        assertEquals(6, productResponses.size());
        assertAll(
            //product 0
            () -> assertNotNull(productResponses.get(0)),
            () -> assertEquals(productResponses.get(0).getProductId(), h2Repository.findAll().get(0).getProductId()),
            () -> assertEquals(productResponses.get(0).getProductName(), h2Repository.findAll().get(0).getProductName()),
            () -> assertEquals(productResponses.get(0).getProductDescription(), h2Repository.findAll().get(0).getProductDescription()),
            () -> assertEquals(productResponses.get(0).getProductType(), h2Repository.findAll().get(0).getProductType()),
            () -> assertEquals(productResponses.get(0).getPrice(), h2Repository.findAll().get(0).getPrice()),
            () -> assertEquals(productResponses.get(0).getQuantity(), h2Repository.findAll().get(0).getQuantity()),

            //product 1
            () -> assertNotNull(productResponses.get(1)),
            () -> assertEquals(productResponses.get(1).getProductId(), h2Repository.findAll().get(1).getProductId()),
            () -> assertEquals(productResponses.get(1).getProductName(), h2Repository.findAll().get(1).getProductName()),
            () -> assertEquals(productResponses.get(1).getProductDescription(), h2Repository.findAll().get(1).getProductDescription()),
            () -> assertEquals(productResponses.get(1).getProductType(), h2Repository.findAll().get(1).getProductType()),
            () -> assertEquals(productResponses.get(1).getPrice(), h2Repository.findAll().get(1).getPrice()),
            () -> assertEquals(productResponses.get(1).getQuantity(), h2Repository.findAll().get(1).getQuantity()),

            //product 2
            () -> assertNotNull(productResponses.get(2)),
            () -> assertEquals(productResponses.get(2).getProductId(), h2Repository.findAll().get(2).getProductId()),
            () -> assertEquals(productResponses.get(2).getProductName(), h2Repository.findAll().get(2).getProductName()),
            () -> assertEquals(productResponses.get(2).getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
            () -> assertEquals(productResponses.get(2).getProductType(), h2Repository.findAll().get(2).getProductType()),
            () -> assertEquals(productResponses.get(2).getPrice(), h2Repository.findAll().get(2).getPrice()),
            () -> assertEquals(productResponses.get(2).getQuantity(), h2Repository.findAll().get(2).getQuantity()),

            //product 3
            () -> assertNotNull(productResponses.get(3)),
            () -> assertEquals(productResponses.get(3).getProductId(), h2Repository.findAll().get(3).getProductId()),
            () -> assertEquals(productResponses.get(3).getProductName(), h2Repository.findAll().get(3).getProductName()),
            () -> assertEquals(productResponses.get(3).getProductDescription(), h2Repository.findAll().get(3).getProductDescription()),
            () -> assertEquals(productResponses.get(3).getProductType(), h2Repository.findAll().get(3).getProductType()),
            () -> assertEquals(productResponses.get(3).getPrice(), h2Repository.findAll().get(3).getPrice()),
            () -> assertEquals(productResponses.get(3).getQuantity(), h2Repository.findAll().get(3).getQuantity()),

            //product 4
            () -> assertNotNull(productResponses.get(4)),
            () -> assertEquals(productResponses.get(4).getProductId(), h2Repository.findAll().get(4).getProductId()),
            () -> assertEquals(productResponses.get(4).getProductName(), h2Repository.findAll().get(4).getProductName()),
            () -> assertEquals(productResponses.get(4).getProductDescription(), h2Repository.findAll().get(4).getProductDescription()),
            () -> assertEquals(productResponses.get(4).getProductType(), h2Repository.findAll().get(4).getProductType()),
            () -> assertEquals(productResponses.get(4).getPrice(), h2Repository.findAll().get(4).getPrice()),
            () -> assertEquals(productResponses.get(4).getQuantity(), h2Repository.findAll().get(4).getQuantity()),

            //product 5
            () -> assertNotNull(productResponses.get(5)),
            () -> assertEquals(productResponses.get(5).getProductId(), h2Repository.findAll().get(5).getProductId()),
            () -> assertEquals(productResponses.get(5).getProductName(), h2Repository.findAll().get(5).getProductName()),
            () -> assertEquals(productResponses.get(5).getProductDescription(), h2Repository.findAll().get(5).getProductDescription()),
            () -> assertEquals(productResponses.get(5).getProductType(), h2Repository.findAll().get(5).getProductType()),
            () -> assertEquals(productResponses.get(5).getPrice(), h2Repository.findAll().get(5).getPrice()),
            () -> assertEquals(productResponses.get(5).getQuantity(), h2Repository.findAll().get(5).getQuantity())
        );
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO PRODUCT (product_id, product_name, product_description, product_type, price, quantity) VALUES " +
            "(1,'iPhone X','Manufactured by Apple','phone',1499.99,6), " +
            "(2,'Galaxy S10','Manufactured by Samsung','phone',1299.99,3), " +
            "(3,'Pixel 5','Manufactured by Google','phone',1099.99,4), " +
            "(4,'Dell XPS 15','Manufactured by Dell','laptop',1799.99,6), " +
            "(5,'HP Envy 13','Manufactured by HP','laptop',1299.99,2), " +
            "(6,'Lenovo IdeaCentre 5i Gaming Desktop','Manufactured by Lenovo','desktop',999.99,6)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProductsByPriceBetween() {
        baseUrl = baseUrl.concat(URIConstant.GET_PRODUCT_BY_PRICE + "?minPrice=1000&maxPrice=1500");
        List<ProductResponse> productResponses = convertJsonToObject(restTemplate.getForObject(baseUrl, List.class));
        assertNotNull(productResponses);
        assertEquals(4, productResponses.size());
        assertAll(
                //product 0
                () -> assertNotNull(productResponses.get(0)),
                () -> assertEquals(productResponses.get(0).getProductId(), h2Repository.findAll().get(0).getProductId()),
                () -> assertEquals(productResponses.get(0).getProductName(), h2Repository.findAll().get(0).getProductName()),
                () -> assertEquals(productResponses.get(0).getProductDescription(), h2Repository.findAll().get(0).getProductDescription()),
                () -> assertEquals(productResponses.get(0).getProductType(), h2Repository.findAll().get(0).getProductType()),
                () -> assertEquals(productResponses.get(0).getPrice(), h2Repository.findAll().get(0).getPrice()),
                () -> assertEquals(productResponses.get(0).getQuantity(), h2Repository.findAll().get(0).getQuantity()),

                //product 1
                () -> assertNotNull(productResponses.get(1)),
                () -> assertEquals(productResponses.get(1).getProductId(), h2Repository.findAll().get(1).getProductId()),
                () -> assertEquals(productResponses.get(1).getProductName(), h2Repository.findAll().get(1).getProductName()),
                () -> assertEquals(productResponses.get(1).getProductDescription(), h2Repository.findAll().get(1).getProductDescription()),
                () -> assertEquals(productResponses.get(1).getProductType(), h2Repository.findAll().get(1).getProductType()),
                () -> assertEquals(productResponses.get(1).getPrice(), h2Repository.findAll().get(1).getPrice()),
                () -> assertEquals(productResponses.get(1).getQuantity(), h2Repository.findAll().get(1).getQuantity()),

                //product 2
                () -> assertNotNull(productResponses.get(2)),
                () -> assertEquals(productResponses.get(2).getProductId(), h2Repository.findAll().get(2).getProductId()),
                () -> assertEquals(productResponses.get(2).getProductName(), h2Repository.findAll().get(2).getProductName()),
                () -> assertEquals(productResponses.get(2).getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
                () -> assertEquals(productResponses.get(2).getProductType(), h2Repository.findAll().get(2).getProductType()),
                () -> assertEquals(productResponses.get(2).getPrice(), h2Repository.findAll().get(2).getPrice()),
                () -> assertEquals(productResponses.get(2).getQuantity(), h2Repository.findAll().get(2).getQuantity()),

                //product 3
                () -> assertNotNull(productResponses.get(3)),
                () -> assertEquals(productResponses.get(3).getProductId(), h2Repository.findAll().get(4).getProductId()),
                () -> assertEquals(productResponses.get(3).getProductName(), h2Repository.findAll().get(4).getProductName()),
                () -> assertEquals(productResponses.get(3).getProductDescription(), h2Repository.findAll().get(4).getProductDescription()),
                () -> assertEquals(productResponses.get(3).getProductType(), h2Repository.findAll().get(4).getProductType()),
                () -> assertEquals(productResponses.get(3).getPrice(), h2Repository.findAll().get(4).getPrice()),
                () -> assertEquals(productResponses.get(3).getQuantity(), h2Repository.findAll().get(4).getQuantity())
        );
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO PRODUCT (product_id, product_name, product_description, product_type, price, quantity) VALUES " +
            "(1,'iPhone X','Manufactured by Apple','phone',1499.99,6), " +
            "(2,'Galaxy S10','Manufactured by Samsung','phone',1299.99,3), " +
            "(3,'Pixel 5','Manufactured by Google','phone',1099.99,4), " +
            "(4,'Dell XPS 15','Manufactured by Dell','laptop',1799.99,6), " +
            "(5,'HP Envy 13','Manufactured by HP','laptop',1299.99,2), " +
            "(6,'Lenovo IdeaCentre 5i Gaming Desktop','Manufactured by Lenovo','desktop',999.99,6)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProductsByPriceLessThan() {
        baseUrl = baseUrl.concat(URIConstant.GET_PRODUCT_BY_PRICE_LESS_THAN + "?price=1500");
        List<ProductResponse> productResponses = convertJsonToObject(restTemplate.getForObject(baseUrl, List.class));
        assertNotNull(productResponses);
        assertEquals(5, productResponses.size());
        assertAll(
                //product 0
                () -> assertNotNull(productResponses.get(0)),
                () -> assertEquals(productResponses.get(0).getProductId(), h2Repository.findAll().get(0).getProductId()),
                () -> assertEquals(productResponses.get(0).getProductName(), h2Repository.findAll().get(0).getProductName()),
                () -> assertEquals(productResponses.get(0).getProductDescription(), h2Repository.findAll().get(0).getProductDescription()),
                () -> assertEquals(productResponses.get(0).getProductType(), h2Repository.findAll().get(0).getProductType()),
                () -> assertEquals(productResponses.get(0).getPrice(), h2Repository.findAll().get(0).getPrice()),
                () -> assertEquals(productResponses.get(0).getQuantity(), h2Repository.findAll().get(0).getQuantity()),

                //product 1
                () -> assertNotNull(productResponses.get(1)),
                () -> assertEquals(productResponses.get(1).getProductId(), h2Repository.findAll().get(1).getProductId()),
                () -> assertEquals(productResponses.get(1).getProductName(), h2Repository.findAll().get(1).getProductName()),
                () -> assertEquals(productResponses.get(1).getProductDescription(), h2Repository.findAll().get(1).getProductDescription()),
                () -> assertEquals(productResponses.get(1).getProductType(), h2Repository.findAll().get(1).getProductType()),
                () -> assertEquals(productResponses.get(1).getPrice(), h2Repository.findAll().get(1).getPrice()),
                () -> assertEquals(productResponses.get(1).getQuantity(), h2Repository.findAll().get(1).getQuantity()),

                //product 2
                () -> assertNotNull(productResponses.get(2)),
                () -> assertEquals(productResponses.get(2).getProductId(), h2Repository.findAll().get(2).getProductId()),
                () -> assertEquals(productResponses.get(2).getProductName(), h2Repository.findAll().get(2).getProductName()),
                () -> assertEquals(productResponses.get(2).getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
                () -> assertEquals(productResponses.get(2).getProductType(), h2Repository.findAll().get(2).getProductType()),
                () -> assertEquals(productResponses.get(2).getPrice(), h2Repository.findAll().get(2).getPrice()),
                () -> assertEquals(productResponses.get(2).getQuantity(), h2Repository.findAll().get(2).getQuantity()),

                //product 3
                () -> assertNotNull(productResponses.get(3)),
                () -> assertEquals(productResponses.get(3).getProductId(), h2Repository.findAll().get(4).getProductId()),
                () -> assertEquals(productResponses.get(3).getProductName(), h2Repository.findAll().get(4).getProductName()),
                () -> assertEquals(productResponses.get(3).getProductDescription(), h2Repository.findAll().get(4).getProductDescription()),
                () -> assertEquals(productResponses.get(3).getProductType(), h2Repository.findAll().get(4).getProductType()),
                () -> assertEquals(productResponses.get(3).getPrice(), h2Repository.findAll().get(4).getPrice()),
                () -> assertEquals(productResponses.get(3).getQuantity(), h2Repository.findAll().get(4).getQuantity()),

                //product 4
                () -> assertNotNull(productResponses.get(4)),
                () -> assertEquals(productResponses.get(4).getProductId(), h2Repository.findAll().get(5).getProductId()),
                () -> assertEquals(productResponses.get(4).getProductName(), h2Repository.findAll().get(5).getProductName()),
                () -> assertEquals(productResponses.get(4).getProductDescription(), h2Repository.findAll().get(5).getProductDescription()),
                () -> assertEquals(productResponses.get(4).getProductType(), h2Repository.findAll().get(5).getProductType()),
                () -> assertEquals(productResponses.get(4).getPrice(), h2Repository.findAll().get(5).getPrice()),
                () -> assertEquals(productResponses.get(4).getQuantity(), h2Repository.findAll().get(5).getQuantity())
        );
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO PRODUCT (product_id, product_name, product_description, product_type, price, quantity) VALUES " +
            "(1,'iPhone X','Manufactured by Apple','phone',1499.99,6), " +
            "(2,'Galaxy S10','Manufactured by Samsung','phone',1299.99,3), " +
            "(3,'Pixel 5','Manufactured by Google','phone',1099.99,4), " +
            "(4,'Dell XPS 15','Manufactured by Dell','laptop',1799.99,6), " +
            "(5,'HP Envy 13','Manufactured by HP','laptop',1299.99,2), " +
            "(6,'Lenovo IdeaCentre 5i Gaming Desktop','Manufactured by Lenovo','desktop',999.99,6)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProductsByPriceGreaterThan() {
        baseUrl = baseUrl.concat(URIConstant.GET_PRODUCT_BY_PRICE_GREATER_THAN + "?price=1000");
        List<ProductResponse> productResponses = convertJsonToObject(restTemplate.getForObject(baseUrl, List.class));
        assertNotNull(productResponses);
        assertEquals(5, productResponses.size());
        assertAll(
                //product 0
                () -> assertNotNull(productResponses.get(0)),
                () -> assertEquals(productResponses.get(0).getProductId(), h2Repository.findAll().get(0).getProductId()),
                () -> assertEquals(productResponses.get(0).getProductName(), h2Repository.findAll().get(0).getProductName()),
                () -> assertEquals(productResponses.get(0).getProductDescription(), h2Repository.findAll().get(0).getProductDescription()),
                () -> assertEquals(productResponses.get(0).getProductType(), h2Repository.findAll().get(0).getProductType()),
                () -> assertEquals(productResponses.get(0).getPrice(), h2Repository.findAll().get(0).getPrice()),
                () -> assertEquals(productResponses.get(0).getQuantity(), h2Repository.findAll().get(0).getQuantity()),

                //product 1
                () -> assertNotNull(productResponses.get(1)),
                () -> assertEquals(productResponses.get(1).getProductId(), h2Repository.findAll().get(1).getProductId()),
                () -> assertEquals(productResponses.get(1).getProductName(), h2Repository.findAll().get(1).getProductName()),
                () -> assertEquals(productResponses.get(1).getProductDescription(), h2Repository.findAll().get(1).getProductDescription()),
                () -> assertEquals(productResponses.get(1).getProductType(), h2Repository.findAll().get(1).getProductType()),
                () -> assertEquals(productResponses.get(1).getPrice(), h2Repository.findAll().get(1).getPrice()),
                () -> assertEquals(productResponses.get(1).getQuantity(), h2Repository.findAll().get(1).getQuantity()),

                //product 2
                () -> assertNotNull(productResponses.get(2)),
                () -> assertEquals(productResponses.get(2).getProductId(), h2Repository.findAll().get(2).getProductId()),
                () -> assertEquals(productResponses.get(2).getProductName(), h2Repository.findAll().get(2).getProductName()),
                () -> assertEquals(productResponses.get(2).getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
                () -> assertEquals(productResponses.get(2).getProductType(), h2Repository.findAll().get(2).getProductType()),
                () -> assertEquals(productResponses.get(2).getPrice(), h2Repository.findAll().get(2).getPrice()),
                () -> assertEquals(productResponses.get(2).getQuantity(), h2Repository.findAll().get(2).getQuantity()),

                //product 3
                () -> assertNotNull(productResponses.get(3)),
                () -> assertEquals(productResponses.get(3).getProductId(), h2Repository.findAll().get(3).getProductId()),
                () -> assertEquals(productResponses.get(3).getProductName(), h2Repository.findAll().get(3).getProductName()),
                () -> assertEquals(productResponses.get(3).getProductDescription(), h2Repository.findAll().get(3).getProductDescription()),
                () -> assertEquals(productResponses.get(3).getProductType(), h2Repository.findAll().get(3).getProductType()),
                () -> assertEquals(productResponses.get(3).getPrice(), h2Repository.findAll().get(3).getPrice()),
                () -> assertEquals(productResponses.get(3).getQuantity(), h2Repository.findAll().get(3).getQuantity()),

                //product 4
                () -> assertNotNull(productResponses.get(4)),
                () -> assertEquals(productResponses.get(4).getProductId(), h2Repository.findAll().get(4).getProductId()),
                () -> assertEquals(productResponses.get(4).getProductName(), h2Repository.findAll().get(4).getProductName()),
                () -> assertEquals(productResponses.get(4).getProductDescription(), h2Repository.findAll().get(4).getProductDescription()),
                () -> assertEquals(productResponses.get(4).getProductType(), h2Repository.findAll().get(4).getProductType()),
                () -> assertEquals(productResponses.get(4).getPrice(), h2Repository.findAll().get(4).getPrice()),
                () -> assertEquals(productResponses.get(4).getQuantity(), h2Repository.findAll().get(4).getQuantity())
        );
    }

    //There are two ways getForObject or getForEntity
    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO PRODUCT (product_id, product_name, product_description, product_type, price, quantity) VALUES " +
            "(1,'iPhone X','Manufactured by Apple','phone',1499.99,6), " +
            "(2,'Galaxy S10','Manufactured by Samsung','phone',1299.99,3), " +
            "(3,'Pixel 5','Manufactured by Google','phone',1099.99,4), " +
            "(4,'Dell XPS 15','Manufactured by Dell','laptop',1799.99,6), " +
            "(5,'HP Envy 13','Manufactured by HP','laptop',1299.99,2), " +
            "(6,'Lenovo IdeaCentre 5i Gaming Desktop','Manufactured by Lenovo','desktop',999.99,6)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProductById() {
        baseUrl = baseUrl.concat(URIConstant.GET_BY_ID);
        ProductResponse productResponse = restTemplate.getForObject(baseUrl, ProductResponse.class, 3);
        assertAll(
                () -> assertNotNull(productResponse),
                () -> assertEquals(productResponse.getProductId(), h2Repository.findAll().get(2).getProductId()),
                () -> assertEquals(productResponse.getProductName(), h2Repository.findAll().get(2).getProductName()),
                () -> assertEquals(productResponse.getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
                () -> assertEquals(productResponse.getProductType(), h2Repository.findAll().get(2).getProductType()),
                () -> assertEquals(productResponse.getPrice(), h2Repository.findAll().get(2).getPrice()),
                () -> assertEquals(productResponse.getQuantity(), h2Repository.findAll().get(2).getQuantity())
        );
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "INSERT INTO PRODUCT (product_id, product_name, product_description, product_type, price, quantity) VALUES " +
            "(1,'iPhone X','Manufactured by Apple','phone',1499.99,6), " +
            "(2,'Galaxy S10','Manufactured by Samsung','phone',1299.99,3), " +
            "(3,'Pixel 5','Manufactured by Google','phone',1099.99,4), " +
            "(4,'Dell XPS 15','Manufactured by Dell','laptop',1799.99,6), " +
            "(5,'HP Envy 13','Manufactured by HP','laptop',1299.99,2), " +
            "(6,'Lenovo IdeaCentre 5i Gaming Desktop','Manufactured by Lenovo','desktop',999.99,6)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProductById_ResponseEntity() {
        ResponseEntity<ProductResponse> productResponse = restTemplate.getForEntity(baseUrl + "/api/product/3", ProductResponse.class);
        assertAll(
                () -> assertNotNull(productResponse),
                () -> assertNotNull(productResponse.getBody()),
                () -> assertEquals(productResponse.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(productResponse.getBody().getProductId(), h2Repository.findAll().get(2).getProductId()),
                () -> assertEquals(productResponse.getBody().getProductName(), h2Repository.findAll().get(2).getProductName()),
                () -> assertEquals(productResponse.getBody().getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
                () -> assertEquals(productResponse.getBody().getProductType(), h2Repository.findAll().get(2).getProductType()),
                () -> assertEquals(productResponse.getBody().getPrice(), h2Repository.findAll().get(2).getPrice()),
                () -> assertEquals(productResponse.getBody().getQuantity(), h2Repository.findAll().get(2).getQuantity())
        );
    }

    //convert JSON to Object using ObjectMapper
    private List<ProductResponse> convertJsonToObject(List<ProductResponse> productResponses) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(productResponses, new TypeReference<>() {});
    }

    //convert JSON to Object using ObjectMapper
    private String convertObjectToJson(ProductRequest productRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(productRequest);
    }
}

