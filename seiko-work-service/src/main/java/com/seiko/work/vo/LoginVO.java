package com.seiko.work.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录返回 VO
 */
@Data
@Schema(description = "登录返回信息")
public class LoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "Token 名称")
    private String tokenName;

    @Schema(description = "用户信息")
    private UserVO user;

}
