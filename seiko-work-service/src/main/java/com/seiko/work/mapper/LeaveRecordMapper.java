package com.seiko.work.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seiko.work.entity.LeaveRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 请假记录 Mapper
 */
@Mapper
public interface LeaveRecordMapper extends BaseMapper<LeaveRecord> {
}
