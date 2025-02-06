package com.khalid.anwargroup.api.pvt;

import com.khalid.anwargroup.exception.AccountServiceException;
import com.khalid.anwargroup.service.AccountService;
import com.khalid.anwargroup.constant.Constants;
import com.khalid.anwargroup.constant.RestResponseMessage;
import com.khalid.anwargroup.constant.RestResponseStatusCode;
import com.khalid.anwargroup.exception.AuthServiceException;
import com.khalid.anwargroup.exception.RoleServiceException;
import com.khalid.anwargroup.form.annotation.MailAddress;
import com.khalid.anwargroup.form.annotation.Password;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

import static com.khalid.anwargroup.constant.RestApiResponse.*;

@Slf4j
@RestController
@CrossOrigin
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountController {
    public static final String API_PATH_ACCOUNT_REGISTER = Constants.ApiPath.PRIVATE_API_PATH + "/register-account";
    public static final String API_PATH_GET_ADMIN_LIST = Constants.ApiPath.PRIVATE_API_PATH + "/get-admin-list";
    public static final String API_PATH_GET_ADMIN_INFO = Constants.ApiPath.PRIVATE_API_PATH + "/get-admin-info";
    public static final String API_PATH_UPDATE_ADMIN_INFO = Constants.ApiPath.PRIVATE_API_PATH + "/update-admin-info";
    public static final String API_PATH_DELETE_ADMIN_INFO = Constants.ApiPath.PRIVATE_API_PATH + "/delete-admin";
    public static final String API_PATH_SEARCH_ADMIN_INFO = Constants.ApiPath.PRIVATE_API_PATH + "/search-admin";

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
    public static class InputAdminId {
        long adminId;
    }

    @Data
    public static class SearchKeyword {
        @NotNull
        String keyword;

        @NotNull
        @Min(0) // Ensuring page is non-negative
        int page;

        @NotNull
        @Min(1) // Ensuring size is at least 1
        int size;
    }


    @Data
    public static class InputAdminUpdate {

        @NotNull(message = "field is mandatory")
        long adminId;

        @NotBlank(message = "field is mandatory")
        String firstName;

        @NotBlank(message = "field is mandatory")
        String lastName;

        @NotBlank(message = "field is mandatory")
        String phoneNumber;

        @NotBlank(message = "field is mandatory")
        String location;
    }
    @PostMapping(path = API_PATH_ACCOUNT_REGISTER,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority(@apiPermission.adminCreatePermission.code)")
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

    @PreAuthorize("hasAuthority(@apiPermission.adminListViewPermission.code)")
    @GetMapping(
            path = API_PATH_GET_ADMIN_LIST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAdminList() throws AccountServiceException, AuthServiceException {
        log.info("START execute.");
        List<AccountService.AccountResponse> toAccountDataResponse = accountService.getUserList();
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.FETCH_OK, toAccountDataResponse);
    }

    @PreAuthorize("hasAuthority(@apiPermission.adminDetailPermission.code)")
    @GetMapping(
            path = API_PATH_GET_ADMIN_INFO, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAdminInfo(@RequestBody(required = false) InputAdminId adminIdInput)
            throws AccountServiceException, AuthServiceException {

        log.info("START execute.");

        // Handle null case by providing a default value (e.g: id, 0)
        long adminId = (adminIdInput != null) ? adminIdInput.getAdminId() : 0;

        AccountService.AccountResponse toAccountDataResponse = accountService.getUserInfo(adminId);
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.FETCH_OK, toAccountDataResponse);
    }


    @PreAuthorize("hasAuthority(@apiPermission.adminEditPermission.code)")
    @PutMapping(path = API_PATH_UPDATE_ADMIN_INFO,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateAdminInfo(@RequestBody @Valid InputAdminUpdate inputAdminUpdateParameter) throws AccountServiceException, AuthServiceException {
        AccountService.AccountUpdateInputParameter parameter = new AccountService.AccountUpdateInputParameter(
                inputAdminUpdateParameter.getAdminId(),
                inputAdminUpdateParameter.getFirstName(),
                inputAdminUpdateParameter.getLastName(),
                inputAdminUpdateParameter.getPhoneNumber(),
                inputAdminUpdateParameter.getLocation()
        );
        AccountService.AccountResponse toAccountDataResponse = accountService.updateAdminInfo(parameter);
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.UPDATE_OK, toAccountDataResponse);
    }

    @PreAuthorize("hasAuthority(@apiPermission.adminDeletePermission.code)")
    @DeleteMapping(path = API_PATH_DELETE_ADMIN_INFO, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteAdminInfo(@RequestBody @Valid InputAdminId adminIdInput) throws AccountServiceException, AuthServiceException {
        AccountService.AccountResponse toAccountDataResponse = accountService.deleteUser(adminIdInput.getAdminId());
        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.UPDATE_OK, toAccountDataResponse);
    }

    @PreAuthorize("hasAuthority(@apiPermission.adminSearchPermission.code)")
    @GetMapping(path = API_PATH_SEARCH_ADMIN_INFO, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> searchAdminInfo(
            @RequestParam String keyword,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "3", required = false) int size) throws AccountServiceException, AuthServiceException {

        List<AccountService.AccountResponse> searchResults = accountService.searchByKeyword(keyword, page, size);

        return buildResponseWithDetails(RestResponseStatusCode.OK_STATUS, RestResponseMessage.FETCH_OK, searchResults);
    }

}
