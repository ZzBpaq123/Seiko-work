package com.seiko.work.module.workstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.module.workstation.entity.WorkTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作事项 Mapper
 */
@Mapper
public interface WorkTaskMapper extends BaseMapper<WorkTask> {
}
