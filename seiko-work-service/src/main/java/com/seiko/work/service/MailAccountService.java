package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.entity.MailAccount;

/**
 * 邮箱账号配置 Service
 */
public interface MailAccountService extends IService<MailAccount> {

    /**
     * 根据用户ID查询邮箱账号配置
     *
     * @param userId 用户ID
     * @return 邮箱账号配置
     */
    MailAccount getByUserId(Long userId);

}
