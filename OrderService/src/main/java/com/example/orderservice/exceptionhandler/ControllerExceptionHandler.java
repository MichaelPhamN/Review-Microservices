package com.example.orderservice.exceptionhandler;


import com.example.orderservice.exception.BadGateway502Exception;
import com.example.orderservice.exception.BadRequest400Exception;
import com.example.orderservice.exception.ErrorMessage;
import com.example.orderservice.exception.Forbidden403Exception;
import com.example.orderservice.exception.GatewayTimeout504Exception;
import com.example.orderservice.exception.InternalServerError500Exception;
import com.example.orderservice.exception.NotFound404Exception;
import com.example.orderservice.exception.ServiceUnavailable503Exception;
import com.example.orderservice.exception.UnAuthorized401Exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

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
