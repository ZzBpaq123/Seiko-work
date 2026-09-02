package com.seiko.work.module.workstation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.module.workstation.entity.SocialInsurance;

/**
 * 五险一金 Service
 */
public interface SocialInsuranceService extends IService<SocialInsurance> {

    /**
     * 根据用户ID和年月查询五险一金记录
     *
     * @param userId    用户ID
     * @param yearMonth 年月，格式 yyyy-MM
     * @return 五险一金记录
     */
    SocialInsurance getByUserIdAndYearMonth(Long userId, String yearMonth);

}
