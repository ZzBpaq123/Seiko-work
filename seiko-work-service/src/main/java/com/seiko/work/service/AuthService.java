package com.seiko.work.service;

import com.seiko.work.dto.LoginDTO;
import com.seiko.work.dto.PhoneLoginDTO;
import com.seiko.work.dto.PhoneRegisterDTO;
import com.seiko.work.dto.RegisterDTO;
import com.seiko.work.dto.SendEmailCodeDTO;
import com.seiko.work.dto.SendPhoneCodeDTO;
import com.seiko.work.vo.LoginVO;
import com.seiko.work.vo.UserVO;

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
    void emailRegister(RegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 请求参数
     * @return 登录信息
     */
    LoginVO emailLogin(LoginDTO dto);

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
