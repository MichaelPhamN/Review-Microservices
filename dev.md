# Review-Microservices

## Services
1. Cloud-Gateway (Authentication and Authorization)
2. Order-Service (Order Service)
3. Payment-Service (Payment Service)
4. Product-Service (Product Service)
5. Service-Registry (Monitoring Services)
6. Config-Server (Providing A Centralized Way To Configure Eureka Client)


## Stage 1: Create services (product, payment, order) to connect to database using JPA technique 
Step 1: Needed Dependencies
1. Lombok
2. Spring Web
3. MySQL Driver
4. Spring Data JPA
5. H2 Database

Step 2: Add additional dependencies to pom.xml
```xml
<!-- hibernate-core, hibernate-entitymanager, and spring-orm  help us to focus on the code first -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-entitymanager</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
</dependency>
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
Step 3: Create packages
- Controller 
- Service -> Impl    
- Repository
- Entity
- Model
- Constant
- Exception
- ExceptionHandler
- Constant

Step 4: Setup application.yml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:3306/productdb
    username: root
    password: 123456
    driverClassName: com.mysql.cj.jdbc.Driver
  jpa:
    database-platform: org.hibernate.dialect.MySQL57InnoDBDialect
    hibernate:
      ddl-auto: update
    open-in-view: true
  application:
    name: PRODUCT-SERVICE
```

### Product Service
#### Expose API
- Get All Products -> /api/product 
- Get Product By Id -> /api/product/{id} 
- Post A Product -> /api/product 
- Put A Product -> /api/product 
- Delete A Product By Id-> /api/product

#### Some error codes that we should remember 
##### Successful responses (200 - 299)
- HTTP_200_OK = "success status response code indicates that the request has succeeded";
- HTTP_201_CREATED = "success status response code indicates that the request has succeeded and has led to the creation of a resource";
- HTTP_202_ACCEPTED = "accepted response status code indicates that the request has been accepted for processing, but the processing has not been completed";       
- HTTP_204_NO_CONTENT = "no content success status response code indicates that a request has succeeded, but that the client doesn't need to navigate away from its current page"; 
##### Client error responses (400 - 499)
- HTTP_400_BAD_REQUEST = "bad request response status code indicates that the server cannot or will not process the request due to something that is perceived to be a client error";
- HTTP_401_UNAUTHORIZED = "unauthorized response status code indicates that the client request has not been completed because it lacks valid authentication credentials for the requested resource";       
- HTTP_403_FORBIDDEN = "forbidden response status code indicates that the server understands the request but refuses to authorize it";
- HTTP_404_NOT_FOUND = "not found response status code indicates that the server cannot find the requested resource";
- HTTP_405_METHOD_NOT_ALLOWED = "method not allowed response status code indicates that the server knows the request method, but the target resource doesn't support this method";       
##### Server error responses (500 - 599)
- HTTP_500_INTERNAL_SERVER_ERROR = "internal server error response code indicates that the server encountered an unexpected condition that prevented it from fulfilling the request";
- HTTP_502_BAD_GATEWAY = "bad gateway server error response code indicates that the server, while acting as a gateway or proxy, received an invalid response from the upstream server";
- HTTP_503_SERVICE_UNAVAILABLE = "service unavailable server error response code indicates that the server is not ready to handle the request";
- HTTP_504_GATEWAY_TIMEOUT = "gateway timeout server error response code indicates that the server, while acting as a gateway or proxy, did not get a response in time from the upstream server that it needed in order to complete the request";


#### Create URIConstant class in constant package
```java
public class URIConstant {
    //getProducts
    public static final String GET = "/api/product";
    //getProductById
    public static final String GET_BY_ID = "/api/product/{id}";    
    //addProduct
    public static final String POST = "/api/product";    
    //editProduct
    public static final String PUT = "/api/product";
    //deleteProductById
    public static final String DELETE_BY_ID = "/api/product/{id}";    
}
```
#### Create exceptions (400, 401, 403, 404, 500, 502, 503, 504) in exception package
- Create ErrorMessage class
```java
public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;
    
    //constructor
    
    //getter
    
    // setter    
}
```
- Create BadRequest400Exception class  
```java
public class BadRequest400Exception extends RuntimeException{
    public BadRequest400Exception(String description) {
        super(description);
    }
}
```
- Continue for 401, 403, 404, 500, 502, 503, 504 error codes
#### Create ControllerExceptionHandler class to provide a centralized way to handle exception in exceptionhandler folder
```java
@ControllerAdvice
public class ControllerExceptionHandler {
    /*********************************************************
     *                   400 : BAD REQUEST                   *
     *********************************************************/
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequest400Exception.class)
    public ResponseEntity<ErrorMessage> BadRequestException(BadRequest400Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
    /*********************************************************
     *                   401 : Unauthorized                  *
     *********************************************************/
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorized401Exception.class)
    public ResponseEntity<ErrorMessage> UnAuthorizedException(UnAuthorized401Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }
    /*********************************************************
     *                   403 : FORBIDDEN                     *
     *  A client is forbidden from accessing a valid URL     *
     *********************************************************/
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(Forbidden403Exception.class)
    public ResponseEntity<ErrorMessage> ForbiddenException(Forbidden403Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.FORBIDDEN.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }
    /*********************************************************
    *                   404 : NOT FOUND                      *
    **********************************************************/
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFound404Exception.class)
    public ResponseEntity<ErrorMessage> NotFoundException(NotFound404Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
    /*********************************************************
     *                   500 : INTERNAL SERVER ERROR         *
     *********************************************************/
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerError500Exception.class)
    public ResponseEntity<ErrorMessage> InternalServerErrorException(InternalServerError500Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /*********************************************************
     *                   502 : BAD GATEWAY                   *
     *********************************************************/
    @ResponseStatus(code = HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(BadGateway502Exception.class)
    public ResponseEntity<ErrorMessage> BadGatewayException(BadGateway502Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_GATEWAY.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.BAD_GATEWAY);
    }
    /*********************************************************
     *                   503 : SERVICE UNAVAILABLE           *
     *********************************************************/
    @ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailable503Exception.class)
    public ResponseEntity<ErrorMessage> ServiceUnavailableException(ServiceUnavailable503Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
    /*********************************************************
     *                   504 : GATEWAY TIMEOUT               *
     *********************************************************/
    @ResponseStatus(code = HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler(GatewayTimeout504Exception.class)
    public ResponseEntity<ErrorMessage> GatewayTimeoutException(GatewayTimeout504Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.GATEWAY_TIMEOUT.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(message, HttpStatus.GATEWAY_TIMEOUT);
    }
}
```
#### Create Product class in entity package
```java
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productId;

    @Column(name = "PRODUCT_NAME")
    private String productName;
    @Column(name = "PRODUCT_DESCRIPTION")
    private String productDescription;
    @Column(name = "PRODUCT_TYPE")
    private String productType;
    @Column(name = "PRICE")
    private double price;
    @Column(name = "QUANTITY")
    private long quantity;

    public Product(String productName, String productDescription, String productType, double price, long quantity) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productType = productType;
        this.price = price;
        this.quantity = quantity;
    }
}
```
#### Create ProductRequest and ProductResponse in model package
```java
@Data
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private String description;
    private String type;
    private double price;
    private long quantity;
}
```

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private long productId;
    private String productName;
    private String productDescription;
    private String productType;
    private double price;
    private long quantity;
}
```
#### Create Product Service interface in service package
```java
public interface ProductService {
    //getProducts
    List<ProductResponse> getProducts();
    //getProductById
    ProductResponse getProductById(long productId);
    //addProduct
    long addProduct(ProductRequest productRequest);
    //editProduct
    ProductResponse editProduct(ProductRequest productRequest, long productId);
    //deleteProductById
    String deleteProductById(long productId);
}
```
#### Create Product Service Implement class which implements Product Service in service/impl package
```java
@Service
@Log4j2
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductResponse> getProducts() {
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(product -> {
                    ProductResponse response = ProductResponse.builder()
                            .productId(product.getProductId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productType(product.getProductType())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .build();
                    return response;
                }).collect(Collectors.toList());
        log.info("Getting list product {}", products);
        return products;
    }    

    @Override
    public ProductResponse getProductById(long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BadRequest400Exception("Product is not found"));
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);
        log.info("Getting product {} by product id {}", product, productId);
        return productResponse;
    }        

    @Override
    public long addProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .productName(productRequest.getName())
                .productDescription(productRequest.getDescription())
                .productType(productRequest.getType())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        log.info("Saving Product ...");
        productRepository.save(product);
        log.info("{} is created", product);
        return product.getProductId();
    }

    @Override
    public ProductResponse editProduct(ProductRequest productRequest, long productId) {
        Product updateProduct = productRepository.findById(productId).orElseThrow(() -> new BadRequest400Exception("Product is not found"));

        updateProduct.builder()
                .productId(updateProduct.getProductId())
                .productName(productRequest.getName())
                .productDescription(productRequest.getDescription())
                .productType(productRequest.getType())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();

        log.info("Editing Product ...");
        productRepository.save(updateProduct);
        log.info("{} has edited", updateProduct);

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(updateProduct, productResponse);

        return productResponse;
    }

    @Override
    public String deleteProductById(long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new BadRequest400Exception("Product is not found"));
        log.info("Deleting Product ...");
        productRepository.delete(product);
        log.info("{} has deleted", product);
        return "Delete product successful";
    }
}
```
#### Create Product Controller class in controller package
```java
@RestController
@Log4j2
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping(URIConstant.GET)
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return new ResponseEntity<>(productService.getProducts(), HttpStatus.OK);
    }

    @GetMapping(URIConstant.GET_BY_ID)
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id) {
        if(id < 0) {
            log.warn("Product Id: {} is invalid", id);
            throw new BadRequest400Exception("Product data is invalid.");
        }
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @PostMapping(URIConstant.POST)
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest) {
        if(productRequest == null
                || productRequest.getName() == null
                || productRequest.getName().length() == 0
                || productRequest.getType() == null
                || productRequest.getType().length() == 0
                || productRequest.getPrice() < 0
                || productRequest.getQuantity() < 0) {
            log.warn("Product {} is invalid", productRequest);
            throw new BadRequest400Exception("Product data is invalid.");
        }

        long productId = productService.addProduct(productRequest);
        if (productId < 1) {
            log.warn("Inserted product {} failed", productRequest);
            throw new BadRequest400Exception("Inserted product failed.");
        }

        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    @PutMapping(URIConstant.PUT)
    public ResponseEntity<ProductResponse> editProduct(@RequestBody ProductRequest productRequest, @PathVariable long productId) {
        if(productRequest == null
                || productId < 0
                || productRequest.getName() == null
                || productRequest.getName().length() == 0
                || productRequest.getType() == null
                || productRequest.getType().length() == 0
                || productRequest.getPrice() < 0
                || productRequest.getQuantity() < 0) {
            log.warn("Product {} is invalid", productRequest);
            throw new BadRequest400Exception("Product data is invalid.");
        }

        return new ResponseEntity<>(productService.editProduct(productRequest, productId), HttpStatus.OK);
    }

    @DeleteMapping(URIConstant.DELETE_BY_ID)
    public ResponseEntity<String> deleteProductById(@PathVariable long id) {
        if(id < 0) {
            log.warn("Product Id: {} is invalid", id);
            throw new BadRequest400Exception("Product data is invalid.");
        }
        return new ResponseEntity<>(productService.deleteProductById(id), HttpStatus.OK);
    }    
}
```