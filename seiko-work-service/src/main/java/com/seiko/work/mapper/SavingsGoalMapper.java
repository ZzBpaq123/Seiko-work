package com.seiko.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.entity.SavingsGoal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 攒钱目标 Mapper
 */
@Mapper
public interface SavingsGoalMapper extends BaseMapper<SavingsGoal> {
}
