package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.Mail;
import com.seiko.work.mapper.MailMapper;
import com.seiko.work.service.MailService;
import org.springframework.stereotype.Service;

/**
 * 邮箱账号配置 Service 实现
 */
@Service
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {

    @Override
    public Mail getByUserId(Long userId) {
        LambdaQueryWrapper<Mail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Mail::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }

}
