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
- Setup test environment
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductControllerOkHttpTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private OkHttpClient client;

    @BeforeEach
    public void setUp() {
        client = new OkHttpClient();
        baseUrl = baseUrl.concat(":").concat(port + "");
    }
}
```
- Test get products
```java
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
public void testGetProducts() throws IOException {
    baseUrl = baseUrl.concat(URIConstant.GET);
    Request request = new Request.Builder()
    .url(baseUrl)
    .get()
    .build();

    Response response = client.newCall(request).execute();

    String expected = "[{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}," +
    "{\"productId\":2,\"productName\":\"Galaxy S10\",\"productDescription\":\"Manufactured by Samsung\",\"productType\":\"phone\",\"price\":1299.99,\"quantity\":3}," +
    "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}," +
    "{\"productId\":4,\"productName\":\"Dell XPS 15\",\"productDescription\":\"Manufactured by Dell\",\"productType\":\"laptop\",\"price\":1799.99,\"quantity\":6}," +
    "{\"productId\":5,\"productName\":\"HP Envy 13\",\"productDescription\":\"Manufactured by HP\",\"productType\":\"laptop\",\"price\":1299.99,\"quantity\":2}," +
    "{\"productId\":6,\"productName\":\"Lenovo IdeaCentre 5i Gaming Desktop\",\"productDescription\":\"Manufactured by Lenovo\",\"productType\":\"desktop\",\"price\":999.99,\"quantity\":6}]";
    assertEquals(expected, response.body().string());
}
```
- Test get product by Id
```java
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
public void testGetProductById() throws IOException {
    baseUrl = baseUrl.concat("/api/product/3");
    Request request = new Request.Builder()
    .url(baseUrl)
    .get()
    .build();

    Response response = client.newCall(request).execute();

    String expected = "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}";
    assertEquals(expected, response.body().string());
}
```
- Test post a product
```java
@Test
@Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
        "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public void testAddProduct() throws IOException {
    baseUrl = baseUrl.concat(URIConstant.POST);

    ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,6);
    RequestBody body = RequestBody.create(
    MediaType.parse("application/json"), convertObjectToJson(productRequest));

    Request request = new Request.Builder()
    .url(baseUrl)
    .post(body)
    .build();

    Response response = client.newCall(request).execute();
    assertEquals(String.valueOf(1), response.body().string());
}
```
- convert object to json
```java
private String convertObjectToJson(ProductRequest productRequest) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(productRequest);
}
```
Step 5: component test with OkHttp (Throw Exception / Red Path)

- Test get product by Id
```java
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
```
- Test post a product
```java
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
```
