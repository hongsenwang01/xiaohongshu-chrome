package com.example.hello.service.impl;

import com.example.hello.dto.BatchOperationResponse;
import com.example.hello.dto.FollowUserInfo;
import com.example.hello.dto.GetFollowListResponse;
import com.example.hello.dto.GetProfileUrlListResponse;
import com.example.hello.dto.ProfileUrlItem;
import com.example.hello.dto.UploadFollowRelationRequest;
import com.example.hello.dto.UploadFollowRelationResponse;
import com.example.hello.entity.XhsFollowRelation;
import com.example.hello.repository.XhsFollowRelationRepository;
import com.example.hello.service.XhsFollowRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小红书关注关系服务实现
 */
@Service
public class XhsFollowRelationServiceImpl implements XhsFollowRelationService {

    private static final Logger logger = LoggerFactory.getLogger(XhsFollowRelationServiceImpl.class);

    @Autowired
    private XhsFollowRelationRepository followRelationRepository;

    @Override
    @Transactional
    public UploadFollowRelationResponse uploadFollowRelations(UploadFollowRelationRequest request) {
        if (request == null) {
            return new UploadFollowRelationResponse(1, "请求参数不能为空");
        }

        String ownerRedid = request.getOwnerRedid();
        List<FollowUserInfo> followList = request.getFollowList();

        if (ownerRedid == null || ownerRedid.trim().isEmpty()) {
            return new UploadFollowRelationResponse(1, "号主小红书号不能为空");
        }

        if (followList == null || followList.isEmpty()) {
            return new UploadFollowRelationResponse(1, "关注列表不能为空");
        }

        int successCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        LocalDateTime now = LocalDateTime.now();

        for (FollowUserInfo followUser : followList) {
            try {
                if (followUser == null || followUser.getTargetRedid() == null || followUser.getTargetRedid().trim().isEmpty()) {
                    failedCount++;
                    logger.warn("跳过无效的关注用户数据: {}", followUser);
                    continue;
                }

                String targetRedid = followUser.getTargetRedid().trim();

                // 检查是否已存在（避免重复）
                if (followRelationRepository.findByOwnerRedidAndTargetRedid(ownerRedid, targetRedid).isPresent()) {
                    skippedCount++;
                    logger.debug("关注关系已存在，跳过: ownerRedid={}, targetRedid={}", ownerRedid, targetRedid);
                    continue;
                }

                // 创建新记录
                XhsFollowRelation relation = new XhsFollowRelation();
                relation.setOwnerRedid(ownerRedid);
                relation.setTargetRedid(targetRedid);
                relation.setNickname(followUser.getNickname());
                relation.setAvatarUrl(followUser.getAvatarUrl());
                relation.setProfileUrl(followUser.getProfileUrl());
                relation.setIsFollowedBack(false);
                relation.setFollowTime(now);

                followRelationRepository.save(relation);
                successCount++;
                logger.debug("保存关注关系成功: ownerRedid={}, targetRedid={}", ownerRedid, targetRedid);

            } catch (Exception e) {
                failedCount++;
                logger.error("保存关注关系失败: ownerRedid={}, targetRedid={}, error={}", 
                    ownerRedid, followUser != null ? followUser.getTargetRedid() : "null", e.getMessage());
            }
        }

        UploadFollowRelationResponse response = new UploadFollowRelationResponse(0, "上传完成");
        response.setSuccessCount(successCount);
        response.setSkippedCount(skippedCount);
        response.setFailedCount(failedCount);

        logger.info("关注关系上传完成: ownerRedid={}, 成功={}, 跳过={}, 失败={}", 
            ownerRedid, successCount, skippedCount, failedCount);

        return response;
    }

    @Override
    public GetFollowListResponse getFollowList(String ownerRedid) {
        if (ownerRedid == null || ownerRedid.trim().isEmpty()) {
            return new GetFollowListResponse(1, "号主小红书号不能为空");
        }

        try {
            List<String> nicknameList = followRelationRepository.findNicknamesByOwnerRedid(ownerRedid);
            
            GetFollowListResponse response = new GetFollowListResponse(0, "获取成功");
            response.setNicknameList(nicknameList);
            
            logger.info("获取关注列表成功: ownerRedid={}, nicknameCount={}", ownerRedid, nicknameList.size());
            
            return response;
        } catch (Exception e) {
            logger.error("获取关注列表异常: ownerRedid={}, error={}", ownerRedid, e.getMessage(), e);
            return new GetFollowListResponse(1, "系统异常: " + e.getMessage());
        }
    }

    @Override
    public GetProfileUrlListResponse getProfileUrlList(String ownerRedid) {
        if (ownerRedid == null || ownerRedid.trim().isEmpty()) {
            return new GetProfileUrlListResponse(1, "号主小红书号不能为空");
        }

        try {
            // 计算2小时之前的时间，避免刚关注就被清理
            LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
            System.out.println("twoHoursAgo: " + twoHoursAgo);
            
            List<XhsFollowRelation> relations = followRelationRepository.findProfileUrlRelationsByOwnerRedid(ownerRedid, twoHoursAgo);
            
            List<ProfileUrlItem> profileUrlList = relations.stream()
                    .map(r -> new ProfileUrlItem(r.getId(), r.getProfileUrl()))
                    .collect(Collectors.toList());
            
            GetProfileUrlListResponse response = new GetProfileUrlListResponse(0, "获取成功");
            response.setProfileUrlList(profileUrlList);
            
            logger.info("获取主页链接列表成功: ownerRedid={}, profileUrlCount={} (只包含2小时前的未互关记录)", 
                ownerRedid, profileUrlList.size());
            return response;
        } catch (Exception e) {
            logger.error("获取主页链接列表异常: ownerRedid={}, error={}", ownerRedid, e.getMessage(), e);
            return new GetProfileUrlListResponse(1, "系统异常: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public BatchOperationResponse batchMarkAsFollowedBack(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new BatchOperationResponse(1, "ID列表不能为空");
        }

        int successCount = 0;
        int failedCount = 0;

        try {
            // 批量查询所有记录
            List<XhsFollowRelation> relations = followRelationRepository.findByIdIn(ids);
            
            if (relations.isEmpty()) {
                logger.warn("批量标记互相关注失败：没有找到任何记录，ids={}", ids);
                return new BatchOperationResponse(1, "没有找到任何记录", 0, ids.size());
            }

            // 批量更新
            for (XhsFollowRelation relation : relations) {
                try {
                    // 只更新未标记的记录
                    if (!Boolean.TRUE.equals(relation.getIsFollowedBack())) {
                        relation.setIsFollowedBack(true);
                        followRelationRepository.save(relation);
                        successCount++;
                        logger.debug("标记互相关注成功：id={}, ownerRedid={}, targetRedid={}", 
                            relation.getId(), relation.getOwnerRedid(), relation.getTargetRedid());
                    } else {
                        successCount++;
                        logger.debug("记录已经是互相关注状态，跳过：id={}", relation.getId());
                    }
                } catch (Exception e) {
                    failedCount++;
                    logger.error("标记互相关注失败：id={}, error={}", relation.getId(), e.getMessage());
                }
            }

            // 计算未找到的记录数
            failedCount += (ids.size() - relations.size());

            logger.info("批量标记互相关注完成：请求数量={}, 成功={}, 失败={}", 
                ids.size(), successCount, failedCount);

            String message = failedCount > 0 ? "批量标记完成，部分失败" : "批量标记成功";
            return new BatchOperationResponse(0, message, successCount, failedCount);

        } catch (Exception e) {
            logger.error("批量标记互相关注异常：ids={}, error={}", ids, e.getMessage(), e);
            return new BatchOperationResponse(1, "系统异常: " + e.getMessage(), successCount, failedCount);
        }
    }

    @Override
    @Transactional
    public BatchOperationResponse batchDeleteFollowRelations(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new BatchOperationResponse(1, "ID列表不能为空");
        }

        int successCount = 0;
        int failedCount = 0;

        try {
            // 批量查询所有记录
            List<XhsFollowRelation> relations = followRelationRepository.findByIdIn(ids);
            
            if (relations.isEmpty()) {
                logger.warn("批量删除关注关系失败：没有找到任何记录，ids={}", ids);
                return new BatchOperationResponse(1, "没有找到任何记录", 0, ids.size());
            }

            // 批量删除
            for (XhsFollowRelation relation : relations) {
                try {
                    Long id = relation.getId();
                    String ownerRedid = relation.getOwnerRedid();
                    String targetRedid = relation.getTargetRedid();
                    
                    followRelationRepository.deleteById(id);
                    successCount++;
                    
                    logger.debug("删除关注关系成功：id={}, ownerRedid={}, targetRedid={}", 
                        id, ownerRedid, targetRedid);
                } catch (Exception e) {
                    failedCount++;
                    logger.error("删除关注关系失败：id={}, error={}", relation.getId(), e.getMessage());
                }
            }

            // 计算未找到的记录数
            failedCount += (ids.size() - relations.size());

            logger.info("批量删除关注关系完成：请求数量={}, 成功={}, 失败={}", 
                ids.size(), successCount, failedCount);

            String message = failedCount > 0 ? "批量删除完成，部分失败" : "批量删除成功";
            return new BatchOperationResponse(0, message, successCount, failedCount);

        } catch (Exception e) {
            logger.error("批量删除关注关系异常：ids={}, error={}", ids, e.getMessage(), e);
            return new BatchOperationResponse(1, "系统异常: " + e.getMessage(), successCount, failedCount);
        }
    }
}

