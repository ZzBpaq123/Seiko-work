package com.seiko.work.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seiko.work.base.Result;
import com.seiko.work.base.ResultCode;
import com.seiko.work.exception.BusinessException;
import com.seiko.work.dto.SocialInsuranceDTO;
import com.seiko.work.entity.SocialInsurance;
import com.seiko.work.service.SocialInsuranceService;
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
 * 五险一金 Controller
 */
@RestController
@RequestMapping("/api/social-insurance")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
@Tag(name = "五险一金", description = "五险一金录入与月度明细")
public class SocialInsuranceController {

    private final SocialInsuranceService socialInsuranceService;

    @GetMapping
    @Operation(summary = "五险一金列表")
    public Result<Page<SocialInsurance>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<SocialInsurance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SocialInsurance::getUserId, userId)
                .orderByDesc(SocialInsurance::getYearMonth);
        return Result.success(socialInsuranceService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/month/{ym}")
    @Operation(summary = "按年月查询五险一金")
    public Result<SocialInsurance> getByMonth(@PathVariable String ym) {
        Long userId = StpUtil.getLoginIdAsLong();
        SocialInsurance record = socialInsuranceService.getByUserIdAndYearMonth(userId, ym);
        return Result.success(record);
    }

    @GetMapping("/{id}")
    @Operation(summary = "五险一金详情")
    public Result<SocialInsurance> getById(@PathVariable Long id) {
        SocialInsurance record = getRecordById(id);
        return Result.success(record);
    }

    @PostMapping
    @Operation(summary = "创建五险一金记录")
    public Result<Void> save(@Valid @RequestBody SocialInsuranceDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SocialInsurance exist = socialInsuranceService.getByUserIdAndYearMonth(userId, dto.getYearMonth());
        if (exist != null) {
            throw new BusinessException(ResultCode.CONFLICT.getCode(), "该月份五险一金记录已存在");
        }
        SocialInsurance record = new SocialInsurance();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);
        socialInsuranceService.save(record);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新五险一金记录")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SocialInsuranceDTO dto) {
        SocialInsurance record = getRecordById(id);
        if (!record.getYearMonth().equals(dto.getYearMonth())) {
            Long userId = StpUtil.getLoginIdAsLong();
            SocialInsurance exist = socialInsuranceService.getByUserIdAndYearMonth(userId, dto.getYearMonth());
            if (exist != null && !exist.getId().equals(id)) {
                throw new BusinessException(ResultCode.CONFLICT.getCode(), "该月份五险一金记录已存在");
            }
        }
        BeanUtils.copyProperties(dto, record);
        socialInsuranceService.updateById(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除五险一金记录")
    public Result<Void> delete(@PathVariable Long id) {
        getRecordById(id);
        socialInsuranceService.removeById(id);
        return Result.success();
    }

    private SocialInsurance getRecordById(Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        SocialInsurance record = socialInsuranceService.getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return record;
    }

}
