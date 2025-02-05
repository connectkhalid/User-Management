package com.khalid.anwargroup.service.impl;

import com.khalid.anwargroup.constant.RestErrorMessageDetail;
import com.khalid.anwargroup.exception.AccountServiceException;
import com.khalid.anwargroup.constant.RestResponseMessage;
import com.khalid.anwargroup.constant.RestResponseStatusCode;
import com.khalid.anwargroup.domain.AccountInfo;
import com.khalid.anwargroup.domain.RoleInfo;
import com.khalid.anwargroup.exception.AuthServiceException;
import com.khalid.anwargroup.exception.RoleServiceException;
import com.khalid.anwargroup.repository.AccountInfoRepository;
import com.khalid.anwargroup.repository.RoleInfoRepository;
import com.khalid.anwargroup.service.AccountService;
import com.khalid.anwargroup.service.AuthCommonService;
import com.khalid.anwargroup.util.DateUtil;
import com.khalid.anwargroup.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("AccountService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceImpl implements AccountService {

     private final AccountInfoRepository accountInfoRepository;
     private final RoleInfoRepository roleInfoRepository;
     private final AuthCommonService authCommonService;

    @Override
    public AccountResponse registerAccount(AccountRegisterInputParameter parameter) throws RoleServiceException, AccountServiceException {

        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());
        if(currentUserAccountInfo.isEmpty()) {
            throw new AccountServiceException(RestResponseMessage.NOT_FOUND,
                    RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.USER_NOT_FOUND_ERROR_MESSAGE);
        }
        long currentUserRoleCode = currentUserAccountInfo.get().getRoleInfoId();

        // Fetch the role information based on the provided role code
        RoleInfo roleInfo = roleInfoRepository.findByRoleCode(parameter.getRoleCode());
        if (Objects.isNull(roleInfo)) {
            log.info("Role not found.");
            throw new RoleServiceException(RestResponseMessage.ROLE_NOT_FOUND,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.BAD_CREDENTIALS_ERROR_MESSAGE);
        }

        // If the role is ADMIN_A (2) or ADMIN_B (3), check if the current user is a Super Admin
        if ((roleInfo.getRoleCode() == 2 || roleInfo.getRoleCode() == 3) && currentUserAccountInfo.get().getRoleInfoId()!=1) {
//            if (currentUserAccountInfo.isEmpty()) {
                log.info("Unauthorized access attempt.");
                throw new AccountServiceException(RestResponseMessage.ACCESS_DENIED,
                        RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
            }

//            // Check if the current user is a super admin (role code 1)
//            if (currentUserRoleCode == 1) {
//                log.info("Super Admin cannot create ADMIN_A or ADMIN_B.");
//                throw new AccountServiceException(RestResponseMessage.BAD_REQUEST, RestResponseStatusCode.BAD_REQUEST_STATUS, RestErrorMessageDetail.SUPER_ADMIN_CREATE_ERROR_MESSAGE);
//            }
//        }

        if (roleInfo.getRoleCode() == 4) {
            // Check if the current user has permission to create a user (ADMIN_A or ADMIN_B)
            if (currentUserRoleCode != 2 && currentUserRoleCode != 3) {
                log.info("Permission denied: Only Admins can create Users.");
                throw new AccountServiceException(RestResponseMessage.ACCESS_DENIED,
                        RestResponseStatusCode.FORBIDDEN, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
            }
        }

        // Create and save the account info
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setRoleInfoId(roleInfo.getId());
        accountInfo.setFirstName(parameter.getFirstName());
        accountInfo.setLastName(parameter.getLastName());
        accountInfo.setUsername(parameter.getUsername());
        accountInfo.setPassword(Utils.encodeBCrypt(parameter.getPassword()));
        accountInfo.setPhoneNumber(parameter.getPhoneNumber());
        accountInfo.setLocation(parameter.getLocation());
        accountInfo.setDeleteFlg(false);
        accountInfo.setCreatedUserId(currentUserRoleCode);
        accountInfo.setCreatedDt(DateUtil.currentTime());
        accountInfo.setUpdatedDt(DateUtil.currentTime());
        accountInfo.setLastLoginDt(DateUtil.currentTime());

        accountInfoRepository.save(accountInfo);

        return AccountResponse.builder()
                .firstName(parameter.getFirstName())
                .lastName(parameter.getLastName())
                .username(parameter.getUsername())
                .password(parameter.getPassword())
                .phoneNumber(parameter.getPhoneNumber())
                .location(parameter.getLocation())
                .roleName(roleInfo.getRoleName())
                .build();
    }

    @Override
    public List<AccountResponse> getUserList() throws AuthServiceException {
        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());

        //If the current user does not exist, deny access
        if (currentUserAccountInfo.isEmpty()) {
            log.info("Unauthorized access attempt.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        AccountInfo currentUser = currentUserAccountInfo.get();
        long currentUserId = currentUser.getId();
        long currentUserRoleCode = currentUser.getRoleInfo().getRoleCode();

        List<AccountInfo> accountList;

        if (currentUserRoleCode == 1) {
            //SuperAdmin can fetch all users
            accountList = accountInfoRepository.getAllAdmin(currentUserRoleCode).orElse(Collections.emptyList());
        } else if (currentUserRoleCode == 2 || currentUserRoleCode == 3) {
            //ADMIN_A or ADMIN_B  can fetch only users they created
            accountList = accountInfoRepository.getUsersCreatedByAdmin(currentUserId).orElse(Collections.emptyList());
        } else {
            log.info("Permission denied.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.FORBIDDEN, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        //Convert AccountInfo list to AccountResponse and sort by ID
        return accountList.stream()
                .sorted(Comparator.comparing(AccountInfo::getId))
                .map(AccountResponse::fromAccountInfo) //Using static method to map
                .collect(Collectors.toList());
    }



    @Override
    public AccountResponse getUserInfo(long adminId) throws AuthServiceException {
        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());

        // If the current user does not exist, deny access
        if (currentUserAccountInfo.isEmpty()) {
            log.info("Access denied: User not found.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        AccountInfo currentUser = currentUserAccountInfo.get();

        // Fetch target user details
        Optional<AccountInfo> accountInfo = (adminId != 0) ? accountInfoRepository.findById(adminId) : Optional.empty();

        // If adminId is provided but the target user is not found, deny access
        if (adminId != 0 && accountInfo.isEmpty()) {
            log.info("User not found.");
            throw new AuthServiceException(RestResponseMessage.NOT_FOUND,
                    RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.USER_NOT_FOUND_ERROR_MESSAGE);
        }

        AccountInfo targetUser = accountInfo.orElse(currentUser); // If adminId is 0, return current user

        //  Updated condition: SuperAdmin (1) can view any user, others follow restrictions
        if (currentUser.getRoleInfoId() != 1) { // If not a SuperAdmin

            // Case: Admin tries to view another admin (not allowed)
            if (adminId != 0 && targetUser.getRoleInfoId() <= 3) {
                log.info("Access denied: Admins cannot view other admins.");
                throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                        RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
            }

            // Case: ADMIN_A (2) or ADMIN_B (3) tries to view a normal user (4)
            if ((currentUser.getRoleInfoId() == 2 || currentUser.getRoleInfoId() == 3) // If ADMIN_A or ADMIN_B
                    && targetUser.getRoleInfoId() == 4  // Target user is a normal user
                    && !Objects.equals(targetUser.getCreatedUserId(), currentUser.getId())) { // Ensure the user was created by the same admin
                log.info("Access denied: ADMIN_A or ADMIN_B can only view users they created.");
                throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                        RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
            }
        }

        log.info("Returning user details for ID: {}", targetUser.getId());
        return AccountResponse.fromAccountInfo(targetUser);
    }

    @Override
    public AccountResponse updateAdminInfo(AccountUpdateInputParameter parameter) throws AuthServiceException {
        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());

        //If no valid user is found, deny access
        if (currentUserAccountInfo.isEmpty()) {
            log.info("Unauthorized access attempt.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        AccountInfo currentUser = currentUserAccountInfo.get();
        long currentUserId = currentUser.getId();
        long currentUserRoleId = currentUser.getRoleInfo().getRoleCode();

        Optional<AccountInfo> accountInfo = accountInfoRepository.findById(parameter.getAdminId());

        //If target account does not exist
        if (accountInfo.isEmpty()) {
            log.info("User not found.");
            throw new AuthServiceException(RestResponseMessage.NOT_FOUND,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.USER_NOT_FOUND_ERROR_MESSAGE);
        }

        AccountInfo targetUser = accountInfo.get();

        //Permission Check
        boolean isSuperAdmin = (currentUserRoleId == 1);
        boolean isSelfUpdate = (currentUserId == targetUser.getId());
        boolean isAdminUpdatingOwnUser = ((currentUserRoleId == 2 || currentUserRoleId == 3) // If ADMIN_A or ADMIN_B
                && targetUser.getRoleInfo().getRoleCode() == 4 // Target is a normal user
                && Objects.equals(targetUser.getCreatedUserId(), currentUserId)); // Created by current admin

        if (!isSuperAdmin && !isSelfUpdate && !isAdminUpdatingOwnUser) {
            log.info("Access denied: No permission to update this user.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        //Update user details
        targetUser.setFirstName(parameter.getFirstName());
        targetUser.setLastName(parameter.getLastName());
        targetUser.setPhoneNumber(parameter.getPhoneNumber());
        targetUser.setLocation(parameter.getLocation());

        accountInfoRepository.save(targetUser);

        return AccountResponse.fromAccountInfo(targetUser);
    }


    @Override
    public AccountResponse deleteUser(long adminId) throws AuthServiceException {
        Optional<AccountInfo> currentUserAccountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey());

        //If no valid user is found, deny access
        if (currentUserAccountInfo.isEmpty()) {
            log.info("Unauthorized access attempt.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        AccountInfo currentUser = currentUserAccountInfo.get();
        long currentUserId = currentUser.getId();
        long currentUserRoleId = currentUser.getRoleInfo().getRoleCode();

        Optional<AccountInfo> accountInfo = accountInfoRepository.findById(adminId);

        // If target account does not exist
        if (accountInfo.isEmpty()) {
            log.info("User not found.");
            throw new AuthServiceException(RestResponseMessage.NOT_FOUND,
                    RestResponseStatusCode.BAD_REQUEST_STATUS, RestErrorMessageDetail.NO_SUCH_ENTITY_FOUND_ERROR_MESSAGE);
        }

        AccountInfo targetUser = accountInfo.get();

        //If the account is already deleted
        if (targetUser.getDeleteFlg()) {
            log.info("Account is already deleted.");
            throw new AuthServiceException(RestResponseMessage.BAD_REQUEST,
                    RestResponseStatusCode.ALREADY_EXISTS_STATUS, RestErrorMessageDetail.USER_ALREADY_DELETED);
        }

        //Permission Check
        boolean isSuperAdmin = (currentUserRoleId == 1);
        boolean isAdminDeletingOwnUser = ((currentUserRoleId == 2 || currentUserRoleId == 3) // If ADMIN_A or ADMIN_B
                && targetUser.getRoleInfo().getRoleCode() == 4 // Target is a normal user
                && Objects.equals(targetUser.getCreatedUserId(), currentUserId)); // Created by current admin

        //Prevent SuperAdmin from deleting their own account
        if (isSuperAdmin && currentUserId == adminId) {
            log.info("SuperAdmin cannot delete their own account.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        if (!isSuperAdmin && !isAdminDeletingOwnUser) {
            log.info("Access denied: No permission to delete this user.");
            throw new AuthServiceException(RestResponseMessage.ACCESS_DENIED,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.NO_PERMISSION_ERROR_MESSAGE);
        }

        //Mark user as deleted
        targetUser.setDeleteFlg(true);
        accountInfoRepository.save(targetUser);

        return AccountResponse.fromAccountInfo(targetUser);
    }


    @Override
    public List<AccountResponse> searchByKeyword(String keyword, int page, int size) throws AccountServiceException {
        // Adjust the page number to be zero-based
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<AccountInfo> accountPage = accountInfoRepository.searchAdmin(keyword, pageable);

        if (accountPage.isEmpty()) {
            throw new AccountServiceException(RestResponseMessage.NOT_FOUND,
                    RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.NO_SUCH_DATA);
        }

        return accountPage.getContent().stream()
                .map(accountInfo -> AccountResponse.builder()
                        .id(accountInfo.getId())
                        .firstName(accountInfo.getFirstName())
                        .lastName(accountInfo.getLastName())
                        .username(accountInfo.getUsername())
                        .phoneNumber(accountInfo.getPhoneNumber())
                        .location(accountInfo.getLocation())
                        .roleName(accountInfo.getRoleInfo().getRoleName())
                        .deleteFlg(accountInfo.getDeleteFlg())
                        .build())
                .collect(Collectors.toList());
    }

}
