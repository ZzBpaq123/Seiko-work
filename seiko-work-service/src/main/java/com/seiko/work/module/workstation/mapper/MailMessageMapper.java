package com.seiko.work.module.workstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.module.workstation.entity.MailMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件 Mapper
 */
@Mapper
public interface MailMessageMapper extends BaseMapper<MailMessage> {
}
