package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.entity.CalendarEvent;

import java.util.Date;
import java.util.List;

/**
 * 日程事件 Service
 */
public interface CalendarEventService extends IService<CalendarEvent> {

    /**
     * 查询时间范围内的日程事件
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日程事件列表
     */
    List<CalendarEvent> listByTimeRange(Long userId, Date startTime, Date endTime);

}
