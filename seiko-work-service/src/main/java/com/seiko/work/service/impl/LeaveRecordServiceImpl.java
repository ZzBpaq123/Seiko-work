package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.LeaveRecord;
import com.seiko.work.enums.LeaveStatusEnum;
import com.seiko.work.mapper.LeaveRecordMapper;
import com.seiko.work.service.LeaveRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请假记录 Service 实现
 */
@Service
public class LeaveRecordServiceImpl extends ServiceImpl<LeaveRecordMapper, LeaveRecord> implements LeaveRecordService {

    @Override
    public List<LeaveRecord> listByYear(Long userId, Integer year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        LambdaQueryWrapper<LeaveRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaveRecord::getUserId, userId)
                .ge(LeaveRecord::getStartDate, start)
                .le(LeaveRecord::getEndDate, end)
                .orderByDesc(LeaveRecord::getStartDate);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Map<String, BigDecimal> summaryByYear(Long userId, Integer year) {
        List<LeaveRecord> records = listByYear(userId, year);
        Map<String, BigDecimal> summary = new HashMap<>();
        for (LeaveRecord record : records) {
            if (record.getStatus() != LeaveStatusEnum.APPROVED) {
                continue;
            }
            String type = record.getLeaveType().getValue();
            BigDecimal days = record.getDays();
            summary.merge(type, days, BigDecimal::add);
        }
        return summary;
    }

}
