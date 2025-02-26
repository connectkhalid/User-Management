package com.khalid.anwargroup.constant;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class RestErrorMessageDetail {
    public static final String BAD_CREDENTIALS_ERROR_MESSAGE = "Username or password is invalid.";
    public static final String NO_PERMISSION_ERROR_MESSAGE = "You do not have the permission to perform the task.";
    public static final String UNKNOWN_ERROR_MESSAGE = "Unknown exception occurred, please check log for details.";
    public static final String INVALID_USER_TOKEN_ERROR_MESSAGE = "Token is not registered for a valid user.";
    public static final String INVALID_TOKEN_ERROR_MESSAGE = "Token is not valid.";
    public static final String EXPIRED_TOKEN_ERROR_MESSAGE = "Token has expired.";
    public static final String USER_NOT_FOUND_ERROR_MESSAGE = "User does not exist.";
    public static final String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role does not exist.";
    public static final String USER_ALREADY_LOGGED_OUT_ERROR_MESSAGE = "User Already logged out.";

    public static final String NO_SUCH_ENTITY_FOUND_ERROR_MESSAGE = "No Such Entity Found.";
    public static final String SUPER_ADMIN_CREATE_ERROR_MESSAGE = "Super Admin cannot be created.";
    public static final String USER_ALREADY_DELETED = "User with the given id is already deleted";
    public static final String NO_SUCH_DATA = "No such data is found";
}
