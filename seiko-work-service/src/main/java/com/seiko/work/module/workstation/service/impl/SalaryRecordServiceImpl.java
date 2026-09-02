package com.seiko.work.module.workstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.module.workstation.entity.SalaryRecord;
import com.seiko.work.module.workstation.mapper.SalaryRecordMapper;
import com.seiko.work.module.workstation.service.SalaryRecordService;
import org.springframework.stereotype.Service;

/**
 * 工资记录 Service 实现
 */
@Service
public class SalaryRecordServiceImpl extends ServiceImpl<SalaryRecordMapper, SalaryRecord> implements SalaryRecordService {

    @Override
    public SalaryRecord getByUserIdAndYearMonth(Long userId, String yearMonth) {
        LambdaQueryWrapper<SalaryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalaryRecord::getUserId, userId)
                .eq(SalaryRecord::getYearMonth, yearMonth);
        return baseMapper.selectOne(wrapper);
    }

}
