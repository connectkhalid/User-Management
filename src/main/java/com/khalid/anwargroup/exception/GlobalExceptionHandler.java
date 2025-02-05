package com.khalid.anwargroup.exception;

import com.khalid.anwargroup.constant.RestApiResponse;
import com.khalid.anwargroup.constant.RestResponseStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            AccountServiceException.class, AuthServiceException.class,RoleServiceException.class})
    public ResponseEntity<Object> handleCustomExceptions(CustomGenericException ex) {
        return RestApiResponse.buildResponseWithError(ex.getErrorMessage(), ex.getHttpStatus(), ex.getErrorDetail());
    }

    @ExceptionHandler({
            NullPointerException.class, NumberFormatException.class, IllegalArgumentException.class,
            IllegalStateException.class, RuntimeException.class, ClassCastException.class,
            ClassNotFoundException.class, OutOfMemoryError.class, StackOverflowError.class,
            IOException.class, SQLException.class, BadCredentialsException.class
    })
    public ResponseEntity<Object> handleCommonExceptions(Exception ex) {
        return RestApiResponse.buildResponseWithError(
                "An error occurred while processing your request.",
                RestResponseStatusCode.BAD_REQUEST_STATUS,
                ex.getClass().getSimpleName() + ": " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception ex) {
        return RestApiResponse.buildResponseWithError(
                "An unknown error occurred.",
                RestResponseStatusCode.INTERNAL_ERROR_STATUS,
                ex.getClass().getSimpleName() + ": " + ex.getMessage());
    }
}