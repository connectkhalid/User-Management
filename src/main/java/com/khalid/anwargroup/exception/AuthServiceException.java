package com.khalid.anwargroup.exception;

public class AuthServiceException extends CustomGenericException{

    public AuthServiceException(String errorMessage, int httpStatus, String errorDetail) {
        super(errorMessage, httpStatus, errorDetail);
    }
}
