package com.khalid.anwargroup.repository;

import com.khalid.anwargroup.domain.RoleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository("RoleInfoRepository")
@Transactional(rollbackFor = Exception.class)
public interface RoleInfoRepository extends JpaRepository<RoleInfo, Long> {

    RoleInfo findByRoleCode(Long roleCode);

    @Query(value =
            " SELECT ri.* "
                    + " FROM role_info ri "
                    + " INNER JOIN account_info ai "
                    + "   ON ri.id = ai.FK_role_info_id "
                    + " INNER JOIN access_key_info akey "
                    + "   ON ai.id = akey.FK_account_info_id "
                    + "WHERE akey.access_key = :accessKey and ai.delete_flg is false "
            , nativeQuery = true)
    Optional<RoleInfo> findByAccessKey(@Param("accessKey") String accessKey);

    @Query(value =
            "SELECT ri.* " +
                    "FROM role_info ri " +
                    "INNER JOIN account_info ai ON ri.id = ai.FK_role_info_id " +
                    "INNER JOIN access_key_info akey ON ai.id = akey.FK_account_info_id " +
                    "WHERE akey.access_key = :accessKey AND ai.delete_flg = false",
            nativeQuery = true)
    Optional<RoleInfo> findByUserAccessKey(@Param("accessKey") String accessKey);

    @Query(value =
            " SELECT ri.* "
                    + " FROM role_info ri "
                    + " INNER JOIN account_info ai "
                    + "   ON ri.id = ai.FK_role_info_id "
                    + "WHERE ai.username = :username and ai.delete_flg is false "
            , nativeQuery = true)
    Optional<RoleInfo> findByMailAddress(@Param("username") String username);

}
