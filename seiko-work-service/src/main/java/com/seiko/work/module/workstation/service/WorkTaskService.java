package com.seiko.work.module.workstation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.module.workstation.entity.WorkTask;

import java.time.LocalDate;
import java.util.List;

/**
 * 工作事项 Service
 */
public interface WorkTaskService extends IService<WorkTask> {

    /**
     * 查询今日工作事项（当天及逾期未完成的）
     *
     * @param userId 用户ID
     * @return 工作事项列表
     */
    List<WorkTask> listToday(Long userId);

    /**
     * 根据计划日期查询
     *
     * @param userId   用户ID
     * @param planDate 计划日期
     * @return 工作事项列表
     */
    List<WorkTask> listByPlanDate(Long userId, LocalDate planDate);

}
