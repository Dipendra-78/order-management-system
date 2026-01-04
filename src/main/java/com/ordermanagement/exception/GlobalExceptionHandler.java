package com.ordermanagement.exception;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> usernameNotFound(UserNotFoundException e) {
        ApiErrorResponse errorResponse= new ApiErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
       
    }

    

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> invalidCredentials(InvalidCredentialsException e)
    {
        ApiErrorResponse errorResponse= new ApiErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> orderNotFound(OrderNotFoundException e)
    {
        ApiErrorResponse errorResponse= new ApiErrorResponse(e.getMessage(), 
        HttpStatus.NOT_FOUND.value(), 
    LocalDateTime.now());

    return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ApiErrorResponse> invalidOrderStatus(InvalidOrderStateException e)
    {
        ApiErrorResponse errorResponse= new ApiErrorResponse(e.getMessage(), 
        HttpStatus.CONFLICT.value(), 
    LocalDateTime.now());

    return new ResponseEntity<>(errorResponse,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> illegalArugument(IllegalArgumentException e)
    {
        ApiErrorResponse errorResponse= new ApiErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

}
