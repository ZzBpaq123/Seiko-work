package com.seiko.work.module.workstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.module.workstation.entity.LeaveRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假记录 Mapper
 */
@Mapper
public interface LeaveRecordMapper extends BaseMapper<LeaveRecord> {
}
