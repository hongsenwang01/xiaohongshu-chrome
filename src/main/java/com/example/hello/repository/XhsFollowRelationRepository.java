package com.example.hello.repository;

import com.example.hello.entity.XhsFollowRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 小红书关注关系仓库接口
 */
@Repository
public interface XhsFollowRelationRepository extends JpaRepository<XhsFollowRelation, Long> {
    
    /**
     * 根据号主和目标查找关注关系
     */
    Optional<XhsFollowRelation> findByOwnerRedidAndTargetRedid(String ownerRedid, String targetRedid);
    
    /**
     * 根据号主查找所有关注关系的昵称列表
     */
    @Query("SELECT r.nickname FROM XhsFollowRelation r WHERE r.ownerRedid = :ownerRedid AND r.nickname IS NOT NULL ORDER BY r.followTime DESC")
    List<String> findNicknamesByOwnerRedid(@Param("ownerRedid") String ownerRedid);

    /**
     * 根据号主查找所有关注关系的主页链接列表
     */
    @Query("SELECT r.profileUrl FROM XhsFollowRelation r WHERE r.ownerRedid = :ownerRedid AND r.profileUrl IS NOT NULL ORDER BY r.followTime DESC")
    List<String> findProfileUrlByOwnerRedid(@Param("ownerRedid") String ownerRedid);

    /**
     * 根据号主查找所有关注关系的主页链接列表（包含id）
     * 只返回未标记为互相关注的记录，且关注时间在指定时间之前（用于避免刚关注就清理）
     */
    @Query("SELECT r FROM XhsFollowRelation r WHERE r.ownerRedid = :ownerRedid AND r.profileUrl IS NOT NULL AND r.isFollowedBack = false AND r.followTime < :beforeTime ORDER BY r.followTime DESC")
    List<XhsFollowRelation> findProfileUrlRelationsByOwnerRedid(@Param("ownerRedid") String ownerRedid, @Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 根据ID列表批量查找关注关系
     */
    @Query("SELECT r FROM XhsFollowRelation r WHERE r.id IN :ids")
    List<XhsFollowRelation> findByIdIn(@Param("ids") List<Long> ids);
}

