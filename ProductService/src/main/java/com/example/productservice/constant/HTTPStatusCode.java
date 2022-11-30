package com.example.productservice.constant;

public class HTTPStatusCode {
    //2xx
    public static final String HTTP_200_OK = "success status response code indicates that the request has succeeded";
    public static final String HTTP_201_CREATED = "success status response code indicates that the request has succeeded and has led to the creation of a resource";
    public static final String HTTP_202_ACCEPTED = "accepted response status code indicates that the request has been accepted for processing, but the processing has not been completed";
    public static final String HTTP_203_NON_AUTHORITATIVE_INFORMATION = "non-Authoritative Information response status indicates that the request was successful but the enclosed payload has been modified by a transforming proxy from that of the origin server's 200 (OK) response";
    public static final String HTTP_204_NO_CONTENT = "no content success status response code indicates that a request has succeeded, but that the client doesn't need to navigate away from its current page";
    public static final String HTTP_205_RESET_CONTENT = "reset content response status tells the client to reset the document view, so for example to clear the content of a form, reset a canvas state, or to refresh the UI";
    public static final String HTTP_206_PARTIAL_CONTENT = "partial content success status response code indicates that the request has succeeded and the body contains the requested ranges of data, as described in the Range header of the request";

    //4xx
    public static final String HTTP_400_BAD_REQUEST = "bad request response status code indicates that the server cannot or will not process the request due to something that is perceived to be a client error";
    public static final String HTTP_401_UNAUTHORIZED = "unauthorized response status code indicates that the client request has not been completed because it lacks valid authentication credentials for the requested resource";
    public static final String HTTP_402_PAYMENT_REQUIRED = "payment required is a nonstandard response status code that is reserved for future use";
    public static final String HTTP_403_FORBIDDEN = "forbidden response status code indicates that the server understands the request but refuses to authorize it";
    public static final String HTTP_404_NOT_FOUND = "not found response status code indicates that the server cannot find the requested resource";
    public static final String HTTP_405_METHOD_NOT_ALLOWED = "method not allowed response status code indicates that the server knows the request method, but the target resource doesn't support this method";
    public static final String HTTP_406_NOT_ACCEPTABLE = "not acceptable client error response code indicates that the server cannot produce a response matching the list of acceptable values defined in the request's proactive content negotiation headers, and that the server is unwilling to supply a default representation";
    public static final String HTTP_407_PROXY_AUTHENTICATION_REQUIRED = "proxy authentication required client error status response code indicates that the request has not been applied because it lacks valid authentication credentials for a proxy server that is between the browser and the server that can access the requested resource";
    public static final String HTTP_408_REQUEST_TIMEOUT = "request timeout response status code means that the server would like to shut down this unused connection";
    public static final String HTTP_409_CONFLICT = "conflict response status code indicates a request conflict with the current state of the target resource";
    public static final String HTTP_413_PAYLOAD_TOO_LARGE = "payload too large response status code indicates that the request entity is larger than limits defined by server; the server might close the connection or return a Retry-After header field";
    public static final String HTTP_414_URI_TOO_LONG = "uri too long response status code indicates that the URI requested by the client is longer than the server is willing to interpret";
    public static final String HTTP_415_UNSUPPORTED_MEDIA_TYPE = "unsupported media type client error response code indicates that the server refuses to accept the request because the payload format is in an unsupported format";

    //5xx
    public static final String HTTP_500_INTERNAL_SERVER_ERROR = "internal server error server error response code indicates that the server encountered an unexpected condition that prevented it from fulfilling the request";
    public static final String HTTP_501_NOT_IMPLEMENTED = "not implemented server error response code means that the server does not support the functionality required to fulfill the request";
    public static final String HTTP_502_BAD_GATEWAY = "bad gateway server error response code indicates that the server, while acting as a gateway or proxy, received an invalid response from the upstream server";
    public static final String HTTP_503_SERVICE_UNAVAILABLE = "service unavailable server error response code indicates that the server is not ready to handle the request";
    public static final String HTTP_504_GATEWAY_TIMEOUT = "gateway timeout server error response code indicates that the server, while acting as a gateway or proxy, did not get a response in time from the upstream server that it needed in order to complete the request";
}
/**
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
 *      - 1. Informational responses (100 - 199)
 *      - 2. Successful responses (200 - 299)
 *      - 3. Redirection messages (300 - 399)
 *      - 4. Client error responses (400 - 499)
 *      - 5. Server error responses (500 - 599)
 */

