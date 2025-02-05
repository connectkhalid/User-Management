package com.khalid.anwargroup.service.impl;

import com.khalid.anwargroup.service.RoleService;
import com.khalid.anwargroup.domain.RoleInfo;
import com.khalid.anwargroup.exception.RoleServiceException;
import com.khalid.anwargroup.repository.RoleInfoRepository;
import com.khalid.anwargroup.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("RoleService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleServiceImpl implements RoleService {

    private final RoleInfoRepository roleInfoRepository;
    @Override
    public RoleRegisterResponse registerRole(RoleRegisterInputParameter parameter) throws RoleServiceException {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRoleCode(parameter.getRoleCode());
        roleInfo.setRoleName(parameter.getRoleName());
        roleInfo.setDeleteFlag(false);
        roleInfo.setCreatedDt(DateUtil.currentTime());
        roleInfo.setUpdatedDt(DateUtil.currentTime());
        roleInfoRepository.save(roleInfo);

        return RoleRegisterResponse.builder()
                .roleCode(parameter.getRoleCode())
                .roleName(parameter.getRoleName())
                .createdDt(roleInfo.getCreatedDt())
                .updatedDt(roleInfo.getUpdatedDt())
                .build();
    }
}
