package com.seiko.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账单趋势统计项 VO
 * <p>
 * periodLabel 的格式随统计粒度变化，且字典序与时间序一致，可直接用于前端 X 轴：
 * 按天 2026-10-12、按周 2026-10-12（该周周一）、按月 2026-10、按年 2026。
 */
@Data
@Schema(description = "账单趋势统计项")
public class BillTrendVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "分组标识：按天 yyyy-MM-dd、按周 yyyy-MM-dd（周一）、按月 yyyy-MM、按年 yyyy")
    private String periodLabel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "该分组起始日（含）")
    private LocalDate periodStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "该分组结束日（含）")
    private LocalDate periodEnd;

    @Schema(description = "支出合计（元）")
    private BigDecimal expense;

    @Schema(description = "收入合计（元）")
    private BigDecimal income;

    @Schema(description = "账单笔数")
    private Long billCount;

}
