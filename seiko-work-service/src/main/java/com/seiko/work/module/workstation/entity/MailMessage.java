package com.seiko.work.module.workstation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seiko.work.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * 邮件实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mail_message")
@Schema(description = "邮件实体")
public class MailMessage extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 邮件原始UID
     */
    @TableField("message_uid")
    @Schema(description = "邮件原始UID")
    private String messageUid;

    /**
     * 发件人邮箱
     */
    @TableField("from_address")
    @Schema(description = "发件人邮箱")
    private String fromAddress;

    /**
     * 发件人名称
     */
    @TableField("from_name")
    @Schema(description = "发件人名称")
    private String fromName;

    /**
     * 主题
     */
    @TableField("subject")
    @Schema(description = "主题")
    private String subject;

    /**
     * 纯文本正文
     */
    @TableField("content_text")
    @Schema(description = "纯文本正文")
    private String contentText;

    /**
     * HTML正文（原始内容）
     */
    @TableField("content_html")
    @Schema(description = "HTML正文")
    private String contentHtml;

    /**
     * 收取时间
     */
    @TableField("receive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "收取时间")
    private Date receiveTime;

    /**
     * 是否已读：0-未读，1-已读
     */
    @TableField("is_read")
    @Schema(description = "是否已读：0-未读，1-已读")
    private Integer isRead;

    /**
     * 是否有附件：0-无，1-有
     */
    @TableField("has_attachment")
    @Schema(description = "是否有附件：0-无，1-有")
    private Integer hasAttachment;

}
