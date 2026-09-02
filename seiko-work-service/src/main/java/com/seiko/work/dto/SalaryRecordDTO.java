package com.seiko.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工资记录 DTO
 */
@Data
@Schema(description = "工资记录请求参数")
public class SalaryRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "年月不能为空")
    @Schema(description = "年月，格式：yyyy-MM", requiredMode = Schema.RequiredMode.REQUIRED)
    private String yearMonth;

    @NotNull(message = "基本工资不能为空")
    @Schema(description = "基本工资", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal baseSalary;

    @NotNull(message = "绩效工资不能为空")
    @Schema(description = "绩效工资", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal performanceSalary;

    @NotNull(message = "补贴不能为空")
    @Schema(description = "补贴", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal subsidy;

    @NotNull(message = "税前总额不能为空")
    @Schema(description = "税前总额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal grossSalary;

    @NotNull(message = "个税不能为空")
    @Schema(description = "个税", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal tax;

    @NotNull(message = "五险一金个人部分不能为空")
    @Schema(description = "五险一金个人部分", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal socialSecurityPersonal;

    @NotNull(message = "公积金个人部分不能为空")
    @Schema(description = "公积金个人部分", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal housingFundPersonal;

    @NotNull(message = "实发工资不能为空")
    @Schema(description = "实发工资", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal netSalary;

    @Schema(description = "备注")
    private String remark;

}
