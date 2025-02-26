//package com.khalid.anwargroup.service.impl;
//
//import com.khalid.anwargroup.constant.RestErrorMessageDetail;
//import com.khalid.anwargroup.constant.RestResponseMessage;
//import com.khalid.anwargroup.constant.RestResponseStatusCode;
//import com.khalid.anwargroup.domain.AccountInfo;
//import com.khalid.anwargroup.domain.RoleInfo;
//import com.khalid.anwargroup.exception.AccountServiceException;
//import com.khalid.anwargroup.exception.AuthServiceException;
//import com.khalid.anwargroup.exception.RoleServiceException;
//import com.khalid.anwargroup.repository.AccountInfoRepository;
//import com.khalid.anwargroup.repository.RoleInfoRepository;
//import com.khalid.anwargroup.service.AccountService;
//import com.khalid.anwargroup.service.AuthCommonService;
//import com.khalid.anwargroup.service.UserService;
//import com.khalid.anwargroup.util.DateUtil;
//import com.khalid.anwargroup.util.Utils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service("UserService")
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class UserServiceImpl implements UserService {
//
//     private final AccountInfoRepository accountInfoRepository;
//     private final RoleInfoRepository roleInfoRepository;
//     private final AuthCommonService authCommonService;
//
//    @Override
//    public AccountResponse getUserInfo(long userId) throws AccountServiceException, AuthServiceException {
//        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());
//
//        if (currentUserAccountInfo.isEmpty()) {
//            log.info("Current user account not found.");
//            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
//                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
//        }
//
//        AccountInfo currentUser = currentUserAccountInfo.get();
//
//        // If the current user is requesting their own info, return it
//        if (currentUser.getId() == userId) {
//            log.warn("Returning details for the current user.");
//            return buildAccountResponse(currentUser);
//        }
//
//        // Fetch the target user's details
//        Optional<AccountInfo> targetUserInfoOpt = accountInfoRepository.findById(userId);
//        if (targetUserInfoOpt.isEmpty()) {
//            log.info("Target user account not found.");
//            throw new AuthServiceException(RestResponseMessage.NOT_FOUND,
//                    RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.USER_NOT_FOUND_ERROR_MESSAGE);
//        }
//
//        AccountInfo targetUser = targetUserInfoOpt.get();
//
//        // Allow SUPER_ADMIN (Role ID = 1) to view all users
//        if (currentUser.getRoleInfoId() == 1) {
//            log.info("Super Admin viewing user details.");
//            return buildAccountResponse(targetUser);
//        }
//
//        // Allow the creator of the user to view the details
//        if (targetUser.getCreatedUserId() == currentUser.getId()) {
//            log.info("Admin who created this user is viewing details.");
//            return buildAccountResponse(targetUser);
//        }
//
//        // Deny access if none of the conditions are met
//        log.info("Access denied: User does not have permission to view this account.");
//        throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
//                RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
//    }
//
//    @Override
//    public List<AccountResponse> getUserList() throws AccountServiceException, AuthServiceException {
//        return List.of();
//    }
//
//    @Override
//    public AccountResponse updateUserInfo(AccountUpdateInputParameter parameter) throws AccountServiceException, AuthServiceException {
//        return null;
//    }
//
//    @Override
//    public AccountResponse deleteUser(long userId) throws AccountServiceException, AuthServiceException {
//        return null;
//    }
//
//    @Override
//    public List<AccountResponse> searchUser(String keyword, int page, int size) throws AccountServiceException {
//        return List.of();
//    }
//
//    private AccountResponse buildAccountResponse(AccountInfo accountInfo) {
//        return AccountResponse.builder()
//                .userId(accountInfo.getId())
//                .firstName(accountInfo.getFirstName())
//                .lastName(accountInfo.getLastName())
//                .username(accountInfo.getUsername())
//                .password(accountInfo.getPassword())
//                .phoneNumber(accountInfo.getPhoneNumber())
//                .location(accountInfo.getLocation())
//                .roleName(accountInfo.getRoleInfo().getRoleName())
//                .deleteFlg(accountInfo.getDeleteFlg())
//                .build();
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
////
////
////
////
////    public List<AccountResponse> getAdminList() throws AuthServiceException {
////        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());
////        if(currentUserAccountInfo.isEmpty() || currentUserAccountInfo.get().getRoleInfoId() != 1 ){
////            log.info("account nothing.");
////            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
////                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
////        }
////
////        Optional<List<AccountInfo>> accountResponseList = accountInfoRepository.getAllAdmin();
////        if(accountResponseList.isEmpty()){
////            log.info("account nothing.");
////            return null;
////        }
////         return accountResponseList.get().stream()
////                .filter(accountInfo -> accountInfo.getId() != 1)
////                .sorted(Comparator.comparing(AccountInfo::getId))
////                .map(accountInfo -> AccountResponse.builder()
////                        .adminId(accountInfo.getId())
////                        .firstName(accountInfo.getFirstName())
////                        .lastName(accountInfo.getLastName())
////                        .username(accountInfo.getUsername())
////                        .phoneNumber(accountInfo.getPhoneNumber())
////                        .location(accountInfo.getLocation())
////                        .roleName(accountInfo.getRoleInfo().getRoleName())
////                        .deleteFlg(accountInfo.getDeleteFlg())
////                        .build())
////                .collect(Collectors.toList());
////    }
////
////    @Override
////    public AccountResponse getAdminInfo(long adminId) throws AuthServiceException {
////        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());
////
////        if (currentUserAccountInfo.isEmpty() || (adminId != 0 && currentUserAccountInfo.get().getRoleInfoId() != 1  && currentUserAccountInfo.get().getId() != adminId))
////        {
////            log.info("account nothing.");
////            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
////                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
////        }
////        Optional<AccountInfo> accountInfo;
////        if(adminId != 0){
////            accountInfo = accountInfoRepository.findById(adminId);
////            if(accountInfo.isEmpty()){
////                log.info("account nothing.");
////                throw new AuthServiceException(RestResponseMessage.NOT_FOUND,
////                        RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.USER_NOT_FOUND_ERROR_MESSAGE);
////            }
////            return AccountResponse.builder()
////                    .adminId(accountInfo.get().getId())
////                    .firstName(accountInfo.get().getFirstName())
////                    .lastName(accountInfo.get().getLastName())
////                    .username(accountInfo.get().getUsername())
////                    .password(accountInfo.get().getPassword())
////                    .phoneNumber(accountInfo.get().getPhoneNumber())
////                    .location(accountInfo.get().getLocation())
////                    .roleName(accountInfo.get().getRoleInfo().getRoleName())
////                    .deleteFlg(accountInfo.get().getDeleteFlg())
////                    .build();
////        }
////        else{
////            log.warn("Current User Details is Returned.");
////            return AccountResponse.builder()
////                    .adminId(currentUserAccountInfo.get().getId())
////                    .firstName(currentUserAccountInfo.get().getFirstName())
////                    .lastName(currentUserAccountInfo.get().getLastName())
////                    .username(currentUserAccountInfo.get().getUsername())
////                    .password(currentUserAccountInfo.get().getPassword())
////                    .phoneNumber(currentUserAccountInfo.get().getPhoneNumber())
////                    .location(currentUserAccountInfo.get().getLocation())
////                    .roleName(currentUserAccountInfo.get().getRoleInfo().getRoleName())
////                    .deleteFlg(currentUserAccountInfo.get().getDeleteFlg())
////                    .build();
////        }
////    }
////
////    @Override
////    public AccountResponse updateAdminInfo(AccountUpdateInputParameter parameter) throws  AuthServiceException {
////        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());
////        if (currentUserAccountInfo.isEmpty()
////                || (currentUserAccountInfo.get().getRoleInfoId() != 1
////                && currentUserAccountInfo.get().getId() != parameter.getAdminId()))
////        {
////            log.info("account nothing.");
////            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
////                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
////        }
////
////        Optional<AccountInfo> accountInfo = accountInfoRepository.findById(parameter.getAdminId());
////        if(accountInfo.isEmpty()){
////            log.info("account nothing.");
////            throw new AuthServiceException(RestResponseMessage.WRONG_CREDENTIALS,
////                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.BAD_CREDENTIALS_ERROR_MESSAGE);
////        }
////
////        accountInfo.get().setFirstName(parameter.getFirstName());
////        accountInfo.get().setLastName(parameter.getLastName());
////        accountInfo.get().setPhoneNumber(parameter.getPhoneNumber());
////        accountInfo.get().setLocation(parameter.getLocation());
////
////        accountInfoRepository.save(accountInfo.get());
////
////        return AccountResponse.builder()
////                .adminId(accountInfo.get().getId())
////                .firstName(accountInfo.get().getFirstName())
////                .lastName(accountInfo.get().getLastName())
////                .username(accountInfo.get().getUsername())
////                .password(accountInfo.get().getPassword())
////                .phoneNumber(accountInfo.get().getPhoneNumber())
////                .location(accountInfo.get().getLocation())
////                .roleName(accountInfo.get().getRoleInfo().getRoleName())
////                .deleteFlg(accountInfo.get().getDeleteFlg())
////                .build();
////    }
////
////    @Override
////    public AccountResponse deleteAdmin(long adminId) throws AuthServiceException {
////        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());
////        //only super admin can delete account
////        if(currentUserAccountInfo.isEmpty() || currentUserAccountInfo.get().getRoleInfoId() != 1 ){
////            log.info("account nothing.");
////            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
////                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
////        }
////
////        Optional<AccountInfo> accountInfo = accountInfoRepository.findById(adminId);
////        if(accountInfo.isEmpty()){
////            log.info("account nothing.");
////            throw new AuthServiceException(RestResponseMessage.NOT_FOUND,
////                    RestResponseStatusCode.BAD_REQUEST_STATUS, RestErrorMessageDetail.NO_SUCH_ENTITY_FOUND_ERROR_MESSAGE);
////        }
////        if(accountInfo.get().getDeleteFlg()){
////            log.info("account is already deleted.");
////            throw new AuthServiceException(RestResponseMessage.BAD_REQUEST,
////                    RestResponseStatusCode.ALREADY_EXISTS_STATUS, RestErrorMessageDetail.USER_ALREADY_DELETED);
////        }
////        accountInfo.get().setDeleteFlg(true);
////        accountInfoRepository.save(accountInfo.get());
////        return AccountResponse.builder()
////                .adminId(accountInfo.get().getId())
////                .firstName(accountInfo.get().getFirstName())
////                .lastName(accountInfo.get().getLastName())
////                .username(accountInfo.get().getUsername())
////                .phoneNumber(accountInfo.get().getPhoneNumber())
////                .location(accountInfo.get().getLocation())
////                .roleName(accountInfo.get().getRoleInfo().getRoleName())
////                .deleteFlg(accountInfo.get().getDeleteFlg())
////                .build();
////    }
////
////    @Override
////    public List<AccountResponse> searchAdmin(String keyword, int page, int size) throws AccountServiceException {
////        // Adjust the page number to be zero-based
////        Pageable pageable = PageRequest.of(page - 1, size);
////
////        Page<AccountInfo> accountPage = accountInfoRepository.searchAdmin(keyword, pageable);
////
////        if (accountPage.isEmpty()) {
////            throw new AccountServiceException(RestResponseMessage.NOT_FOUND,
////                    RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.NO_SUCH_DATA);
////        }
////
////        return accountPage.getContent().stream()
////                .map(accountInfo -> AccountResponse.builder()
////                        .adminId(accountInfo.getId())
////                        .firstName(accountInfo.getFirstName())
////                        .lastName(accountInfo.getLastName())
////                        .username(accountInfo.getUsername())
////                        .phoneNumber(accountInfo.getPhoneNumber())
////                        .location(accountInfo.getLocation())
////                        .roleName(accountInfo.getRoleInfo().getRoleName())
////                        .deleteFlg(accountInfo.getDeleteFlg())
////                        .build())
////                .collect(Collectors.toList());
////    }
//
//
//}
