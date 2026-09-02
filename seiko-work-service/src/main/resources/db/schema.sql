-- Seiko Work Service 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS `seiko_work`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `seiko_work`;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`        VARCHAR(255) DEFAULT NULL COMMENT '密码（BCrypt加密），手机号注册用户可能为空',
    `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `nickname`        VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`          VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`     DATETIME     DEFAULT NULL COMMENT '删除时间',
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 邮件表
CREATE TABLE IF NOT EXISTS `mail_message` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID',
    `message_uid`     VARCHAR(255) NOT NULL COMMENT '邮件原始UID',
    `from_address`    VARCHAR(255) DEFAULT NULL COMMENT '发件人邮箱',
    `from_name`       VARCHAR(255) DEFAULT NULL COMMENT '发件人名称',
    `subject`         VARCHAR(500) DEFAULT NULL COMMENT '主题',
    `content_text`    LONGTEXT     DEFAULT NULL COMMENT '纯文本正文',
    `content_html`    LONGTEXT     DEFAULT NULL COMMENT 'HTML正文',
    `receive_time`    DATETIME     DEFAULT NULL COMMENT '收取时间',
    `is_read`         TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `has_attachment`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否有附件：0-无，1-有',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`     DATETIME     DEFAULT NULL COMMENT '删除时间',
    UNIQUE KEY `uk_user_message_uid` (`user_id`, `message_uid`),
    KEY `idx_user_receive_time` (`user_id`, `receive_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件表';

-- 工作事项表
CREATE TABLE IF NOT EXISTS `work_task` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID',
    `mail_id`         BIGINT       DEFAULT NULL COMMENT '关联邮件ID',
    `title`           VARCHAR(255) NOT NULL COMMENT '标题',
    `content`         LONGTEXT     DEFAULT NULL COMMENT '内容',
    `plan_date`       DATE         DEFAULT NULL COMMENT '计划日期',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待办，1-进行中，2-已完成',
    `priority`        TINYINT      NOT NULL DEFAULT 1 COMMENT '优先级：0-低，1-中，2-高',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`     DATETIME     DEFAULT NULL COMMENT '删除时间',
    KEY `idx_user_plan_date` (`user_id`, `plan_date`),
    KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作事项表';

-- 日程事件表
CREATE TABLE IF NOT EXISTS `calendar_event` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID',
    `title`           VARCHAR(255) NOT NULL COMMENT '标题',
    `start_time`      DATETIME     NOT NULL COMMENT '开始时间',
    `end_time`        DATETIME     DEFAULT NULL COMMENT '结束时间',
    `is_all_day`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否全天：0-否，1-是',
    `location`        VARCHAR(255) DEFAULT NULL COMMENT '地点',
    `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`     DATETIME     DEFAULT NULL COMMENT '删除时间',
    KEY `idx_user_start_time` (`user_id`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程事件表';
