//package com.khalid.anwargroup.service;
//import com.khalid.anwargroup.exception.AccountServiceException;
//import com.khalid.anwargroup.exception.AuthServiceException;
//import com.khalid.anwargroup.exception.RoleServiceException;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//public interface UserService {
//
//    @Builder
//    @Data
//    class AccountResponse {
//        private long userId;
//        private String firstName;
//        private String lastName;
//        private String username;
//        private String password;
//        private String phoneNumber;
//        private String location;
//        private String roleName;
//        private boolean deleteFlg;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    class AccountRegisterInputParameter{
//        private String firstName;
//        private String lastName;
//        private String username;
//        private String password;
//        private String phoneNumber;
//        private String location;
//        private long roleCode;
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    class AccountUpdateInputParameter{
//        private long userId;
//        private String firstName;
//        private String lastName;
//        private String phoneNumber;
//        private String location;
//    }
//    AccountResponse getUserInfo(long userId) throws AccountServiceException, AuthServiceException;
//    List<AccountResponse> getUserList() throws AccountServiceException, AuthServiceException;
//    AccountResponse updateUserInfo(AccountUpdateInputParameter parameter) throws AccountServiceException, AuthServiceException;
//    AccountResponse deleteUser(long userId) throws AccountServiceException, AuthServiceException;
//    List<AccountResponse> searchUser(String keyword, int page, int size) throws AccountServiceException;
//}
