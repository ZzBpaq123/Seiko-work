package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.entity.Mail;

/**
 * 邮箱账号配置 Service
 */
public interface MailService extends IService<Mail> {

    /**
     * 根据用户ID查询邮箱账号配置
     *
     * @param userId 用户ID
     * @return 邮箱账号配置
     */
    Mail getByUserId(Long userId);

}
