package com.khalid.anwargroup.service.impl;

import com.khalid.anwargroup.constant.RestErrorMessageDetail;
import com.khalid.anwargroup.repository.*;
import com.khalid.anwargroup.security.JwtTokenUtil;
import com.khalid.anwargroup.constant.RestResponseMessage;
import com.khalid.anwargroup.constant.RestResponseStatusCode;
import com.khalid.anwargroup.domain.AccessKeyInfo;
import com.khalid.anwargroup.domain.AccountInfo;
import com.khalid.anwargroup.exception.AuthServiceException;
import com.khalid.anwargroup.service.AuthCommonService;
import com.khalid.anwargroup.service.JwtLoginService;
import com.khalid.anwargroup.util.DateUtil;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtLoginServiceImpl implements JwtLoginService {

   private final AccountInfoRepository accountInfoRepository;
   private final JwtTokenUtil jwtTokenUtil;
   private final AccessKeyInfoRepository accessKeyInfoRepository;
   private final AuthenticationManager authenticationManager;
   private final RoleInfoRepository roleInfoRepository;
   private final AuthCommonService authCommonService;

    private final Clock clock = DefaultClock.INSTANCE;
    @Value("${jwt.token.expiration.in.seconds}")
    private Long expiration;

    @Override
    public LoginResponse loginWithJwtSecurity(String mailAddress, String password) throws AuthServiceException {
        log.info("START login.");
        AccountInfo accountInfo = accountInfoRepository.findByUsernameAndDeleteFlgIsFalse(mailAddress);

        if(Objects.isNull(accountInfo)){
            log.info("account nothing.");
            throw new AuthServiceException(RestResponseMessage.WRONG_CREDENTIALS,
                    RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.BAD_CREDENTIALS_ERROR_MESSAGE);
        }

        checkPassword(mailAddress, password);

        return loginUser(accountInfo);
    }

    void checkPassword(String mailAddress, String password) throws AuthServiceException {
        try{
            authenticate(mailAddress, password);
        } catch (BadCredentialsException bcex){
            log.info("password invalid.");
            throw new BadCredentialsException(RestErrorMessageDetail.BAD_CREDENTIALS_ERROR_MESSAGE);
        } catch (Exception ex){
            log.warn("Error: {}", ex.getMessage());
            throw new AuthServiceException(RestResponseMessage.UNKNOWN_ERROR, RestResponseStatusCode.INTERNAL_ERROR_STATUS, RestErrorMessageDetail.UNKNOWN_ERROR_MESSAGE);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginResponse loginUser(AccountInfo accountInfo) throws AuthServiceException{
        if(!Objects.isNull(accountInfo)){
            return getLoginResponse(accountInfo);
        }

        log.info("account nothing.");
        throw new AuthServiceException(RestResponseMessage.WRONG_CREDENTIALS,
                RestResponseStatusCode.VALIDATION_ERROR_STATUS, RestErrorMessageDetail.BAD_CREDENTIALS_ERROR_MESSAGE);
    }

    private LoginResponse getLoginResponse(AccountInfo accountInfo) throws AuthServiceException {
        accountInfo.setLastLoginDt(DateUtil.currentTime());
        accountInfo = accountInfoRepository.save(accountInfo);
        setAccountDetails(accountInfo);

        log.info("delete expired Token");
        accessKeyInfoRepository.deleteByMailAddressAndExpDtLessThan(accountInfo.getUsername(),
                new Timestamp(clock.now().getTime() - (expiration * 1000)));

        AccessKeyInfo accesskeyInfo = issueAccessKeyInfoForJwt(accountInfo.getId(), accountInfo.getUsername(), accountInfo.getRoleInfo().getRoleName());

        return LoginResponse.builder()
                .userId(accountInfo.getId())
                .roleId(accountInfo.getRoleInfoId())
                .accessKey(accesskeyInfo.getAccessKey())
                .firstName(accountInfo.getFirstName())
                .lastName(accountInfo.getLastName())
                .mailAddress(accountInfo.getUsername())
                .password(accountInfo.getPassword())
                .deleteFlg(accountInfo.getDeleteFlg())
                .createdUserId(accountInfo.getCreatedUserId())
                .build();
    }

    private void setAccountDetails(AccountInfo accountInfo) throws AuthServiceException {
        accountInfo.setRoleInfo(Objects.isNull(accountInfo.getRoleInfo()) ?
                roleInfoRepository.findById(accountInfo.getRoleInfoId())
                        .orElseThrow(() -> new AuthServiceException(RestResponseMessage.AUTH_DATA_NOT_FOUND,
                                RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.ROLE_NOT_FOUND_ERROR_MESSAGE)) : accountInfo.getRoleInfo());
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
    private AccessKeyInfo issueAccessKeyInfoForJwt(Long currentUserId, String username, String roleName) {
        log.info("START issueAccessKeyInfo.");
        final String token = jwtTokenUtil.generateToken(username, roleName);
        AccessKeyInfo accesskeyInfo = new AccessKeyInfo();
        accesskeyInfo.setAccessKey(token);
        accesskeyInfo.setAccountInfoId(currentUserId);
        accesskeyInfo.setUsername(username);
        accesskeyInfo.setLastAccessDt(DateUtil.currentTime());
        accesskeyInfo.setCreatedDt(DateUtil.currentTime());
        accesskeyInfo.setUpdatedDt(DateUtil.currentTime());
        accessKeyInfoRepository.save(accesskeyInfo);
        log.info("END issueAccessKeyInfo. accessKeyInfo:{}", accesskeyInfo);

        return accesskeyInfo;
    }

    private AccessKeyInfo issueRefreshAccessKeyInfoForJwt(Long currentUserId, String username, String roleName, String oldToken) {
        log.info("START issueAccessKeyInfo.");
        final String token = jwtTokenUtil.generateToken(username, roleName);
        AccessKeyInfo accesskeyInfo = accessKeyInfoRepository.findByAccessKey(oldToken);
        accesskeyInfo.setAccessKey(token);
        accesskeyInfo.setAccountInfoId(currentUserId);
        accesskeyInfo.setLastAccessDt(DateUtil.currentTime());
        accesskeyInfo.setCreatedDt(DateUtil.currentTime());
        accesskeyInfo.setUpdatedDt(DateUtil.currentTime());
        accesskeyInfo.setUsername(username);
        accessKeyInfoRepository.save(accesskeyInfo);

        log.info("END issueAccessKeyInfo. accessKeyInfo:{}", accesskeyInfo);
        return accesskeyInfo;
    }

    @Override
    public RefreshTokenResponse getRefreshToken() throws AuthServiceException {
        AccountInfo accountInfo = accountInfoRepository.findByAccessKey(authCommonService.getAccessKey())
                .orElseThrow(() -> new AuthServiceException(RestResponseMessage.USER_NOT_FOUND,
                        RestResponseStatusCode.NOT_FOUND_STATUS, RestErrorMessageDetail.USER_NOT_FOUND_ERROR_MESSAGE));
        setAccountDetails(accountInfo);

        AccessKeyInfo accesskeyInfo = issueRefreshAccessKeyInfoForJwt(accountInfo.getId(), accountInfo.getUsername(), accountInfo.getRoleInfo().getRoleName(), authCommonService.getAccessKey());

        return RefreshTokenResponse.builder()
                .userId(accountInfo.getId())
                .roleId(accountInfo.getRoleInfoId())
                .accessKey(accesskeyInfo.getAccessKey())
                .firstName(accountInfo.getFirstName())
                .lastName(accountInfo.getLastName())
                .mailAddress(accountInfo.getUsername())
                .password(accountInfo.getPassword())
                .deleteFlg(accountInfo.getDeleteFlg())
                .createdUserId(accountInfo.getCreatedUserId())
                .build();
    }

    @Override
    public void logoutWithJwtSecurity() throws AuthServiceException {
        String token = authCommonService.getAccessKey();
        if (!Objects.isNull(accessKeyInfoRepository.findByAccessKey(token))) {
            accessKeyInfoRepository.deleteByAccessKey(token);
        }
        else{
            throw new AuthServiceException(RestResponseMessage.NOT_LOGGED_IN, RestResponseStatusCode.NO_ACCESS_STATUS, RestErrorMessageDetail.USER_ALREADY_LOGGED_OUT_ERROR_MESSAGE);
        }
    }
}
