package com.khalid.anwargroup.api.pvt;

import com.khalid.anwargroup.constant.Constants;
import com.khalid.anwargroup.constant.RestResponseMessage;
import com.khalid.anwargroup.constant.RestResponseStatusCode;
import com.khalid.anwargroup.exception.AccountServiceException;
import com.khalid.anwargroup.exception.AuthServiceException;
import com.khalid.anwargroup.exception.RoleServiceException;
import com.khalid.anwargroup.form.annotation.MailAddress;
import com.khalid.anwargroup.form.annotation.Password;
import com.khalid.anwargroup.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.khalid.anwargroup.constant.RestApiResponse.buildResponseWithDetails;

@Slf4j
@RestController
@CrossOrigin
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAccountController {
    public static final String API_PATH_ACCOUNT_REGISTER = Constants.ApiPath.PRIVATE_API_PATH + "/register-user-account";
    public static final String API_PATH_GET_USER_LIST = Constants.ApiPath.PRIVATE_API_PATH + "/get-user-list";
    public static final String API_PATH_GET_USER_INFO = Constants.ApiPath.PRIVATE_API_PATH + "/get-user-info";
    public static final String API_PATH_DELETE_USER_INFO = Constants.ApiPath.PRIVATE_API_PATH + "/delete-user";

    private final AccountService accountService;
    @Data
    public static class AccountRegisterInput {

        @NotBlank(message = "field is mandatory")
        String firstName;

        @NotBlank(message = "field is mandatory")
        String lastName;

        @NotBlank(message = "field is mandatory")
        @MailAddress(message = "field value must be email")
        String username;

        @NotBlank(message = "field is mandatory")
        @Password(message = "invalid password format")
        String password;

        @NotBlank(message = "field is mandatory")
        String phoneNumber;

        @NotBlank(message = "field is mandatory")
        String location;

        @NotNull(message = "field is mandatory")
        long roleCode;
    }

    @Data
    public static class InputUserId {
        @NotNull long userId;
    }

    @PostMapping(path = API_PATH_ACCOUNT_REGISTER,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority(@apiPermission.userCreatePermission.code)")
    public ResponseEntity<Object> accountRegister(@Valid @RequestBody AccountRegisterInput accountRegisterInput) throws AccountServiceException, RoleServiceException {
        AccountService.AccountRegisterInputParameter parameter = new AccountService.AccountRegisterInputParameter(
                accountRegisterInput.getFirstName(),
                accountRegisterInput.getLastName(),
                accountRegisterInput.getUsername(),
                accountRegisterInput.getPassword(),
                accountRegisterInput.getPhoneNumber(),
                accountRegisterInput.getLocation(),
                accountRegisterInput.getRoleCode()
        );

        AccountService.AccountResponse accountResponse = accountService.registerAccount(parameter);
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.CREATE_OK, accountResponse);
    }

    @PreAuthorize("hasAuthority(@apiPermission.userListViewPermission.code)")
    @GetMapping(
            path = API_PATH_GET_USER_LIST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getUserList() throws AccountServiceException, AuthServiceException {
        log.info("START execute.");
        List<AccountService.AccountResponse> toAccountDataResponse = accountService.getUserList();
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.FETCH_OK, toAccountDataResponse);
    }

    @PreAuthorize("hasAuthority(@apiPermission.userListViewPermission.code)")
    @GetMapping(
            path = API_PATH_GET_USER_INFO, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getUserInfo(InputUserId userIdInput) throws AccountServiceException, AuthServiceException {
        log.info("START execute.");
        AccountService.AccountResponse toAccountDataResponse = accountService.getUserInfo(userIdInput.getUserId());
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.FETCH_OK, toAccountDataResponse);
    }

    @PreAuthorize("hasAuthority(@apiPermission.userDeletePermission.code)")
    @DeleteMapping(path = API_PATH_DELETE_USER_INFO, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUserInfo(@RequestBody @Valid InputUserId userIdInput) throws AccountServiceException, AuthServiceException {
        AccountService.AccountResponse toAccountDataResponse = accountService.deleteUser(userIdInput.getUserId());
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.UPDATE_OK, toAccountDataResponse);
    }

}
