package com.khalid.anwargroup.service;

import com.khalid.anwargroup.domain.AccountInfo;
import com.khalid.anwargroup.exception.AccountServiceException;
import com.khalid.anwargroup.exception.AuthServiceException;
import com.khalid.anwargroup.exception.RoleServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountService {

    @Builder
    @Data
    class AccountResponse {
        private long id;
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private String phoneNumber;
        private String location;
        private String roleName;
        private boolean deleteFlg;
        public static AccountResponse fromAccountInfo(AccountInfo accountInfo) {
            return AccountResponse.builder()
                    .id(accountInfo.getId())
                    .firstName(accountInfo.getFirstName())
                    .lastName(accountInfo.getLastName())
                    .username(accountInfo.getUsername())
                    .phoneNumber(accountInfo.getPhoneNumber())
                    .location(accountInfo.getLocation())
                    .roleName(accountInfo.getRoleInfo().getRoleName())
                    .deleteFlg(accountInfo.getDeleteFlg())
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AccountRegisterInputParameter{
        private String firstName;
        private String lastName;
        private String username;
        private String password;
        private String phoneNumber;
        private String location;
        private long roleCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AccountUpdateInputParameter{
        private long adminId;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String location;
    }

    @Transactional(rollbackFor = Exception.class)
    AccountResponse registerAccount(AccountRegisterInputParameter parameter) throws AccountServiceException, RoleServiceException;
    AccountResponse getUserInfo(long adminId) throws AccountServiceException, AuthServiceException;
    List<AccountResponse> getUserList() throws AccountServiceException, AuthServiceException;
    AccountResponse updateAdminInfo(AccountUpdateInputParameter parameter) throws AccountServiceException, AuthServiceException;
    AccountResponse deleteUser(long adminId) throws AccountServiceException, AuthServiceException;
    List<AccountResponse> searchByKeyword(String keyword, int page, int size) throws AccountServiceException;
}
