package com.seiko.work.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seiko.work.base.Result;
import com.seiko.work.base.ResultCode;
import com.seiko.work.exception.BusinessException;
import com.seiko.work.dto.LeaveRecordDTO;
import com.seiko.work.entity.LeaveRecord;
import com.seiko.work.service.LeaveRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

import java.math.BigDecimal;
import java.util.Map;

/**
 * 请假记录 Controller
 */
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@Tag(name = "请假记录", description = "请假录入与年度统计")
public class LeaveController {

    private final LeaveRecordService leaveRecordService;

    @GetMapping
    @Operation(summary = "请假列表")
    public Result<Page<LeaveRecord>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer year) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<LeaveRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaveRecord::getUserId, userId);
        if (year != null) {
            wrapper.ge(LeaveRecord::getStartDate, year + "-01-01")
                    .le(LeaveRecord::getEndDate, year + "-12-31");
        }
        wrapper.orderByDesc(LeaveRecord::getStartDate);
        return Result.success(leaveRecordService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/year/{year}/summary")
    @Operation(summary = "年度请假统计")
    public Result<Map<String, BigDecimal>> summary(@PathVariable Integer year) {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(leaveRecordService.summaryByYear(userId, year));
    }

    @GetMapping("/{id}")
    @Operation(summary = "请假详情")
    public Result<LeaveRecord> getById(@PathVariable Long id) {
        LeaveRecord record = getRecordById(id);
        return Result.success(record);
    }

    @PostMapping
    @Operation(summary = "创建请假记录")
    public Result<Void> save(@Valid @RequestBody LeaveRecordDTO dto) {
        LeaveRecord record = new LeaveRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(StpUtil.getLoginIdAsLong());
        leaveRecordService.save(record);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新请假记录")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LeaveRecordDTO dto) {
        LeaveRecord record = getRecordById(id);
        BeanUtils.copyProperties(dto, record);
        leaveRecordService.updateById(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除请假记录")
    public Result<Void> delete(@PathVariable Long id) {
        getRecordById(id);
        leaveRecordService.removeById(id);
        return Result.success();
    }

    private LeaveRecord getRecordById(Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        LeaveRecord record = leaveRecordService.getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return record;
    }

}
