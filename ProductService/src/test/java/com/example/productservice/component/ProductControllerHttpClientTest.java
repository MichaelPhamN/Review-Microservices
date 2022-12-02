package com.example.productservice.component;

import com.example.productservice.constant.URIConstant;
import com.example.productservice.model.ProductRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerHttpClientTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "");
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddProduct() throws IOException, URISyntaxException, InterruptedException {
        baseUrl = baseUrl.concat(URIConstant.POST);

        ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,6);
        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(baseUrl))
                                .header("Content-Type" , "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(convertObjectToJson(productRequest)))
                                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                                .build()
                                .send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(String.valueOf(1), response.body());
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
    public void testGetProducts() throws IOException, URISyntaxException, InterruptedException {
        baseUrl = baseUrl.concat(URIConstant.GET);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        String expected = "[{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}," +
                "{\"productId\":2,\"productName\":\"Galaxy S10\",\"productDescription\":\"Manufactured by Samsung\",\"productType\":\"phone\",\"price\":1299.99,\"quantity\":3}," +
                "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}," +
                "{\"productId\":4,\"productName\":\"Dell XPS 15\",\"productDescription\":\"Manufactured by Dell\",\"productType\":\"laptop\",\"price\":1799.99,\"quantity\":6}," +
                "{\"productId\":5,\"productName\":\"HP Envy 13\",\"productDescription\":\"Manufactured by HP\",\"productType\":\"laptop\",\"price\":1299.99,\"quantity\":2}," +
                "{\"productId\":6,\"productName\":\"Lenovo IdeaCentre 5i Gaming Desktop\",\"productDescription\":\"Manufactured by Lenovo\",\"productType\":\"desktop\",\"price\":999.99,\"quantity\":6}]";
        assertEquals(expected, response.body());
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
    public void testGetProductsByPriceBetween() throws IOException, URISyntaxException, InterruptedException {
        baseUrl = baseUrl.concat(URIConstant.GET_PRODUCT_BY_PRICE + "?minPrice=1000&maxPrice=1500");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        String expected = "[{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}," +
                "{\"productId\":2,\"productName\":\"Galaxy S10\",\"productDescription\":\"Manufactured by Samsung\",\"productType\":\"phone\",\"price\":1299.99,\"quantity\":3}," +
                "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}," +
                "{\"productId\":5,\"productName\":\"HP Envy 13\",\"productDescription\":\"Manufactured by HP\",\"productType\":\"laptop\",\"price\":1299.99,\"quantity\":2}]";
        assertEquals(expected, response.body());
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
    public void testGetProductsByPriceLessThan() throws IOException, URISyntaxException, InterruptedException {
        baseUrl = baseUrl.concat(URIConstant.GET_PRODUCT_BY_PRICE_LESS_THAN + "?price=1500");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        String expected = "[{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}," +
                "{\"productId\":2,\"productName\":\"Galaxy S10\",\"productDescription\":\"Manufactured by Samsung\",\"productType\":\"phone\",\"price\":1299.99,\"quantity\":3}," +
                "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}," +
                "{\"productId\":5,\"productName\":\"HP Envy 13\",\"productDescription\":\"Manufactured by HP\",\"productType\":\"laptop\",\"price\":1299.99,\"quantity\":2}," +
                "{\"productId\":6,\"productName\":\"Lenovo IdeaCentre 5i Gaming Desktop\",\"productDescription\":\"Manufactured by Lenovo\",\"productType\":\"desktop\",\"price\":999.99,\"quantity\":6}]";
        assertEquals(expected, response.body());
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
    public void testGetProductsByPriceGreaterThan() throws IOException, URISyntaxException, InterruptedException {
        baseUrl = baseUrl.concat(URIConstant.GET_PRODUCT_BY_PRICE_GREATER_THAN + "?price=1000");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        String expected = "[{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}," +
                "{\"productId\":2,\"productName\":\"Galaxy S10\",\"productDescription\":\"Manufactured by Samsung\",\"productType\":\"phone\",\"price\":1299.99,\"quantity\":3}," +
                "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}," +
                "{\"productId\":4,\"productName\":\"Dell XPS 15\",\"productDescription\":\"Manufactured by Dell\",\"productType\":\"laptop\",\"price\":1799.99,\"quantity\":6}," +
                "{\"productId\":5,\"productName\":\"HP Envy 13\",\"productDescription\":\"Manufactured by HP\",\"productType\":\"laptop\",\"price\":1299.99,\"quantity\":2}]";
        assertEquals(expected, response.body());
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
    public void testGetProductById() throws IOException, URISyntaxException, InterruptedException {
        baseUrl = baseUrl.concat("/api/product/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        String expected = "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}";
        assertEquals(expected, response.body());
    }

    //convert Object to Json using ObjectMapper
    private String convertObjectToJson(ProductRequest productRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(productRequest);
    }
}
//https://www.baeldung.com/okhttp-post
