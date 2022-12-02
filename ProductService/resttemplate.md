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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //Reset test context after each test method
public class ProductControllerRestTemplateTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository h2Repository;

    private HttpHeaders headers;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        headers = new HttpHeaders();
        baseUrl = baseUrl.concat(":").concat(port + "");
    }
}
```
- Setup JPA
```java
public interface TestH2Repository extends JpaRepository<Product, Long> {
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
public void testGetProducts() {
    baseUrl = baseUrl.concat(URIConstant.GET);
    List<ProductResponse> productResponses = convertJsonToObject(restTemplate.getForObject(baseUrl, List.class));
    assertNotNull(productResponses);
    assertEquals(6, productResponses.size());    
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
public void testGetProductById() {
    baseUrl = baseUrl.concat(URIConstant.GET_BY_ID);
    ProductResponse productResponse = restTemplate.getForObject(baseUrl, ProductResponse.class, 3);
    assertAll(
            () -> assertNotNull(productResponse),
            () -> assertEquals(productResponse.getProductId(), h2Repository.findAll().get(2).getProductId()),
            () -> assertEquals(productResponse.getProductName(), h2Repository.findAll().get(2).getProductName()),
            () -> assertEquals(productResponse.getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
            () -> assertEquals(productResponse.getProductType(), h2Repository.findAll().get(2).getProductType()),
            () -> assertEquals(productResponse.getPrice(), h2Repository.findAll().get(2).getPrice()),
            () -> assertEquals(productResponse.getQuantity(), h2Repository.findAll().get(2).getQuantity())
    );
}
    
//OR

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
public void testGetProductById_ResponseEntity() {
    ResponseEntity<ProductResponse> productResponse = restTemplate.getForEntity(baseUrl + "/api/product/3", ProductResponse.class);
    assertAll(
            () -> assertNotNull(productResponse),
            () -> assertNotNull(productResponse.getBody()),
            () -> assertEquals(productResponse.getStatusCode(), HttpStatus.OK),
            () -> assertEquals(productResponse.getBody().getProductId(), h2Repository.findAll().get(2).getProductId()),
            () -> assertEquals(productResponse.getBody().getProductName(), h2Repository.findAll().get(2).getProductName()),
            () -> assertEquals(productResponse.getBody().getProductDescription(), h2Repository.findAll().get(2).getProductDescription()),
            () -> assertEquals(productResponse.getBody().getProductType(), h2Repository.findAll().get(2).getProductType()),
            () -> assertEquals(productResponse.getBody().getPrice(), h2Repository.findAll().get(2).getPrice()),
            () -> assertEquals(productResponse.getBody().getQuantity(), h2Repository.findAll().get(2).getQuantity())
    );
}
```
- Test post a product
```java
@Test
@Sql(statements = "CREATE TABLE IF NOT EXISTS PRODUCT(product_id BIGINT PRIMARY KEY, product_name VARCHAR(255), " +
        "product_description VARCHAR(255), product_type VARCHAR(255), price DOUBLE, quantity BIGINT)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DROP TABLE PRODUCT", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public void testAddProduct() {
    baseUrl = baseUrl.concat(URIConstant.POST);
    ProductRequest productRequest = new ProductRequest("iPhone X","Manufactured by Apple","phone",1499.99,6);
    long productId = restTemplate.postForObject(baseUrl, productRequest, Long.class);
    assertEquals(1, productId);
    assertAll(
        () -> assertNotNull(h2Repository.findAll().get(0)),
        () -> assertEquals(1, h2Repository.findAll().get(0).getProductId()),
        () -> assertEquals("iPhone X", h2Repository.findAll().get(0).getProductName()),
        () -> assertEquals("Manufactured by Apple", h2Repository.findAll().get(0).getProductDescription()),
        () -> assertEquals("phone", h2Repository.findAll().get(0).getProductType()),
        () -> assertEquals(1499.99, h2Repository.findAll().get(0).getPrice()),
        () -> assertEquals(6, h2Repository.findAll().get(0).getQuantity())
    );
}
```

