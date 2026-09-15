package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.dto.EventDTO;
import com.seiko.work.entity.Event;
import com.seiko.work.vo.EventVO;

import java.util.Date;
import java.util.List;

/**
 * 日程事件 Service
 */
public interface EventService extends IService<Event> {

    /**
     * 分页查询日程事件
     *
     * @param userId  用户ID
     * @param current 当前页
     * @param size    每页条数
     * @return 分页结果（VO）
     */
    Page<EventVO> pageEvents(Long userId, long current, long size);

    /**
     * 查询时间范围内的日程事件（end_time 为空的事件按开始时间兜底参与匹配）
     *
     * @param userId    用户ID
     * @param startTime 范围开始
     * @param endTime   范围结束（必须晚于 startTime）
     * @return 日程事件列表（VO）
     */
    List<EventVO> listByTimeRange(Long userId, Date startTime, Date endTime);

    /**
     * 查询属主校验后的日程事件，不存在或无权限时抛业务异常
     *
     * @param id     日程事件ID
     * @param userId 用户ID
     * @return 日程事件（VO）
     */
    EventVO getOwned(Long id, Long userId);

    /**
     * 创建日程事件
     *
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void createEvent(Long userId, EventDTO dto);

    /**
     * 更新日程事件（仅更新 DTO 中非空字段）
     *
     * @param id     日程事件ID
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void updateEvent(Long id, Long userId, EventDTO dto);

    /**
     * 删除日程事件（属主校验）
     *
     * @param id     日程事件ID
     * @param userId 用户ID
     */
    void deleteEvent(Long id, Long userId);

}
