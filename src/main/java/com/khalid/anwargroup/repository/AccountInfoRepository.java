package com.khalid.anwargroup.repository;

import com.khalid.anwargroup.domain.AccountInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository("AccountInfoRepository")
@Transactional(rollbackFor = Exception.class)
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long>{
    @Query(value =
            "SELECT ai.* "
                    + "FROM account_info ai "
                    + "WHERE ai.username = :username and ai.delete_flg is false "
            , nativeQuery = true)
    AccountInfo findByUsernameAndDeleteFlgIsFalse(String username);

    @Query(value =
            "SELECT ai.* "
                    + "FROM account_info ai "
                    + "INNER JOIN access_key_info akey "
                    + "ON ai.id = akey.FK_account_info_id "
                    + "WHERE akey.access_key = :accessKey "
            , nativeQuery = true)
    Optional<AccountInfo> findByAccessKey(@Param("accessKey") String accessKey);

    @Query(value =
            "SELECT ai.* "
                    + "FROM account_info ai "
                    + "WHERE ai.fk_role_info_id != :superadminid"
            , nativeQuery = true)
    Optional<List<AccountInfo>> getAllAdmin(@Param("superadminid") long superadminid);

    @Query(value =
            "SELECT ai.* "
                    + "FROM account_info ai "
                    + "WHERE ai.created_user_id = :adminId AND ai.fk_role_info_id = 4",
            nativeQuery = true)
    Optional<List<AccountInfo>> getUsersCreatedByAdmin(@Param("adminId") long adminId);


    @Query("SELECT a FROM AccountInfo a WHERE " +
            "(:searchTerm IS NULL OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND a.deleteFlg = false")
    Page<AccountInfo> searchAdmin(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT a FROM AccountInfo a WHERE a.username = :username")
    Optional<AccountInfo> findByUsername(String username);
}
