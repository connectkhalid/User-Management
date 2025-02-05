package com.khalid.anwargroup.repository;

import com.khalid.anwargroup.domain.AccessKeyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Repository("AccessKeyInfoRepository")
@Transactional(rollbackFor = Exception.class)
public interface AccessKeyInfoRepository extends JpaRepository<AccessKeyInfo, Long> {
    AccessKeyInfo findByAccessKey(String accessKey);

    @Modifying
    @Query(value = "DELETE FROM access_key_info WHERE access_key = :accessKey", nativeQuery = true)
    void deleteByAccessKey(String accessKey);

    @Modifying
    @Query(value = "DELETE FROM access_key_info WHERE access_key_info.username = ?1 AND access_key_info.created_dt < ?2", nativeQuery = true)
    void deleteByMailAddressAndExpDtLessThan(String mailAddress, Timestamp time);
}
