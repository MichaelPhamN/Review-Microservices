package com.example.productservice.component.exception;

import com.example.productservice.constant.URIConstant;
import com.example.productservice.model.ProductRequest;
import com.example.productservice.model.ProductResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerHttpClientAsyncExceptionTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "");
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
    public void testGetProductById() throws IOException, URISyntaxException, InterruptedException, ExecutionException {
        baseUrl = baseUrl.concat("/api/product/-3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl))
                .GET()
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<HttpResponse<String>> response = HttpClient
                .newBuilder()
                .executor(executorService)
                .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.get().statusCode());
        assertEquals("Product data is invalid.", getMessage(response.get().body()));
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddProduct() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        baseUrl = baseUrl.concat(URIConstant.POST);

        ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,-1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl))
                .header("Content-Type" , "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(convertObjectToJson(productRequest)))
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<HttpResponse<String>> response = HttpClient
                .newBuilder()
                .executor(executorService)
                .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.get().statusCode());
        assertEquals("Product data is invalid.", getMessage(response.get().body()));
    }

    private String getMessage(String message) {
        int start = message.indexOf("message") + 10;
        int end = message.indexOf("description") - 3;
        return message.substring(start, end);
    }

    //convert Object to Json using ObjectMapper
    private String convertObjectToJson(ProductRequest productRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(productRequest);
    }
}

