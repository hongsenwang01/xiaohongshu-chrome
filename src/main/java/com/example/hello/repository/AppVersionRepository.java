package com.example.hello.repository;

import com.example.hello.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 应用版本管理Repository
 */
@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    /**
     * 查询指定应用的最新版本（已启用状态）
     * 
     * @param appName 应用名称
     * @return 最新版本信息
     */
    @Query("SELECT v FROM AppVersion v WHERE v.appName = :appName AND v.status = 1 ORDER BY v.versionCode DESC LIMIT 1")
    Optional<AppVersion> findLatestVersionByAppName(@Param("appName") String appName);

    /**
     * 根据应用名称和版本号查询
     * 
     * @param appName 应用名称
     * @param versionCode 版本号
     * @return 版本信息
     */
    Optional<AppVersion> findByAppNameAndVersionCode(String appName, Integer versionCode);
}
