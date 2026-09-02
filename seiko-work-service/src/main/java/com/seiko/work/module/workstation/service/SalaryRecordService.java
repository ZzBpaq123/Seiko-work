package com.seiko.work.module.workstation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.module.workstation.entity.SalaryRecord;

/**
 * 工资记录 Service
 */
public interface SalaryRecordService extends IService<SalaryRecord> {

    /**
     * 根据用户ID和年月查询工资记录
     *
     * @param userId    用户ID
     * @param yearMonth 年月，格式 yyyy-MM
     * @return 工资记录
     */
    SalaryRecord getByUserIdAndYearMonth(Long userId, String yearMonth);

}
