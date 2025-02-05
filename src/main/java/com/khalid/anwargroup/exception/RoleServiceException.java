package com.khalid.anwargroup.exception;

public class RoleServiceException extends CustomGenericException{
    public RoleServiceException(String errorMessage, int httpStatus, String errorDetail) {
        super(errorMessage, httpStatus, errorDetail);
    }
}