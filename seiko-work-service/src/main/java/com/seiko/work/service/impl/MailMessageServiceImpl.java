package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.MailMessage;
import com.seiko.work.mapper.MailMessageMapper;
import com.seiko.work.service.MailMessageService;
import org.springframework.stereotype.Service;

/**
 * 邮件 Service 实现
 */
@Service
public class MailMessageServiceImpl extends ServiceImpl<MailMessageMapper, MailMessage> implements MailMessageService {

    @Override
    public MailMessage getByUserIdAndMessageUid(Long userId, String messageUid) {
        LambdaQueryWrapper<MailMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailMessage::getUserId, userId)
                .eq(MailMessage::getMessageUid, messageUid);
        return baseMapper.selectOne(wrapper);
    }

}
