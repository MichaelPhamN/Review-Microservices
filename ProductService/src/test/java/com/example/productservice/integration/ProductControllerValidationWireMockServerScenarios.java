package com.example.productservice.integration;

import com.example.productservice.model.ProductResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class ProductControllerValidationWireMockServerScenarios {
    private int testCase;
    private String description;
    private long productId;
    private ProductResponse productResponse;
    private ProductResponse expectedProductResponse;

    private WireMockServer wireMockServer;
    private int port = 8080;
    private String baseUrl = "http://localhost";
    private CloseableHttpClient client;

    @Before
    public void setUp() {
        initMocks(this);
        wireMockServer = new WireMockServer();
        client = HttpClients.createDefault();
    }


    public ProductControllerValidationWireMockServerScenarios(int testCase,
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
    public void shouldReturnExpectedProductResponse() throws IOException {
        wireMockServer.start();

        // Arrange - setup wiremock stubs
        configureStubs();

        baseUrl = baseUrl.concat(":").concat(port + "").concat("/api/product/3");
        HttpGet request = new HttpGet(baseUrl);
        HttpResponse httpResponse = client.execute(request);

        String stringResponse = convertResponseToString(httpResponse);

        String expected = "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}";
        Assertions.assertEquals(expected, stringResponse);

        wireMockServer.stop();
    }

    // configure stubs for wiremock
    private void configureStubs() throws IOException {
        configureFor("localhost", port);
        stubFor(get(urlEqualTo("/api/product/3"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}")));
    }

    private String convertResponseToString(HttpResponse response) throws IOException {
        InputStream inputStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String stringResponse = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return stringResponse;
    }
}
