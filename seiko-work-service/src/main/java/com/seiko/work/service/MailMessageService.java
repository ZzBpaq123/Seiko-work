package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.entity.MailMessage;

/**
 * 邮件 Service
 */
public interface MailMessageService extends IService<MailMessage> {

    /**
     * 根据用户ID和邮件UID查询邮件
     *
     * @param userId     用户ID
     * @param messageUid 邮件UID
     * @return 邮件
     */
    MailMessage getByUserIdAndMessageUid(Long userId, String messageUid);

}
