package com.khalid.anwargroup.security;

import com.khalid.anwargroup.domain.AccountInfo;
import com.khalid.anwargroup.repository.AccountInfoRepository;
import com.khalid.anwargroup.repository.RoleFeaturePermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtInMemoryUserDetailsService implements UserDetailsService {

    private final AccountInfoRepository accountInfoRepository;
    private final RoleFeaturePermissionRepository roleFeaturePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountInfo accountInfo = accountInfoRepository.findByUsernameAndDeleteFlgIsFalse(username);

        if(Objects.isNull(accountInfo))
            throw new UsernameNotFoundException(String.format("USER_NOT_FOUND '%s'.", username));


        List<SimpleGrantedAuthority> featurePermissionList = roleFeaturePermissionRepository
                .findAllFeaturePermissionByRoleCode(accountInfo.getRoleInfo().getRoleCode())
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new JwtUserDetails(accountInfo.getId(), accountInfo.getUsername(),
                accountInfo.getPassword(), featurePermissionList);
    }
}
