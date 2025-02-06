package com.khalid.anwargroup.provider;

import com.khalid.anwargroup.domain.AccountInfo;
import com.khalid.anwargroup.domain.RoleInfo;
import com.khalid.anwargroup.repository.AccountInfoRepository;
import com.khalid.anwargroup.repository.RoleInfoRepository;
import com.khalid.anwargroup.util.DateUtil;
import com.khalid.anwargroup.util.Utils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SuperAdminInitializer implements CommandLineRunner {
    private final AccountInfoRepository userRepository;
    private final RoleInfoRepository roleInfoRepository;

    public SuperAdminInitializer(AccountInfoRepository userRepository, RoleInfoRepository roleInfoRepository) {
        this.userRepository = userRepository;
        this.roleInfoRepository = roleInfoRepository;
    }

    @Override
    public void run(String... args) {
        String email = "khalid.hasan@gmail.com";
        RoleInfo roleInfo = roleInfoRepository.findByRoleCode(1L);
        Optional<AccountInfo> existingUser = userRepository.findByUsername(email);
        if (existingUser.isEmpty() && roleInfo != null) {
            AccountInfo superAdmin = new AccountInfo();
            superAdmin.setFirstName("Khalid");
            superAdmin.setLastName("Hasan");
            superAdmin.setLocation("Dhaka");
            superAdmin.setUsername(email);
            superAdmin.setPhoneNumber("01997770868");
            superAdmin.setCreatedDt(DateUtil.currentTime());
            superAdmin.setUpdatedDt(DateUtil.currentTime());
            superAdmin.setDeleteFlg(false);
            superAdmin.setLastLoginDt(DateUtil.currentTime());
            superAdmin.setRoleInfoId(1L); // SuperAdmin role id is 1
            superAdmin.setPassword(Utils.encodeBCrypt("Kh@123456"));

            userRepository.save(superAdmin);
            System.out.println("Super Admin user created successfully.");
        } else {
            if(roleInfo == null)System.out.println("Create Role For SuperAdmin");
            System.out.println("Super Admin user already exists.");
        }
    }
}
