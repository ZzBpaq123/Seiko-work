package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.MailAccount;
import com.seiko.work.mapper.MailAccountMapper;
import com.seiko.work.service.MailAccountService;
import org.springframework.stereotype.Service;

/**
 * 邮箱账号配置 Service 实现
 */
@Service
public class MailAccountServiceImpl extends ServiceImpl<MailAccountMapper, MailAccount> implements MailAccountService {

    @Override
    public MailAccount getByUserId(Long userId) {
        LambdaQueryWrapper<MailAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MailAccount::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }

}
