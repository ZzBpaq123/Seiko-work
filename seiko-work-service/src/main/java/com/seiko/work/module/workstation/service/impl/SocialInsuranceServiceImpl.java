package com.seiko.work.module.workstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.module.workstation.entity.SocialInsurance;
import com.seiko.work.module.workstation.mapper.SocialInsuranceMapper;
import com.seiko.work.module.workstation.service.SocialInsuranceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 五险一金 Service 实现
 */
@Service
public class SocialInsuranceServiceImpl extends ServiceImpl<SocialInsuranceMapper, SocialInsurance> implements SocialInsuranceService {

    @Override
    public SocialInsurance getByUserIdAndYearMonth(Long userId, String yearMonth) {
        LambdaQueryWrapper<SocialInsurance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SocialInsurance::getUserId, userId)
                .eq(SocialInsurance::getYearMonth, yearMonth);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean save(SocialInsurance entity) {
        calculateTotal(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(SocialInsurance entity) {
        calculateTotal(entity);
        return super.updateById(entity);
    }

    private void calculateTotal(SocialInsurance entity) {
        BigDecimal totalPersonal = defaultZero(entity.getPensionPersonal())
                .add(defaultZero(entity.getMedicalPersonal()))
                .add(defaultZero(entity.getUnemploymentPersonal()))
                .add(defaultZero(entity.getHousingFundPersonal()));
        BigDecimal totalCompany = defaultZero(entity.getPensionCompany())
                .add(defaultZero(entity.getMedicalCompany()))
                .add(defaultZero(entity.getUnemploymentCompany()))
                .add(defaultZero(entity.getInjuryCompany()))
                .add(defaultZero(entity.getMaternityCompany()))
                .add(defaultZero(entity.getHousingFundCompany()));
        entity.setTotalPersonal(totalPersonal);
        entity.setTotalCompany(totalCompany);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

}
