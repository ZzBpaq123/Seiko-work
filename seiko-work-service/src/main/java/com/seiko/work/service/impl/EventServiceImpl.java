package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.Event;
import com.seiko.work.mapper.EventMapper;
import com.seiko.work.service.EventService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 日程事件 Service 实现
 */
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

    @Override
    public List<Event> listByTimeRange(Long userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Event::getUserId, userId)
                .lt(Event::getStartTime, endTime)
                .gt(Event::getEndTime, startTime)
                .orderByAsc(Event::getStartTime);
        return baseMapper.selectList(wrapper);
    }

}
