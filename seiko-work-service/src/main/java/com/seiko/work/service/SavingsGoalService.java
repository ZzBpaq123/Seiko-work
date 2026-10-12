package com.seiko.work.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seiko.work.dto.SavingsGoalDTO;
import com.seiko.work.entity.SavingsGoal;
import com.seiko.work.vo.SavingsGoalVO;

/**
 * 攒钱目标 Service
 */
public interface SavingsGoalService extends IService<SavingsGoal> {

    /**
     * 查询当前用户的攒钱目标
     *
     * @param userId 用户ID
     * @return 攒钱目标，未配置时返回 null
     */
    SavingsGoalVO getByUserId(Long userId);

    /**
     * 保存攒钱目标（不存在则新建，已存在则整体覆盖）
     *
     * @param userId 用户ID
     * @param dto    请求参数
     */
    void saveGoal(Long userId, SavingsGoalDTO dto);

}
