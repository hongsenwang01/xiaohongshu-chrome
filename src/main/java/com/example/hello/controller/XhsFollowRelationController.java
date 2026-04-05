package com.example.hello.controller;

import com.example.hello.dto.BatchOperationRequest;
import com.example.hello.dto.BatchOperationResponse;
import com.example.hello.dto.GetFollowListResponse;
import com.example.hello.dto.GetProfileUrlListResponse;
import com.example.hello.dto.UploadFollowRelationRequest;
import com.example.hello.dto.UploadFollowRelationResponse;
import com.example.hello.service.XhsFollowRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 小红书关注关系控制器
 */
@RestController
@RequestMapping("/api/xhs/follow")
@CrossOrigin(origins = "*")
public class XhsFollowRelationController {

    private static final Logger logger = LoggerFactory.getLogger(XhsFollowRelationController.class);

    @Autowired
    private XhsFollowRelationService followRelationService;

    /**
     * 上传关注关系数据接口
     * 
     * 接口地址：POST /api/xhs/follow/upload
     * 
     * 请求示例：
     * {
     *   "ownerRedid": "号主的小红书号",
     *   "followList": [
     *     {
     *       "targetRedid": "被关注人的小红书号",
     *       "nickname": "被关注人昵称",
     *       "avatarUrl": "头像地址",
     *       "profileUrl": "主页链接"
     *     }
     *   ]
     * }
     * 
     * 响应示例（成功）：
     * {
     *   "code": 0,
     *   "message": "上传完成",
     *   "successCount": 10,
     *   "skippedCount": 2,
     *   "failedCount": 0,
     *   "timestamp": 1699605600000
     * }
     *
     * @param request 上传请求对象
     * @return 上传响应对象
     */
    @PostMapping("/upload")
    public UploadFollowRelationResponse uploadFollowRelations(@RequestBody UploadFollowRelationRequest request) {
        logger.info("收到关注关系上传请求: ownerRedid={}, followListSize={}", 
            request != null ? request.getOwnerRedid() : "null",
            request != null && request.getFollowList() != null ? request.getFollowList().size() : 0);
        
        if (request == null) {
            return new UploadFollowRelationResponse(1, "请求参数不能为空");
        }

        try {
            UploadFollowRelationResponse response = followRelationService.uploadFollowRelations(request);
            logger.info("关注关系上传响应: code={}, message={}, successCount={}, skippedCount={}, failedCount={}", 
                response.getCode(), response.getMessage(), 
                response.getSuccessCount(), response.getSkippedCount(), response.getFailedCount());
            return response;
        } catch (Exception e) {
            logger.error("关注关系上传接口异常", e);
            return new UploadFollowRelationResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 获取关注列表接口（初始化用）
     * 
     * 接口地址：GET /api/xhs/follow/list?ownerRedid=xxx
     * 
     * 说明：
     * - 用于插件第一次加载时获取初始化关注列表
     * - 只返回昵称列表，供前端缓存使用
     * 
     * 响应示例（成功）：
     * {
     *   "code": 0,
     *   "message": "获取成功",
     *   "nicknameList": ["昵称1", "昵称2", "昵称3"],
     *   "timestamp": 1699605600000
     * }
     *
     * @param ownerRedid 号主的小红书号
     * @return 关注列表响应对象
     */
    @GetMapping("/list")
    public GetFollowListResponse getFollowList(@RequestParam String ownerRedid) {
        logger.info("收到获取关注列表请求: ownerRedid={}", ownerRedid);
        
        if (ownerRedid == null || ownerRedid.trim().isEmpty()) {
            return new GetFollowListResponse(1, "号主小红书号不能为空");
        }

        try {
            GetFollowListResponse response = followRelationService.getFollowList(ownerRedid);
            logger.info("获取关注列表响应: code={}, message={}, nicknameCount={}", 
                response.getCode(), response.getMessage(), 
                response.getNicknameList() != null ? response.getNicknameList().size() : 0);
            return response;
        } catch (Exception e) {
            logger.error("获取关注列表接口异常", e);
            return new GetFollowListResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 获取profileUrlList 接口
     * 
     * 过滤条件：
     * 1. 只返回 is_followed_back = false 的记录（未互相关注的）
     * 2. 只返回关注时间在2小时之前的记录（避免刚关注就清理，给对方时间回关）
     * 
     * 接口地址：GET /api/xhs/follow/profileUrlList?ownerRedid=xxx
     * 
     * 请求示例：
     * {
     *   "ownerRedid": "号主的小红书号"
     * }
     * 
     * 响应示例（成功）：
     * {
     *   "code": 0,
     *   "message": "获取成功",
     *   "profileUrlList": [
     *     {"id": 1, "profileUrl": "https://..."},
     *     {"id": 2, "profileUrl": "https://..."}
     *   ],
     *   "timestamp": 1699605600000
     * }
     * 
     * @param ownerRedid 号主的小红书号
     * @return 主页链接列表响应对象（包含id和profileUrl）
     */
    @GetMapping("/profileUrlList")
    public GetProfileUrlListResponse getProfileUrlList(@RequestParam String ownerRedid) {
        logger.info("收到获取主页链接列表请求: ownerRedid={}", ownerRedid);
        
        if (ownerRedid == null || ownerRedid.trim().isEmpty()) {
            return new GetProfileUrlListResponse(1, "号主小红书号不能为空");
        }

        try {
            GetProfileUrlListResponse response = followRelationService.getProfileUrlList(ownerRedid);
            logger.info("获取主页链接列表响应: code={}, message={}, profileUrlCount={}", 
                response.getCode(), response.getMessage(), 
                response.getProfileUrlList() != null ? response.getProfileUrlList().size() : 0);
            return response;
        } catch (Exception e) {
            logger.error("获取主页链接列表接口异常", e);
            return new GetProfileUrlListResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 批量标记为互相关注接口
     * 
     * 接口地址：PUT /api/xhs/follow/batch/mark-followed-back
     * 
     * 说明：
     * - 前端验证到用户已回关后调用此接口（支持单个或多个ID）
     * - 将对应记录的 is_followed_back 字段批量更新为 true
     * - 如果某些记录已经是互相关注状态，不会重复更新，但计入成功数
     * - 支持部分成功，返回成功和失败的数量
     * 
     * 请求示例（单个）：
     * {
     *   "ids": [1]
     * }
     * 
     * 请求示例（多个）：
     * {
     *   "ids": [1, 2, 3, 4, 5]
     * }
     * 
     * 响应示例（成功）：
     * {
     *   "code": 0,
     *   "message": "批量标记成功",
     *   "successCount": 5,
     *   "failedCount": 0,
     *   "timestamp": 1699605600000
     * }
     * 
     * 响应示例（部分成功）：
     * {
     *   "code": 0,
     *   "message": "批量标记完成，部分失败",
     *   "successCount": 3,
     *   "failedCount": 2,
     *   "timestamp": 1699605600000
     * }
     *
     * @param request 批量操作请求，包含ID列表（支持单个或多个ID）
     * @return 批量操作结果
     */
    @PutMapping("/batch/mark-followed-back")
    public BatchOperationResponse batchMarkAsFollowedBack(@RequestBody BatchOperationRequest request) {
        logger.info("收到批量标记互相关注请求: ids={}", request != null ? request.getIds() : null);
        
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return new BatchOperationResponse(1, "ID列表不能为空");
        }

        try {
            BatchOperationResponse response = followRelationService.batchMarkAsFollowedBack(request.getIds());
            logger.info("批量标记互相关注响应: code={}, message={}, successCount={}, failedCount={}", 
                response.getCode(), response.getMessage(), response.getSuccessCount(), response.getFailedCount());
            return response;
        } catch (Exception e) {
            logger.error("批量标记互相关注接口异常", e);
            return new BatchOperationResponse(1, "系统异常: " + e.getMessage());
        }
    }

    /**
     * 批量删除关注关系接口
     * 
     * 接口地址：DELETE /api/xhs/follow/batch
     * 
     * 说明：
     * - 前端验证到用户未回关（取消关注）后调用此接口（支持单个或多个ID）
     * - 根据ID列表批量删除对应的关注关系记录
     * - 删除操作不可逆，请前端确认后再调用
     * - 支持部分成功，返回成功和失败的数量
     * 
     * 请求示例（单个）：
     * {
     *   "ids": [1]
     * }
     * 
     * 请求示例（多个）：
     * {
     *   "ids": [1, 2, 3, 4, 5]
     * }
     * 
     * 响应示例（成功）：
     * {
     *   "code": 0,
     *   "message": "批量删除成功",
     *   "successCount": 5,
     *   "failedCount": 0,
     *   "timestamp": 1699605600000
     * }
     * 
     * 响应示例（部分成功）：
     * {
     *   "code": 0,
     *   "message": "批量删除完成，部分失败",
     *   "successCount": 3,
     *   "failedCount": 2,
     *   "timestamp": 1699605600000
     * }
     *
     * @param request 批量操作请求，包含ID列表（支持单个或多个ID）
     * @return 批量操作结果
     */
    @DeleteMapping("/batch")
    public BatchOperationResponse batchDeleteFollowRelations(@RequestBody BatchOperationRequest request) {
        logger.info("收到批量删除关注关系请求: ids={}", request != null ? request.getIds() : null);
        
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return new BatchOperationResponse(1, "ID列表不能为空");
        }

        try {
            BatchOperationResponse response = followRelationService.batchDeleteFollowRelations(request.getIds());
            logger.info("批量删除关注关系响应: code={}, message={}, successCount={}, failedCount={}", 
                response.getCode(), response.getMessage(), response.getSuccessCount(), response.getFailedCount());
            return response;
        } catch (Exception e) {
            logger.error("批量删除关注关系接口异常", e);
            return new BatchOperationResponse(1, "系统异常: " + e.getMessage());
        }
    }
}