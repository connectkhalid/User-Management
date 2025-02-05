package com.khalid.anwargroup.constant;

public class RestResponseMessage {
    public static final String UNKNOWN_ERROR = "ERR_UNKNOWN";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    // Entity Not Found
    public static final String USER_NOT_FOUND = "INFO_NO_USER";
    public static final String ROLE_NOT_FOUND = "INFO_NO_ROLE";
    public static final String AUTH_DATA_NOT_FOUND = "INFO_NO_AUTH_DATA";

    // Operation Success
    public static final String CREATE_OK = "SUCCESSFULLY_CREATED";
    public static final String UPDATE_OK = "SUCCESSFULLY_UPDATED";
    public static final String FETCH_OK = "SUCCESSFULLY_FETCHED";

    // Auth Service
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String EXPIRED_TOKEN = "EXPIRED_TOKEN";
    public static final String WRONG_CREDENTIALS = "WRONG_CREDENTIALS";
    public static final String NOT_FOUND = "DATA_NOT_FOUND";
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String REFRESH_SUCCESS = "REFRESH_SUCCESS";
    public static final String LOGOUT_SUCCESS = "LOGOUT_SUCCESS";
    public static final String NOT_LOGGED_IN = "NOT_LOGGED_IN";

    public static final String ACCESS_DENIED = "Access is denied";
    public static final String ROLE_MISMATCH = "USER_ROLE_CHANGED";
    public static final String BAD_REQUEST = "Can Not execute this request";
}
