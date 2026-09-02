package com.seiko.work.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seiko.work.base.Result;
import com.seiko.work.base.ResultCode;
import com.seiko.work.exception.BusinessException;
import com.seiko.work.dto.CalendarEventDTO;
import com.seiko.work.entity.CalendarEvent;
import com.seiko.work.service.CalendarEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 日程事件 Controller
 */
@RestController
@RequestMapping("/api/calendar-events")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@Tag(name = "日程事件", description = "日程增删改查与范围查询")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @GetMapping
    @Operation(summary = "日程列表")
    public Result<Page<CalendarEvent>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<CalendarEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CalendarEvent::getUserId, userId)
                .orderByDesc(CalendarEvent::getStartTime);
        return Result.success(calendarEventService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/range")
    @Operation(summary = "查询时间范围内的日程")
    public Result<List<CalendarEvent>> range(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(calendarEventService.listByTimeRange(userId, start, end));
    }

    @GetMapping("/{id}")
    @Operation(summary = "日程详情")
    public Result<CalendarEvent> getById(@PathVariable Long id) {
        CalendarEvent event = getEventById(id);
        return Result.success(event);
    }

    @PostMapping
    @Operation(summary = "创建日程")
    public Result<Void> save(@Valid @RequestBody CalendarEventDTO dto) {
        CalendarEvent event = new CalendarEvent();
        BeanUtils.copyProperties(dto, event);
        event.setUserId(StpUtil.getLoginIdAsLong());
        calendarEventService.save(event);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新日程")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CalendarEventDTO dto) {
        CalendarEvent event = getEventById(id);
        BeanUtils.copyProperties(dto, event);
        calendarEventService.updateById(event);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除日程")
    public Result<Void> delete(@PathVariable Long id) {
        getEventById(id);
        calendarEventService.removeById(id);
        return Result.success();
    }

    private CalendarEvent getEventById(Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        CalendarEvent event = calendarEventService.getById(id);
        if (event == null || !event.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return event;
    }

}
