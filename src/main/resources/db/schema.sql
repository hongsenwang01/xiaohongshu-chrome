-- 微信支付订单表
CREATE TABLE IF NOT EXISTS `wechat_pay_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `out_trade_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '商户订单号',
    `transaction_id` VARCHAR(32) COMMENT '微信支付订单号',
    `description` VARCHAR(255) COMMENT '商品描述',
    `amount` INT NOT NULL COMMENT '订单金额（单位：分）',
    `code_url` LONGTEXT COMMENT '二维码链接',
    `payer_openid` VARCHAR(128) COMMENT '付款用户的openid',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
    `success_time` DATETIME COMMENT '支付成功时间',
    `client_ip` VARCHAR(50) COMMENT '用户客户端IP',
    `remarks` LONGTEXT COMMENT '备注信息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_out_trade_no` (`out_trade_no`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信支付Native订单记录表';

-- 小红书关注关系表
CREATE TABLE IF NOT EXISTS `xhs_follow_relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `owner_redid` VARCHAR(64) NOT NULL COMMENT '号主的 redid（谁的关注列表）',
  `target_redid` VARCHAR(64) NOT NULL COMMENT '被关注人的 redid',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '被关注人昵称（快照）',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '被关注人头像地址',
  `profile_url` VARCHAR(255) DEFAULT NULL COMMENT '被关注人主页链接',
  `is_followed_back` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '对方是否回关：1=已回关（target→owner），0=未回关',
  `follow_time` DATETIME NOT NULL COMMENT '关注时间（owner→target）',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_owner_target` (`owner_redid`, `target_redid`),
  KEY `idx_owner_back` (`owner_redid`, `is_followed_back`),
  KEY `idx_owner_time` (`owner_redid`, `follow_time`),
  KEY `idx_target_owner` (`target_redid`, `owner_redid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小红书关注关系表';

-- 应用版本管理表
CREATE TABLE IF NOT EXISTS `app_version` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_name` VARCHAR(100) NOT NULL COMMENT '应用名称',
  `version_code` INT NOT NULL COMMENT '版本号（整数，用于比较版本大小）',
  `version_name` VARCHAR(50) NOT NULL COMMENT '版本名称（如1.0.0）',
  `is_force_update` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否强制更新：1=强制更新，0=非强制更新',
  `description` TEXT COMMENT '版本更新描述',
  `download_url` VARCHAR(500) COMMENT '下载地址',
  `file_size` BIGINT COMMENT '文件大小（字节）',
  `min_support_version` INT COMMENT '最低支持的版本号',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_version` (`app_name`, `version_code`),
  KEY `idx_app_status` (`app_name`, `status`),
  KEY `idx_version_code` (`version_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用版本管理表';
