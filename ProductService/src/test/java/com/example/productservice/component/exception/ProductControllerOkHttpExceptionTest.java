package com.example.productservice.component.exception;

import com.example.productservice.constant.URIConstant;
import com.example.productservice.model.ProductRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerOkHttpExceptionTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private OkHttpClient client;

    @BeforeEach
    public void setUp() {
        client = new OkHttpClient();
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
    public void testGetProductById() throws IOException, URISyntaxException, InterruptedException{
        baseUrl = baseUrl.concat("/api/product/-3");
        Request request = new Request.Builder()
                .url(baseUrl)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.code());
        assertEquals("Product data is invalid.", getMessage(response.body().string()));
    }

    @Test
    @Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
            "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddProduct() throws IOException, URISyntaxException, InterruptedException{
        baseUrl = baseUrl.concat(URIConstant.POST);

        ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,-1);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), convertObjectToJson(productRequest));

        Request request = new Request.Builder()
                .url(baseUrl)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.code());
        assertEquals("Product data is invalid.", getMessage(response.body().string()));
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

