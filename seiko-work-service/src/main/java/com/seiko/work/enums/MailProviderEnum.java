package com.seiko.work.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 常见邮箱服务商 IMAP 默认配置
 */
@Getter
public enum MailProviderEnum {

    /**
     * QQ邮箱
     */
    QQ("qq.com", "QQ邮箱", "imap.qq.com", 993, true),

    /**
     * Foxmail邮箱
     */
    FOXMAIL("foxmail.com", "Foxmail邮箱", "imap.foxmail.com", 993, true),

    /**
     * 腾讯企业邮箱
     */
    EXMAIL("exmail.qq.com", "腾讯企业邮箱", "imap.exmail.qq.com", 993, true),

    /**
     * 163邮箱
     */
    NETEASE_163("163.com", "163邮箱", "imap.163.com", 993, true),

    /**
     * 126邮箱
     */
    NETEASE_126("126.com", "126邮箱", "imap.126.com", 993, true),

    /**
     * yeah.net邮箱
     */
    YEAH("yeah.net", "yeah.net邮箱", "imap.yeah.net", 993, true),

    /**
     * 新浪邮箱
     */
    SINA("sina.com", "新浪邮箱", "imap.sina.com", 993, true),

    /**
     * 搜狐邮箱
     */
    SOHU("sohu.com", "搜狐邮箱", "imap.sohu.com", 993, true),

    /**
     * 阿里云邮箱
     */
    ALIYUN("aliyun.com", "阿里云邮箱", "imap.aliyun.com", 993, true),

    /**
     * 139邮箱
     */
    MAIL_139("139.com", "139邮箱", "imap.139.com", 993, true),

    /**
     * Gmail
     */
    GMAIL("gmail.com", "Gmail", "imap.gmail.com", 993, true),

    /**
     * Outlook
     */
    OUTLOOK("outlook.com", "Outlook", "outlook.office365.com", 993, true),

    /**
     * Hotmail
     */
    HOTMAIL("hotmail.com", "Hotmail", "outlook.office365.com", 993, true);

    /**
     * 邮箱域名后缀（@后面的部分）
     */
    private final String domain;

    /**
     * 服务商名称
     */
    private final String description;

    /**
     * IMAP服务器地址
     */
    private final String imapHost;

    /**
     * IMAP端口
     */
    private final Integer imapPort;

    /**
     * 是否启用SSL
     */
    private final Boolean sslEnable;

    MailProviderEnum(String domain, String description, String imapHost, Integer imapPort, Boolean sslEnable) {
        this.domain = domain;
        this.description = description;
        this.imapHost = imapHost;
        this.imapPort = imapPort;
        this.sslEnable = sslEnable;
    }

    /**
     * 根据邮箱地址匹配服务商
     *
     * @param email 邮箱地址
     * @return 匹配的服务商，未匹配返回 null
     */
    public static MailProviderEnum resolve(String email) {
        if (email == null) {
            return null;
        }
        int atIndex = email.lastIndexOf('@');
        if (atIndex < 0) {
            return null;
        }
        String domain = email.substring(atIndex + 1).trim().toLowerCase();
        for (MailProviderEnum provider : values()) {
            if (provider.domain.equals(domain)) {
                return provider;
            }
        }
        return null;
    }

    public static List<MailProviderEnum> all() {
        return Arrays.asList(values());
    }

}
