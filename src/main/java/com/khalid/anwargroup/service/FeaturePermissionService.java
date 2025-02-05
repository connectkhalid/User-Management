package com.khalid.anwargroup.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public interface FeaturePermissionService {

    @AllArgsConstructor
    class FeaturePermissionMap{
        MainFeature mainFeature;
        SubFeature subFeature;
        Permission permission;
    }

    @Getter
    @Builder
    class FeaturePermissionObject{
        FeaturePermissionMap featurePermissionMap;
        String code;
    }

    enum MainFeature {
        ACCOUNT_MANAGEMENT
    }

    enum SubFeature {
        ADMIN_BASIC,
        USER_BASIC,
        ADMIN_PROFILE,
        USER_PROFILE
    }

    enum Permission {
        VIEW_DETAILS,
        EDIT,
        ADD,
        VIEW_LIST,
        SEARCH,
        DELETE
    }
}
