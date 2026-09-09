package com.seiko.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seiko.work.entity.Task;
import com.seiko.work.enums.TaskStatusEnum;
import com.seiko.work.mapper.TaskMapper;
import com.seiko.work.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 工作事项 Service 实现
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Override
    public List<Task> listToday(Long userId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId)
                .and(w -> w.le(Task::getPlanDate, today)
                        .ne(Task::getStatus, TaskStatusEnum.DONE))
                .orderByAsc(Task::getPlanDate)
                .orderByDesc(Task::getPriority);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Task> listByPlanDate(Long userId, LocalDate planDate) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId)
                .eq(Task::getPlanDate, planDate)
                .orderByDesc(Task::getPriority)
                .orderByAsc(Task::getStatus);
        return baseMapper.selectList(wrapper);
    }

}
