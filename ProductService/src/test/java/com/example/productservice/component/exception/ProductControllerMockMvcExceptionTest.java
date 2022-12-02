package com.example.productservice.component.exception;

import com.example.productservice.ProductServiceApplication;
import com.example.productservice.constant.URIConstant;
import com.example.productservice.exception.BadRequest400Exception;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProductServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ProductControllerMockMvcExceptionTest {
    @Autowired
    @Qualifier("mockMvc")
    private MockMvc mockMvc;

    @Test
    @Sql(scripts= "/scripts/schema.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts= "/scripts/insert.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts= "/scripts/delete.sql", executionPhase= Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetProductById() throws Exception {
        //Create RequestBuilder
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(URIConstant.GET_BY_ID, -3)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        //Create RequestBuilder
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Validate
        resultActions.andExpect(header().string(CONTENT_TYPE, "application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequest400Exception))
                .andExpect(result -> assertEquals("Product data is invalid.", result.getResolvedException().getMessage()));
    }


    @Test
    @Sql(scripts= "/scripts/schema.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts= "/scripts/delete.sql", executionPhase= Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddProduct() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URIConstant.POST)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{ \"name\":\"Lenovo IdeaPad 3\",\"description\":\"Manufactured by Lenovo\",\"type\":\"phone\",\"price\":1699.99,\"quantity\":-1}");

        //Create RequestBuilder
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        //Validate
        resultActions.andExpect(header().string(CONTENT_TYPE, "application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequest400Exception))
                .andExpect(result -> assertEquals("Product data is invalid.", result.getResolvedException().getMessage()));;
    }

}

