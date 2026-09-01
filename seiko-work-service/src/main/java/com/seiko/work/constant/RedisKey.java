package com.seiko.work.constant;

/**
 * Redis Key 常量
 * <p>
 * 统一维护项目中所有 Redis Key 模板，避免在业务代码中硬编码 Key。
 * 模板中含 {@code %s} 占位符，使用 {@link String#formatted(Object...)} 方法生成最终 Key，
 * 例如：{@code RedisKey.EMAIL_CODE.formatted(email)}。
 */
public final class RedisKey {

    private RedisKey() {
    }

    /**
     * 邮箱验证码：email:code:{email}
     */
    public static final String EMAIL_CODE = "email:code:%s";

    /**
     * 邮箱验证码发送冷却：email:cooldown:{email}
     */
    public static final String EMAIL_COOLDOWN = "email:cooldown:%s";

    /**
     * 邮箱验证码 IP 发送计数：email:ip:{ip}:count
     */
    public static final String EMAIL_IP_COUNT = "email:ip:%s:count";

    /**
     * 邮箱验证码 IP 锁定：email:ip:{ip}:lock
     */
    public static final String EMAIL_IP_LOCK = "email:ip:%s:lock";

    /**
     * 手机验证码：phone:code:{phone}
     */
    public static final String PHONE_CODE = "phone:code:%s";

    /**
     * 手机验证码发送冷却：phone:cooldown:{phone}
     */
    public static final String PHONE_COOLDOWN = "phone:cooldown:%s";

    /**
     * 手机验证码 IP 发送计数：phone:ip:{ip}:count
     */
    public static final String PHONE_IP_COUNT = "phone:ip:%s:count";

    /**
     * 手机验证码 IP 锁定：phone:ip:{ip}:lock
     */
    public static final String PHONE_IP_LOCK = "phone:ip:%s:lock";

    /**
     * 登录账号失败计数：login:account:{identifier}:fail
     */
    public static final String LOGIN_ACCOUNT_FAIL = "login:account:%s:fail";

    /**
     * 登录账号锁定：login:account:{identifier}:lock
     */
    public static final String LOGIN_ACCOUNT_LOCK = "login:account:%s:lock";

    /**
     * 登录 IP 失败计数：login:ip:{ip}:fail
     */
    public static final String LOGIN_IP_FAIL = "login:ip:%s:fail";

    /**
     * 登录 IP 锁定：login:ip:{ip}:lock
     */
    public static final String LOGIN_IP_LOCK = "login:ip:%s:lock";

}
