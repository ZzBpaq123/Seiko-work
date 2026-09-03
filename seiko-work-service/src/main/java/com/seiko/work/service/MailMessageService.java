package com.seiko.work.service;

import com.seiko.work.entity.MailMessage;

import java.util.List;

/**
 * 邮件 Service（通过 IMAP 实时获取，不持久化）
 */
public interface MailMessageService {

    /**
     * 获取收件箱所有邮件（按收取时间倒序，不含正文）
     *
     * @param userId 用户ID
     * @return 邮件列表
     */
    List<MailMessage> listAll(Long userId);

    /**
     * 根据邮件UID获取邮件详情（含正文）
     *
     * @param userId     用户ID
     * @param messageUid 邮件UID
     * @return 邮件详情
     */
    MailMessage getDetail(Long userId, String messageUid);

    /**
     * 标记邮件已读
     *
     * @param userId     用户ID
     * @param messageUid 邮件UID
     */
    void markRead(Long userId, String messageUid);

}
