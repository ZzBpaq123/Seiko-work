package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.WorkTask;
import com.seiko.work.enums.TaskStatusEnum;
import com.seiko.work.mapper.WorkTaskMapper;
import com.seiko.work.service.WorkTaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 工作事项 Service 实现
 */
@Service
public class WorkTaskServiceImpl extends ServiceImpl<WorkTaskMapper, WorkTask> implements WorkTaskService {

    @Override
    public List<WorkTask> listToday(Long userId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<WorkTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkTask::getUserId, userId)
                .and(w -> w.le(WorkTask::getPlanDate, today)
                        .ne(WorkTask::getStatus, TaskStatusEnum.DONE))
                .orderByAsc(WorkTask::getPlanDate)
                .orderByDesc(WorkTask::getPriority);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<WorkTask> listByPlanDate(Long userId, LocalDate planDate) {
        LambdaQueryWrapper<WorkTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkTask::getUserId, userId)
                .eq(WorkTask::getPlanDate, planDate)
                .orderByDesc(WorkTask::getPriority)
                .orderByAsc(WorkTask::getStatus);
        return baseMapper.selectList(wrapper);
    }

}
