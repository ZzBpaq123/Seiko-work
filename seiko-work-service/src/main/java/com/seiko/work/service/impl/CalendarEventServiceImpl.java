package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.CalendarEvent;
import com.seiko.work.mapper.CalendarEventMapper;
import com.seiko.work.service.CalendarEventService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 日程事件 Service 实现
 */
@Service
public class CalendarEventServiceImpl extends ServiceImpl<CalendarEventMapper, CalendarEvent> implements CalendarEventService {

    @Override
    public List<CalendarEvent> listByTimeRange(Long userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<CalendarEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalendarEvent::getUserId, userId)
                .lt(CalendarEvent::getStartTime, endTime)
                .gt(CalendarEvent::getEndTime, startTime)
                .orderByAsc(CalendarEvent::getStartTime);
        return baseMapper.selectList(wrapper);
    }

}
