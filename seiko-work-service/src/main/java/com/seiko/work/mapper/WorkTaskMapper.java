package com.seiko.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.entity.WorkTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作事项 Mapper
 */
@Mapper
public interface WorkTaskMapper extends BaseMapper<WorkTask> {
}
