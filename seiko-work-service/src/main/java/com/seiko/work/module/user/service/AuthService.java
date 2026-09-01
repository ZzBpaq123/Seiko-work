package com.seiko.work.module.user.service;

import com.seiko.work.module.user.dto.LoginDTO;
import com.seiko.work.module.user.dto.PhoneLoginDTO;
import com.seiko.work.module.user.dto.PhoneRegisterDTO;
import com.seiko.work.module.user.dto.RegisterDTO;
import com.seiko.work.module.user.dto.SendEmailCodeDTO;
import com.seiko.work.module.user.dto.SendPhoneCodeDTO;
import com.seiko.work.module.user.vo.LoginVO;
import com.seiko.work.module.user.vo.UserVO;

/**
 * 认证 Service
 */
public interface AuthService {

    /**
     * 发送邮箱验证码
     *
     * @param dto 请求参数
     */
    void sendEmailCode(SendEmailCodeDTO dto);

    /**
     * 用户注册
     *
     * @param dto 请求参数
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 请求参数
     * @return 登录信息
     */
    LoginVO login(LoginDTO dto);

    /**
     * 发送手机验证码
     *
     * @param dto 请求参数
     */
    void sendPhoneCode(SendPhoneCodeDTO dto);

    /**
     * 手机号注册
     *
     * @param dto 请求参数
     */
    void phoneRegister(PhoneRegisterDTO dto);

    /**
     * 手机号验证码登录
     *
     * @param dto 请求参数
     * @return 登录信息
     */
    LoginVO phoneLogin(PhoneLoginDTO dto);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserVO getCurrentUser();

}
