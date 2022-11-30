package com.example.productservice.component;

import com.example.productservice.model.ProductRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.util.StreamUtils.copyToString;

public class ProductControllerOkHttpWIthWireMockTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8080));
    private int port = 8080;
    private String baseUrl = "http://localhost";
    private OkHttpClient client;

    @BeforeEach
    public void setUp() {
        client = new OkHttpClient();
    }

    @Test
    public void testFindProductById() throws IOException {
        // Arrange - setup wiremock stubs
        configureStubs();

        baseUrl = baseUrl.concat(":").concat(port + "").concat("/api/product/3");
        Request request = new Request.Builder()
                .url(baseUrl)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String expected = "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}";
        assertEquals(expected, response.body().string());
    }

    // configure stubs for wiremock
    private void configureStubs() throws IOException {
        configureFor("localhost", port);
        stubFor(get(urlEqualTo("/api/product/3"))
                .willReturn(aResponse()
//                        .withStatus(HttpStatus.OK.value())
//                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}")));
    }
}
//https://www.baeldung.com/okhttp-post
