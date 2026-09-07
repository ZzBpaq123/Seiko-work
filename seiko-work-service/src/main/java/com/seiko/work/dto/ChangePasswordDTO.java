package com.seiko.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 修改密码（已登录用户）请求参数
 */
@Data
@Schema(description = "修改密码请求参数")
public class ChangePasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    @Schema(description = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;

    @AssertTrue(message = "两次输入的密码不一致")
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }

    @AssertTrue(message = "新密码不能与原密码相同")
    public boolean isPasswordChanged() {
        return oldPassword == null || password == null || !oldPassword.equals(password);
    }

}
