package com.seiko.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账单周期汇总 VO
 * <p>
 * 这里的 startDate / endDate 是<b>闭区间</b>（均含当天），与接口入参语义一致；
 * 转成 SQL 用的半开区间 [startDate, endDate + 1) 由 Service 层完成。
 */
@Data
@Schema(description = "账单周期汇总")
public class BillSummaryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "统计起始日（含）")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "统计结束日（含）")
    private LocalDate endDate;

    @Schema(description = "收入合计（元）")
    private BigDecimal income;

    @Schema(description = "支出合计（元）")
    private BigDecimal expense;

    @Schema(description = "结余 = 收入 - 支出（元）")
    private BigDecimal balance;

    @Schema(description = "账单笔数")
    private Long billCount;

}
