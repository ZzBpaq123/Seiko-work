package com.seiko.work.module.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 手机号注册请求参数
 */
@Data
@Schema(description = "手机号注册请求参数")
public class PhoneRegisterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50之间")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须为6位")
    @Schema(description = "短信验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @Schema(description = "密码（可选，用于后续邮箱密码登录）")
    private String password;

    @Schema(description = "确认密码")
    private String confirmPassword;

    @AssertTrue(message = "两次输入的密码不一致")
    public boolean isPasswordMatch() {
        if (password == null || password.isEmpty()) {
            return true;
        }
        return password.equals(confirmPassword);
    }

}
