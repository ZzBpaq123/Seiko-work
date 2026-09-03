package com.seiko.work.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 邮件信息（通过 IMAP 实时获取，不持久化）
 */
@Data
@Schema(description = "邮件信息")
public class MailMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邮件原始UID
     */
    @Schema(description = "邮件原始UID")
    private String messageUid;

    /**
     * 发件人邮箱
     */
    @Schema(description = "发件人邮箱")
    private String fromAddress;

    /**
     * 发件人名称
     */
    @Schema(description = "发件人名称")
    private String fromName;

    /**
     * 主题
     */
    @Schema(description = "主题")
    private String subject;

    /**
     * 纯文本正文
     */
    @Schema(description = "纯文本正文")
    private String contentText;

    /**
     * HTML正文（原始内容）
     */
    @Schema(description = "HTML正文")
    private String contentHtml;

    /**
     * 收取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "收取时间")
    private Date receiveTime;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读")
    private Boolean isRead;

    /**
     * 是否有附件
     */
    @Schema(description = "是否有附件")
    private Boolean hasAttachment;

}
