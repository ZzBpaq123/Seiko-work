package com.seiko.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.entity.MailMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件 Mapper
 */
@Mapper
public interface MailMessageMapper extends BaseMapper<MailMessage> {
}
