package com.seiko.work.module.workstation.dto;

import com.seiko.work.module.workstation.enums.LeaveStatusEnum;
import com.seiko.work.module.workstation.enums.LeaveTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 请假记录 DTO
 */
@Data
@Schema(description = "请假记录请求参数")
public class LeaveRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "请假类型不能为空")
    @Schema(description = "请假类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private LeaveTypeEnum leaveType;

    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;

    @NotNull(message = "请假天数不能为空")
    @Schema(description = "请假天数", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal days;

    @Schema(description = "事由")
    private String reason;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private LeaveStatusEnum status;

}
