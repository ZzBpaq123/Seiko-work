package com.seiko.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱账号配置 DTO
 */
@Data
@Schema(description = "邮箱账号配置请求参数")
public class MailAccountDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "邮箱地址不能为空")
    @Schema(description = "邮箱地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "授权码不能为空")
    @Schema(description = "邮箱授权码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String authCode;

    @NotBlank(message = "IMAP服务器地址不能为空")
    @Schema(description = "IMAP服务器地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String imapHost;

    @NotNull(message = "IMAP端口不能为空")
    @Schema(description = "IMAP端口", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer imapPort;

    @NotNull(message = "是否启用SSL不能为空")
    @Schema(description = "是否启用SSL", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean sslEnable;

}
