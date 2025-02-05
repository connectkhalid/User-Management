package com.khalid.anwargroup.exception;

public class AccountServiceException extends CustomGenericException{
    public AccountServiceException(String errorMessage, int httpStatus, String errorDetail) {
        super(errorMessage, httpStatus, errorDetail);
    }
}
