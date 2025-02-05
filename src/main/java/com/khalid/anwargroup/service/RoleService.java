package com.khalid.anwargroup.service;

import com.khalid.anwargroup.exception.RoleServiceException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

public interface RoleService {

    @Builder
    @Data
    class RoleRegisterResponse{
        private long roleCode;
        private String roleName;
        private Timestamp createdDt;
        private Timestamp updatedDt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class RoleRegisterInputParameter{
        private long roleCode;
        private String roleName;
    }

    RoleService.RoleRegisterResponse registerRole(RoleService.RoleRegisterInputParameter parameter) throws RoleServiceException;
}
