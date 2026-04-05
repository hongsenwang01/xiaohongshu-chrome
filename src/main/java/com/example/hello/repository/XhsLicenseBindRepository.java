package com.example.hello.repository;

import com.example.hello.entity.XhsLicenseBind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 授权码绑定仓库接口
 */
@Repository
public interface XhsLicenseBindRepository extends JpaRepository<XhsLicenseBind, Long> {
    
    /**
     * 根据redid查找绑定记录
     */
    Optional<XhsLicenseBind> findByRedid(String redid);
    
    /**
     * 统计某个授权码的绑定数量
     */
    int countByLicenseId(Long licenseId);
    
    /**
     * 更新最近访问时间
     */
    @Modifying
    @Query("UPDATE XhsLicenseBind b SET b.lastSeenAt = :lastSeenAt WHERE b.redid = :redid")
    int updateLastSeenAt(@Param("redid") String redid, @Param("lastSeenAt") LocalDateTime lastSeenAt);
}

