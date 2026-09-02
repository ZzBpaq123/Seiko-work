package com.seiko.work.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seiko.work.base.Result;
import com.seiko.work.base.ResultCode;
import com.seiko.work.exception.BusinessException;
import com.seiko.work.dto.SalaryRecordDTO;
import com.seiko.work.entity.SalaryRecord;
import com.seiko.work.service.SalaryRecordService;
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

/**
 * 工资记录 Controller
 */
@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@Tag(name = "工资记录", description = "工资录入与查询")
public class SalaryController {

    private final SalaryRecordService salaryRecordService;

    @GetMapping
    @Operation(summary = "工资列表")
    public Result<Page<SalaryRecord>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<SalaryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalaryRecord::getUserId, userId)
                .orderByDesc(SalaryRecord::getYearMonth);
        return Result.success(salaryRecordService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "工资详情")
    public Result<SalaryRecord> getById(@PathVariable Long id) {
        SalaryRecord record = getRecordById(id);
        return Result.success(record);
    }

    @PostMapping
    @Operation(summary = "创建工资记录")
    public Result<Void> save(@Valid @RequestBody SalaryRecordDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SalaryRecord exist = salaryRecordService.getByUserIdAndYearMonth(userId, dto.getYearMonth());
        if (exist != null) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "该月份工资记录已存在");
        }
        SalaryRecord record = new SalaryRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);
        salaryRecordService.save(record);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新工资记录")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SalaryRecordDTO dto) {
        SalaryRecord record = getRecordById(id);
        if (!record.getYearMonth().equals(dto.getYearMonth())) {
            Long userId = StpUtil.getLoginIdAsLong();
            SalaryRecord exist = salaryRecordService.getByUserIdAndYearMonth(userId, dto.getYearMonth());
            if (exist != null && !exist.getId().equals(id)) {
                throw new BusinessException(ResultCode.CONFLICT.getCode(), "该月份工资记录已存在");
            }
        }
        BeanUtils.copyProperties(dto, record);
        salaryRecordService.updateById(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除工资记录")
    public Result<Void> delete(@PathVariable Long id) {
        getRecordById(id);
        salaryRecordService.removeById(id);
        return Result.success();
    }

    private SalaryRecord getRecordById(Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        SalaryRecord record = salaryRecordService.getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return record;
    }

}
