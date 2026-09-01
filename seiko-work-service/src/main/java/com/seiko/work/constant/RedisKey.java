package com.seiko.work.constant;

import lombok.Getter;

/**
 * Redis Key 枚举
 * <p>
 * 统一维护项目中所有 Redis Key 模板，避免在业务代码中硬编码 Key。
 * 使用 {@link #format(Object...)} 方法生成最终 Key。
 */
@Getter
public enum RedisKey {

    /**
     * 邮箱验证码：email:code:{email}
     */
    EMAIL_CODE("email:code:%s", "邮箱验证码"),

    /**
     * 邮箱验证码发送冷却：email:cooldown:{email}
     */
    EMAIL_COOLDOWN("email:cooldown:%s", "邮箱验证码发送冷却"),

    /**
     * 邮箱验证码 IP 发送计数：email:ip:{ip}:count
     */
    EMAIL_IP_COUNT("email:ip:%s:count", "邮箱验证码 IP 发送计数"),

    /**
     * 邮箱验证码 IP 锁定：email:ip:{ip}:lock
     */
    EMAIL_IP_LOCK("email:ip:%s:lock", "邮箱验证码 IP 锁定"),

    /**
     * 手机验证码：phone:code:{phone}
     */
    PHONE_CODE("phone:code:%s", "手机验证码"),

    /**
     * 手机验证码发送冷却：phone:cooldown:{phone}
     */
    PHONE_COOLDOWN("phone:cooldown:%s", "手机验证码发送冷却"),

    /**
     * 手机验证码 IP 发送计数：phone:ip:{ip}:count
     */
    PHONE_IP_COUNT("phone:ip:%s:count", "手机验证码 IP 发送计数"),

    /**
     * 手机验证码 IP 锁定：phone:ip:{ip}:lock
     */
    PHONE_IP_LOCK("phone:ip:%s:lock", "手机验证码 IP 锁定"),

    /**
     * 登录账号失败计数：login:account:{identifier}:fail
     */
    LOGIN_ACCOUNT_FAIL("login:account:%s:fail", "登录账号失败计数"),

    /**
     * 登录账号锁定：login:account:{identifier}:lock
     */
    LOGIN_ACCOUNT_LOCK("login:account:%s:lock", "登录账号锁定"),

    /**
     * 登录 IP 失败计数：login:ip:{ip}:fail
     */
    LOGIN_IP_FAIL("login:ip:%s:fail", "登录 IP 失败计数"),

    /**
     * 登录 IP 锁定：login:ip:{ip}:lock
     */
    LOGIN_IP_LOCK("login:ip:%s:lock", "登录 IP 锁定");

    private final String pattern;
    private final String description;

    RedisKey(String pattern, String description) {
        this.pattern = pattern;
        this.description = description;
    }

    /**
     * 根据参数格式化 Redis Key
     *
     * @param args 占位符参数
     * @return 最终 Redis Key
     */
    public String format(Object... args) {
        return String.format(pattern, args);
    }

}
