package com.seiko.work.module.workstation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.module.workstation.entity.LeaveRecord;

import java.util.List;

/**
 * 请假记录 Service
 */
public interface LeaveRecordService extends IService<LeaveRecord> {

    /**
     * 根据年份查询请假记录
     *
     * @param userId 用户ID
     * @param year   年份
     * @return 请假记录列表
     */
    List<LeaveRecord> listByYear(Long userId, Integer year);

    /**
     * 统计指定年份已批准的请假天数（按类型）
     *
     * @param userId 用户ID
     * @param year   年份
     * @return 类型 -> 天数
     */
    java.util.Map<String, java.math.BigDecimal> summaryByYear(Long userId, Integer year);

}
