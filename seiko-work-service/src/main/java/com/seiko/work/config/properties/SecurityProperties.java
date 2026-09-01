package com.seiko.work.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 安全配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog.security")
public class SecurityProperties {

    /**
     * 登录限流配置
     */
    private LoginRateLimitProperties loginRateLimit = new LoginRateLimitProperties();

    /**
     * 邮箱验证码配置
     */
    private EmailCodeProperties emailCode = new EmailCodeProperties();

    @Data
    public static class LoginRateLimitProperties {

        /**
         * 是否启用
         */
        private Boolean enabled = true;

        /**
         * 单个账号在窗口期内最大失败次数
         */
        private Integer maxAttempts = 3;

        /**
         * 单个 IP 在窗口期内最大失败次数
         */
        private Integer ipMaxAttempts = 5;

        /**
         * 计数窗口时长（秒）
         */
        private Long windowSeconds = 300L;

        /**
         * 触发限制后锁定时长（秒）
         */
        private Long lockSeconds = 900L;
    }

    @Data
    public static class EmailCodeProperties {

        /**
         * 是否启用
         */
        private Boolean enabled = true;

        /**
         * 验证码长度
         */
        private Integer codeLength = 6;

        /**
         * 验证码有效期（秒）
         */
        private Long codeTtlSeconds = 300L;

        /**
         * 同一邮箱重发冷却时间（秒）
         */
        private Long resendCooldownSeconds = 60L;

        /**
         * 同一 IP 每小时最多发送次数
         */
        private Integer ipMaxSendsPerHour = 3;

        /**
         * IP 超限锁定时长（秒）
         */
        private Long ipLockSeconds = 3600L;

        /**
         * 邮件发件人显示名称
         */
        private String senderName = "Seiko";

        /**
         * 邮件主题
         */
        private String subject = "Seiko 邮箱验证码";
    }
}
