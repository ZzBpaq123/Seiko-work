package com.seiko.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seiko.work.entity.Mail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 邮箱账号配置 VO（不含授权码等敏感字段）
 */
@Data
@Schema(description = "邮箱账号配置响应")
public class MailAccountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "IMAP服务器地址")
    private String imapHost;

    @Schema(description = "IMAP端口")
    private Integer imapPort;

    @Schema(description = "是否启用SSL")
    private Boolean sslEnable;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    public static MailAccountVO from(Mail account) {
        MailAccountVO vo = new MailAccountVO();
        BeanUtils.copyProperties(account, vo);
        return vo;
    }

}
