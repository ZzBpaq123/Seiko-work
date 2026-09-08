package com.seiko.work.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 节假日信息 VO
 * 字段与 holiday.ailcc.com allyear 接口返回一致
 */
@Data
@Schema(description = "节假日信息")
public class HolidayVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "日期 yyyy-MM-dd")
    private String date;

    @Schema(description = "类型：0-工作日 1-周末 2-节日 3-调休放假 4-补班")
    private Integer type;

    @Schema(description = "是否放假：1-放假（周末/法定节假日/调休），0-工作日（含补班）")
    private Integer isHoliday;

    @Schema(description = "名称，调休/补班时为对应节假日名，如“国庆节（休）”")
    private String name;

    @Schema(description = "调休/补班位置：1-假后，0-假前，否则为 null")
    private Integer after;

    @Schema(description = "一周中的第几天，1-周一 … 7-周日")
    private Integer week;

    @Schema(description = "调休的节假日名称，仅调休/补班时有值")
    private String target;

    @Schema(description = "农历")
    private String lunar;

    @Schema(description = "其它节气，一般为空")
    private String extraInfo;
}
