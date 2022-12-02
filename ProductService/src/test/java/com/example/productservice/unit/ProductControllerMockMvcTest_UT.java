package com.example.productservice.unit;

import com.example.productservice.constant.URIConstant;
import com.example.productservice.model.ProductRequest;
import com.example.productservice.model.ProductResponse;
import com.example.productservice.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerMockMvcTest_UT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceImpl productService;

    @Test
    public void testGetProducts() throws Exception {
        //Setup
        List<ProductResponse> productResponses = generateListProducts();

        //Mock
        when(productService.getProducts()).thenReturn(productResponses);

        //Create RequestBuilder
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URIConstant.GET).contentType(MediaType.APPLICATION_JSON);

        //Send GET request
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Validate
        MvcResult result = resultActions.andDo(print())
                .andExpect(header().string(CONTENT_TYPE, "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String expected = "[{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}," +
                "{\"productId\":2,\"productName\":\"Galaxy S10\",\"productDescription\":\"Manufactured by Samsung\",\"productType\":\"phone\",\"price\":1299.99,\"quantity\":3}," +
                "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}," +
                "{\"productId\":4,\"productName\":\"Dell XPS 15\",\"productDescription\":\"Manufactured by Dell\",\"productType\":\"laptop\",\"price\":1799.99,\"quantity\":6}," +
                "{\"productId\":5,\"productName\":\"HP Envy 13\",\"productDescription\":\"Manufactured by HP\",\"productType\":\"laptop\",\"price\":1299.99,\"quantity\":2}," +
                "{\"productId\":6,\"productName\":\"Lenovo IdeaCentre 5i Gaming Desktop\",\"productDescription\":\"Manufactured by Lenovo\",\"productType\":\"desktop\",\"price\":999.99,\"quantity\":6}]";
        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    public void testGetProductById() throws Exception {
        //Setup
        ProductResponse productResponse = new ProductResponse(1,"iPhone X",
                "Manufactured by Apple","phone",1499.99,6);

        //Mock
        when(productService.getProductById(1)).thenReturn(productResponse);

        //Create RequestBuilder
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(URIConstant.GET_BY_ID, 1).contentType(MediaType.APPLICATION_JSON);

        //Send GET request
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Validate
        MvcResult result = resultActions.andDo(print())
                .andExpect(header().string(CONTENT_TYPE, "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String expected = "{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}";
        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    public void testAddProduct() throws Exception {
        //Setup
        ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,6);

        //Mock
        when(productService.addProduct(productRequest)).thenReturn(1L);

        //Create RequestBuilder
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URIConstant.POST)
                .content(asJsonString(productRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Send GET request
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Validate
        MvcResult result = resultActions.andDo(print())
                .andExpect(header().string(CONTENT_TYPE, "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        assertEquals(String.valueOf(1), result.getResponse().getContentAsString());
    }


    private List<ProductResponse> generateListProducts() {
        List<ProductResponse> productResponses = new ArrayList<>();
        productResponses.add(new ProductResponse(1,"iPhone X",
                "Manufactured by Apple","phone",1499.99,6));
        productResponses.add(new ProductResponse(2,"Galaxy S10",
                "Manufactured by Samsung","phone",1299.99,3));
        productResponses.add(new ProductResponse(3,"Pixel 5",
                "Manufactured by Google","phone",1099.99,4));
        productResponses.add(new ProductResponse(4,"Dell XPS 15",
                "Manufactured by Dell","laptop",1799.99,6));
        productResponses.add(new ProductResponse(5,"HP Envy 13",
                "Manufactured by HP","laptop",1299.99,2));
        productResponses.add(new ProductResponse(6,"Lenovo IdeaCentre 5i Gaming Desktop",
                "Manufactured by Lenovo","desktop",999.99,6));
        return productResponses;
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
