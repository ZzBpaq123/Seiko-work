package com.seiko.work.module.workstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.module.workstation.entity.CalendarEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日程事件 Mapper
 */
@Mapper
public interface CalendarEventMapper extends BaseMapper<CalendarEvent> {
}
