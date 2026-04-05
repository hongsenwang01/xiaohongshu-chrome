package com.example.hello.service;

import com.example.hello.dto.BatchOperationResponse;
import com.example.hello.dto.GetFollowListResponse;
import com.example.hello.dto.GetProfileUrlListResponse;
import com.example.hello.dto.UploadFollowRelationRequest;
import com.example.hello.dto.UploadFollowRelationResponse;

import java.util.List;

/**
 * 小红书关注关系服务接口
 */
public interface XhsFollowRelationService {
    
    /**
     * 上传关注关系数据
     * 
     * @param request 上传请求
     * @return 上传结果
     */
    UploadFollowRelationResponse uploadFollowRelations(UploadFollowRelationRequest request);
    
    /**
     * 获取关注列表（仅昵称）
     * 
     * @param ownerRedid 号主的小红书号
     * @return 关注列表响应
     */
    GetFollowListResponse getFollowList(String ownerRedid);

    /**
     * 获取主页链接列表（只返回未标记为互相关注的记录）
     * 
     * @param ownerRedid 号主的小红书号
     * @return 主页链接列表响应
     */
    GetProfileUrlListResponse getProfileUrlList(String ownerRedid);

    /**
     * 批量标记为互相关注（更新 is_followed_back 为 true）
     * 支持传入单个或多个ID
     * 
     * @param ids 关注关系记录ID列表
     * @return 批量操作结果
     */
    BatchOperationResponse batchMarkAsFollowedBack(List<Long> ids);
    
    /**
     * 批量删除关注关系记录
     * 支持传入单个或多个ID
     * 
     * @param ids 关注关系记录ID列表
     * @return 批量操作结果
     */
    BatchOperationResponse batchDeleteFollowRelations(List<Long> ids);
}

