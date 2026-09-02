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

-- 工资记录表
CREATE TABLE IF NOT EXISTS `salary_record` (
    `id`                        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`                   BIGINT          NOT NULL COMMENT '用户ID',
    `year_month`                VARCHAR(7)      NOT NULL COMMENT '年月，格式：yyyy-MM',
    `base_salary`               DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '基本工资',
    `performance_salary`        DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '绩效工资',
    `subsidy`                   DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '补贴',
    `gross_salary`              DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '税前总额',
    `tax`                       DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '个税',
    `social_security_personal`  DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '五险一金个人部分',
    `housing_fund_personal`     DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '公积金个人部分',
    `net_salary`                DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '实发工资',
    `remark`                    VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    `create_time`               DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`               DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`                TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`               DATETIME        DEFAULT NULL COMMENT '删除时间',
    UNIQUE KEY `uk_user_year_month` (`user_id`, `year_month`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工资记录表';

-- 五险一金表
CREATE TABLE IF NOT EXISTS `social_insurance` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`             BIGINT          NOT NULL COMMENT '用户ID',
    `year_month`          VARCHAR(7)      NOT NULL COMMENT '年月，格式：yyyy-MM',
    `pension_personal`    DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '养老保险个人缴纳',
    `pension_company`     DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '养老保险公司缴纳',
    `medical_personal`    DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '医疗保险个人缴纳',
    `medical_company`     DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '医疗保险公司缴纳',
    `unemployment_personal` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '失业保险个人缴纳',
    `unemployment_company`  DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '失业保险公司缴纳',
    `injury_company`      DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '工伤保险公司缴纳',
    `maternity_company`   DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '生育保险公司缴纳',
    `housing_fund_personal` DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '公积金个人缴纳',
    `housing_fund_company`  DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '公积金公司缴纳',
    `total_personal`      DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '个人缴纳合计',
    `total_company`       DECIMAL(12, 2)  NOT NULL DEFAULT 0.00 COMMENT '公司缴纳合计',
    `remark`              VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    `create_time`         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`          TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`         DATETIME        DEFAULT NULL COMMENT '删除时间',
    UNIQUE KEY `uk_user_year_month` (`user_id`, `year_month`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='五险一金表';

-- 请假记录表
CREATE TABLE IF NOT EXISTS `leave_record` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID',
    `leave_type`      VARCHAR(20)  NOT NULL COMMENT '请假类型：annual-年假，sick-病假，personal-事假，compensatory-调休，other-其他',
    `start_date`      DATE         NOT NULL COMMENT '开始日期',
    `end_date`        DATE         NOT NULL COMMENT '结束日期',
    `days`            DECIMAL(4, 1) NOT NULL DEFAULT 0.0 COMMENT '请假天数',
    `reason`          VARCHAR(500) DEFAULT NULL COMMENT '事由',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待审批，1-已通过，2-已拒绝',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`     DATETIME     DEFAULT NULL COMMENT '删除时间',
    KEY `idx_user_start_date` (`user_id`, `start_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假记录表';

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
