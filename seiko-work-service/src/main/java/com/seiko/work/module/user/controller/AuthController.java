package com.seiko.work.module.user.controller;

import com.seiko.work.base.Result;
import com.seiko.work.module.user.dto.LoginDTO;
import com.seiko.work.module.user.dto.PhoneLoginDTO;
import com.seiko.work.module.user.dto.PhoneRegisterDTO;
import com.seiko.work.module.user.dto.RegisterDTO;
import com.seiko.work.module.user.dto.SendEmailCodeDTO;
import com.seiko.work.module.user.dto.SendPhoneCodeDTO;
import com.seiko.work.module.user.service.AuthService;
import com.seiko.work.module.user.vo.LoginVO;
import com.seiko.work.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证管理 Controller
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证管理", description = "用户登录、注册、登出、验证码")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-code")
    @Operation(summary = "发送邮箱验证码")
    public Result<Void> sendEmailCode(@Valid @RequestBody SendEmailCodeDTO dto) {
        authService.sendEmailCode(dto);
        return Result.success();
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录（邮箱+密码）")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @PostMapping("/phone/send-code")
    @Operation(summary = "发送手机验证码")
    public Result<Void> sendPhoneCode(@Valid @RequestBody SendPhoneCodeDTO dto) {
        authService.sendPhoneCode(dto);
        return Result.success();
    }

    @PostMapping("/phone/register")
    @Operation(summary = "手机号注册")
    public Result<Void> phoneRegister(@Valid @RequestBody PhoneRegisterDTO dto) {
        authService.phoneRegister(dto);
        return Result.success();
    }

    @PostMapping("/phone/login")
    @Operation(summary = "手机号验证码登录")
    public Result<LoginVO> phoneLogin(@Valid @RequestBody PhoneLoginDTO dto) {
        return Result.success(authService.phoneLogin(dto));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前登录用户信息")
    public Result<UserVO> info() {
        return Result.success(authService.getCurrentUser());
    }

}
