package com.example.hello.repository;

import com.example.hello.entity.XhsLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 授权码仓库接口
 */
@Repository
public interface XhsLicenseRepository extends JpaRepository<XhsLicense, Long> {
    
    /**
     * 根据授权码查找
     */
    Optional<XhsLicense> findByCode(String code);
}

