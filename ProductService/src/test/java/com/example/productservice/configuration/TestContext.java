package com.example.productservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class TestContext {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Bean
    @Qualifier("mockMvc")
    public MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }
}
