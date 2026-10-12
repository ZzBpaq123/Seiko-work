package com.seiko.work.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 账单分类统计项 VO
 */
@Data
@Schema(description = "账单分类统计项")
public class BillCategoryStatVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类，账单未填分类时兜底为「未分类」")
    private String category;

    @Schema(description = "支出合计（元）")
    private BigDecimal expense;

    @Schema(description = "收入合计（元）")
    private BigDecimal income;

    @Schema(description = "账单笔数")
    private Long billCount;

    /**
     * 该分类支出占「全部支出」的百分比，保留两位小数，由 Service 层计算填充。
     */
    @Schema(description = "支出占比（%），保留两位小数")
    private BigDecimal expensePercent;

}
