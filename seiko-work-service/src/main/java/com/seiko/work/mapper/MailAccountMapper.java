package com.seiko.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.entity.MailAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮箱账号配置 Mapper
 */
@Mapper
public interface MailAccountMapper extends BaseMapper<MailAccount> {
}
