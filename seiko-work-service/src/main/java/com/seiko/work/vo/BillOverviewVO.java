package com.seiko.work.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 账单概览 VO
 * <p>
 * 一次性返回今日 / 本周 / 本月 / 本年四个周期的汇总，前端首页一个请求即可渲染。
 * 周口径为 ISO 周（周一 ~ 周日），与 {@link com.seiko.work.enums.BillStatPeriodEnum#WEEK} 保持一致。
 */
@Data
@Schema(description = "账单概览：今日/本周/本月/本年")
public class BillOverviewVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "今日汇总")
    private BillSummaryVO today;

    @Schema(description = "本周汇总（周一 ~ 周日）")
    private BillSummaryVO week;

    @Schema(description = "本月汇总")
    private BillSummaryVO month;

    @Schema(description = "本年汇总")
    private BillSummaryVO year;

}
