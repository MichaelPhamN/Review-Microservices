

# Review-Microservices (Test)
 
Step 1: Needed Dependencies
1. Spring Data JPA
2. H2 Database

Step 2: Add additional dependencies to pom.xml
```xml
<!-- the below dependencies are for testing -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-all</artifactId>
    <version>1.10.19</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock</artifactId>
    <version>2.27.2</version>
    <scope>test</scope>
</dependency>
```
Step 3: Setup application.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true  
  jpa:
    show-sql: true
    properties:
      hibernate.format_sql: true
```

Step 4: Integration testing with RestTemplate
- Setup test context
```java
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
```
- Setup test environment
```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ProductServiceApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ProductControllerMockMvcTest {
    @Autowired
    @Qualifier("mockMvc")
    private MockMvc mockMvc;

}
```
- Test get products
```java
@Test
@Sql(scripts= "/scripts/schema.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts= "/scripts/insert.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts= "/scripts/delete.sql", executionPhase= Sql.ExecutionPhase.AFTER_TEST_METHOD)
public void testGetProducts() throws Exception {
    //Create RequestBuilder
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get(URIConstant.GET)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    //Create RequestBuilder
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
```
- Test get product by Id
```java
@Test
@Sql(scripts= "/scripts/schema.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts= "/scripts/insert.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts= "/scripts/delete.sql", executionPhase= Sql.ExecutionPhase.AFTER_TEST_METHOD)
public void testGetProductById() throws Exception {
    //Create RequestBuilder
    RequestBuilder requestBuilder = MockMvcRequestBuilders
    .get(URIConstant.GET_BY_ID, 1)
    .contentType(MediaType.APPLICATION_JSON)
    .accept(MediaType.APPLICATION_JSON);

    //Create RequestBuilder
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
```
- Test post a product
```java
@Test
@Sql(scripts= "/scripts/schema.sql", executionPhase= Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts= "/scripts/delete.sql", executionPhase= Sql.ExecutionPhase.AFTER_TEST_METHOD)
public void testAddProduct() throws Exception{
    RequestBuilder requestBuilder = MockMvcRequestBuilders
    .post(URIConstant.POST)
    .contentType(MediaType.APPLICATION_JSON)
    .accept(MediaType.APPLICATION_JSON)
    .content("{ \"name\":\"Lenovo IdeaPad 3\",\"description\":\"Manufactured by Lenovo\",\"type\":\"phone\",\"price\":1699.99,\"quantity\":3}");
    MvcResult result = mockMvc.perform(requestBuilder)
    .andExpect(status().isCreated())
    .andDo(print())
    .andReturn();

    Assertions.assertEquals(String.valueOf(1), result.getResponse().getContentAsString());
}
```

Step 5: component test with MockMvc (Throw Exception / Red Path)

- Test get product by Id
```java
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
```
- Test post a product
```java
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
```