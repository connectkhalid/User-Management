package com.khalid.anwargroup.constant;


import com.khalid.anwargroup.service.FeaturePermissionService.FeaturePermissionObject;
import com.khalid.anwargroup.service.FeaturePermissionService.MainFeature;
import com.khalid.anwargroup.service.FeaturePermissionService.SubFeature;
import com.khalid.anwargroup.service.FeaturePermissionService.Permission;
import org.springframework.stereotype.Service;
import static com.khalid.anwargroup.service.impl.FeaturePermissionServiceImpl.getFeaturePermissionObject;
@Service("apiPermission")
public class FeaturePermissionList {

    // ACCOUNT MANAGEMENT
    public static FeaturePermissionObject getAdminCreatePermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.ADMIN_BASIC, Permission.VIEW_LIST);
    }

    public static FeaturePermissionObject getAdminEditPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.ADMIN_BASIC, Permission.EDIT);
    }

    public static FeaturePermissionObject getAdminDetailPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.ADMIN_BASIC, Permission.VIEW_DETAILS);
    }

    public static FeaturePermissionObject getAdminListViewPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.ADMIN_BASIC, Permission.VIEW_LIST);
    }

    public static FeaturePermissionObject getAdminDeletePermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.ADMIN_BASIC, Permission.SEARCH);
    }

    public static FeaturePermissionObject getAdminSearchPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.ADMIN_BASIC, Permission.DELETE);
    }

    // USER MANAGEMENT
    public static FeaturePermissionObject getUserCreatePermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.USER_BASIC, Permission.VIEW_LIST);
    }

    public static FeaturePermissionObject getUserEditPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.USER_BASIC, Permission.EDIT);
    }

    public static FeaturePermissionObject getUserDetailPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.USER_BASIC, Permission.VIEW_DETAILS);
    }

    public static FeaturePermissionObject getUserListViewPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.USER_BASIC, Permission.VIEW_LIST);
    }

    public static FeaturePermissionObject getUserDeletePermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.USER_BASIC, Permission.SEARCH);
    }

    public static FeaturePermissionObject getUserSearchPermission() {
        return getFeaturePermissionObject(MainFeature.ACCOUNT_MANAGEMENT, SubFeature.USER_BASIC, Permission.DELETE);
    }

}
