package com.seiko.work.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.module.user.entity.User;

/**
 * 用户 Service
 */
public interface UserService extends IService<User> {

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户
     */
    User getByEmail(String email);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户
     */
    User getByPhone(String phone);
}
