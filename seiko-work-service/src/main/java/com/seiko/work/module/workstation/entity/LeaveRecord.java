package com.seiko.work.module.workstation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seiko.work.base.BaseEntity;
import com.seiko.work.module.workstation.enums.LeaveStatusEnum;
import com.seiko.work.module.workstation.enums.LeaveTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 请假记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("leave_record")
@Schema(description = "请假记录实体")
public class LeaveRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 请假类型
     */
    @TableField("leave_type")
    @Schema(description = "请假类型")
    private LeaveTypeEnum leaveType;

    /**
     * 开始日期
     */
    @TableField("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束日期")
    private LocalDate endDate;

    /**
     * 请假天数
     */
    @TableField("days")
    @Schema(description = "请假天数")
    private BigDecimal days;

    /**
     * 事由
     */
    @TableField("reason")
    @Schema(description = "事由")
    private String reason;

    /**
     * 状态
     */
    @TableField("status")
    @Schema(description = "状态")
    private LeaveStatusEnum status;

}
