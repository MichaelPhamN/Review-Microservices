tha# Review-Microservices (Test)
 
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

Step 4: component test with HttpClient

1. Without asynchronous

- Setup test environment
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Reset test context after each test method
public class ProductControllerRestTemplateTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    @BeforeEach
    public void setUp() {
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
```
- Test post a product
```java
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
```
- Convert Object to Json
```java
private String convertObjectToJson(ProductRequest productRequest) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(productRequest);
}
```

2. With asynchronous

- Setup test environment
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Reset test context after each test method
public class ProductControllerRestTemplateTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    @BeforeEach
    public void setUp() {
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
public void testGetProducts() throws IOException, URISyntaxException, InterruptedException, ExecutionException {
    baseUrl = baseUrl.concat(URIConstant.GET);
    HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI(baseUrl))
    .GET()
    .build();
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    CompletableFuture<HttpResponse<String>> response1 = HttpClient
    .newBuilder()
    .executor(executorService)
    .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());


    CompletableFuture<HttpResponse<String>> response2 = HttpClient
    .newBuilder()
    .executor(executorService)
    .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());

    CompletableFuture<HttpResponse<String>> response3 = HttpClient
    .newBuilder()
    .executor(executorService)
    .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());

    CompletableFuture.allOf(response1, response2, response3).join();

    String expected = "[{\"productId\":1,\"productName\":\"iPhone X\",\"productDescription\":\"Manufactured by Apple\",\"productType\":\"phone\",\"price\":1499.99,\"quantity\":6}," +
    "{\"productId\":2,\"productName\":\"Galaxy S10\",\"productDescription\":\"Manufactured by Samsung\",\"productType\":\"phone\",\"price\":1299.99,\"quantity\":3}," +
    "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}," +
    "{\"productId\":4,\"productName\":\"Dell XPS 15\",\"productDescription\":\"Manufactured by Dell\",\"productType\":\"laptop\",\"price\":1799.99,\"quantity\":6}," +
    "{\"productId\":5,\"productName\":\"HP Envy 13\",\"productDescription\":\"Manufactured by HP\",\"productType\":\"laptop\",\"price\":1299.99,\"quantity\":2}," +
    "{\"productId\":6,\"productName\":\"Lenovo IdeaCentre 5i Gaming Desktop\",\"productDescription\":\"Manufactured by Lenovo\",\"productType\":\"desktop\",\"price\":999.99,\"quantity\":6}]";

    assertEquals(HttpStatus.OK.value(), response1.get().statusCode());
    assertEquals(HttpStatus.OK.value(), response2.get().statusCode());
    assertEquals(HttpStatus.OK.value(), response3.get().statusCode());
    assertEquals(expected, response1.get().body());
    assertEquals(expected, response2.get().body());
    assertEquals(expected, response3.get().body());
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
public void testGetProductById() throws IOException, URISyntaxException, InterruptedException, ExecutionException {
    baseUrl = baseUrl.concat("/api/product/3");
    HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI(baseUrl))
    .GET()
    .build();
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    CompletableFuture<HttpResponse<String>> response1 = HttpClient
    .newBuilder()
    .executor(executorService)
    .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());


    CompletableFuture<HttpResponse<String>> response2 = HttpClient
    .newBuilder()
    .executor(executorService)
    .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());

    CompletableFuture<HttpResponse<String>> response3 = HttpClient
    .newBuilder()
    .executor(executorService)
    .build().sendAsync(request, HttpResponse.BodyHandlers.ofString());

    CompletableFuture.allOf(response1, response2, response3).join();

    String expected = "{\"productId\":3,\"productName\":\"Pixel 5\",\"productDescription\":\"Manufactured by Google\",\"productType\":\"phone\",\"price\":1099.99,\"quantity\":4}";

    assertEquals(HttpStatus.OK.value(), response1.get().statusCode());
    assertEquals(HttpStatus.OK.value(), response2.get().statusCode());
    assertEquals(HttpStatus.OK.value(), response3.get().statusCode());
    assertEquals(expected, response1.get().body());
    assertEquals(expected, response2.get().body());
    assertEquals(expected, response3.get().body());
}
```
- Test post a product
```java
@Test
@Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
        "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public void testAddProductCompletableFutureV1() throws IOException, URISyntaxException {
    baseUrl = baseUrl.concat(URIConstant.POST);

    ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,6);
    HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI(baseUrl))
    .header("Content-Type" , "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(convertObjectToJson(productRequest)))
    .build();
    CompletableFuture<HttpResponse<String>>
    completableFuture = HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());

    completableFuture.thenApplyAsync(HttpResponse::headers).thenAcceptAsync(System.out::println);

    HttpResponse<String> response = completableFuture.join();

    assertEquals(String.valueOf(1), response.body());
}
        
//OR

@Test
@Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
        "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public void testAddProductCompletableFutureV2() throws IOException, URISyntaxException, ExecutionException, InterruptedException {
    baseUrl = baseUrl.concat(URIConstant.POST);

    ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,6);
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

    assertEquals(String.valueOf(1), response.get().body());
}
```
- Convert Object to Json
```java
private String convertObjectToJson(ProductRequest productRequest) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(productRequest);
}
```

Step 5: component test with HttpClient (Throw Exception / Red Path) without asynchronous

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
    HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI(baseUrl))
    .GET()
    .build();
    HttpResponse<String> response = HttpClient.newBuilder()
    .build()
    .send(request, HttpResponse.BodyHandlers.ofString());

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    assertEquals("Product data is invalid.", getMessage(response.body()));
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
    HttpRequest request = HttpRequest.newBuilder()
    .uri(new URI(baseUrl))
    .header("Content-Type" , "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(convertObjectToJson(productRequest)))
    .build();
    HttpResponse<String> response = HttpClient.newBuilder()
    .build()
    .send(request, HttpResponse.BodyHandlers.ofString());

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    assertEquals("Product data is invalid.", getMessage(response.body()));
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

Step 6: component test with HttpClient (Throw Exception / Red Path) with asynchronous

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
```
- Test post a product
```java
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
```