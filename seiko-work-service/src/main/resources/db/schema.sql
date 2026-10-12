-- Seiko Work Service 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS `seiko_work`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `seiko_work`;

-- 用户表
CREATE TABLE IF NOT EXISTS `work_user` (
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

-- 邮箱账号配置表（邮件内容不入库，通过 IMAP 实时拉取）
CREATE TABLE IF NOT EXISTS `work_mail` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID',
    `email`           VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    `auth_code`       VARCHAR(255) NOT NULL COMMENT '邮箱授权码',
    `imap_host`       VARCHAR(100) NOT NULL COMMENT 'IMAP服务器地址',
    `imap_port`       INT          NOT NULL DEFAULT 993 COMMENT 'IMAP端口',
    `ssl_enable`      TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用SSL：0-否，1-是',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`     DATETIME     DEFAULT NULL COMMENT '删除时间',
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮箱账号配置表';

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
CREATE TABLE IF NOT EXISTS `work_event` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户ID',
    `title`           VARCHAR(255) NOT NULL COMMENT '标题',
    `start_time`      DATETIME     NOT NULL COMMENT '开始时间',
    `end_time`        DATETIME     DEFAULT NULL COMMENT '结束时间',
    `is_all_day`      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否全天：0-否，1-是',
    `location`        VARCHAR(255) DEFAULT NULL COMMENT '地点',
    `content`         VARCHAR(500) NOT NULL COMMENT '内容',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`     DATETIME     DEFAULT NULL COMMENT '删除时间',
    KEY `idx_user_start_time` (`user_id`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程事件表';

-- ============================================================================
-- 账单记录（记账）模块
-- 需求：记录每天的消费情况，并支持按「日 / 周 / 月 / 年」统计
--
-- 设计要点：
--   1. 金额用 DECIMAL(12,2)，绝不用 FLOAT/DOUBLE——浮点累加会出现
--      0.1 + 0.2 = 0.30000000000000004 这类误差，账目上不可接受。
--   2. 金额恒为正数，收/支方向由 type 区分，统计时用 CASE WHEN 换算。
--      比「支出存负数」更直观，也避免漏加负号导致统计正负颠倒。
--   3. 日/周/月/年四种粒度的统计全部落在 bill_date 这一个字段上：
--      查询统一写成 [起始日, 结束日次日) 的日期区间，直接命中 idx_user_date，
--      因此既不需要生成列（generated column），也不需要预聚合统计表。
--      个人记账一年也就几千行，实时聚合结果永远准确、且零维护成本；
--      预聚合表反而要在增删改时同步，容易产生对不上账的数据。
--   4. 沿用现有约定：逻辑删除（is_deleted + delete_time）、无物理外键、
--      枚举用 TINYINT 由 MyBatis-Plus 映射。
-- ============================================================================

-- 账单明细表（一条记录 = 一笔消费/收入）
CREATE TABLE IF NOT EXISTS `work_bill` (
    `id`             BIGINT        AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`        BIGINT        NOT NULL COMMENT '用户ID',
    `category`       VARCHAR(255)  DEFAULT NULL COMMENT '分类',
    `type`           TINYINT       NOT NULL DEFAULT 0 COMMENT '账单类型：0-支出，1-收入',
    `amount`         DECIMAL(12,2) NOT NULL COMMENT '金额（元），恒为正数，收支方向由 type 决定',
    `bill_date`      DATE          NOT NULL COMMENT '账单日期（日/周/月/年统计均基于该字段）',
    `payment_method` TINYINT       NOT NULL DEFAULT 0 COMMENT '支付方式：0-其他，1-现金，2-微信，3-支付宝，4-储蓄卡，5-信用卡',
    `remark`         VARCHAR(255)  DEFAULT NULL COMMENT '备注',
    `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time`    DATETIME      DEFAULT NULL COMMENT '删除时间',
    KEY `idx_user_date` (`user_id`, `bill_date`),
    KEY `idx_user_type_date` (`user_id`, `type`, `bill_date`),
    KEY `idx_user_category_date` (`user_id`, `bill_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单明细表';

-- ============================================================================
-- 攒钱目标（账单页「攒钱目标」弹窗）
-- 一个用户一条配置：把每月收入按比例分配到若干档位，如
-- 储蓄 70% / 日常开销 20% / 心愿 10%
--
-- 设计要点：
--   1. 档位数量与名称都允许用户自定义，因此用 JSON 数组存一个字段，不拆子表：
--      档位只有几行、永远整组读写、也不会按档位做条件查询，拆表只会徒增 join。
--      后续若真需要按档位统计，再把 JSON 里的内容迁到子表即可。
--   2. percent 为整数百分比，服务端校验各档合计必须等于 100。
--   3. saving 标记该档是否为「储蓄档」，前端据此把「非储蓄档金额合计」当作
--      本月可用预算，与实际支出做对比。
-- ============================================================================

CREATE TABLE IF NOT EXISTS `work_savings_goal` (
    `id`          BIGINT      AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `name`        VARCHAR(50) NOT NULL DEFAULT '每月工资分配' COMMENT '目标名称',
    `buckets`     JSON        NOT NULL COMMENT '分配档位数组：[{"name":"储蓄","percent":70,"saving":true}]，percent 合计必须为 100',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    `delete_time` DATETIME    DEFAULT NULL COMMENT '删除时间',
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='攒钱目标配置表';