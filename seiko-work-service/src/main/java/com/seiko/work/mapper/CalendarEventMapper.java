package com.seiko.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.entity.CalendarEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日程事件 Mapper
 */
@Mapper
public interface CalendarEventMapper extends BaseMapper<CalendarEvent> {
}
