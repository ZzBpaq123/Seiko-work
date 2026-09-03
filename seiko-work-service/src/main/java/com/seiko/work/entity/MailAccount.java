package com.seiko.work.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seiko.work.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 邮箱账号配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mail_account")
@Schema(description = "邮箱账号配置")
public class MailAccount extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 邮箱地址
     */
    @TableField("email")
    @Schema(description = "邮箱地址")
    private String email;

    /**
     * 邮箱授权码（仅接收，不返回给前端）
     */
    @TableField("auth_code")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "邮箱授权码")
    private String authCode;

    /**
     * IMAP服务器地址
     */
    @TableField("imap_host")
    @Schema(description = "IMAP服务器地址")
    private String imapHost;

    /**
     * IMAP端口
     */
    @TableField("imap_port")
    @Schema(description = "IMAP端口")
    private Integer imapPort;

    /**
     * 是否启用SSL
     */
    @TableField("ssl_enable")
    @Schema(description = "是否启用SSL")
    private Boolean sslEnable;

}
