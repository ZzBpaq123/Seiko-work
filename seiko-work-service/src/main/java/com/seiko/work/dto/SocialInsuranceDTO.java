package com.seiko.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 五险一金 DTO
 */
@Data
@Schema(description = "五险一金请求参数")
public class SocialInsuranceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "年月不能为空")
    @Schema(description = "年月，格式：yyyy-MM", requiredMode = Schema.RequiredMode.REQUIRED)
    private String yearMonth;

    @NotNull(message = "养老保险个人缴纳不能为空")
    @Schema(description = "养老保险个人缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal pensionPersonal;

    @NotNull(message = "养老保险公司缴纳不能为空")
    @Schema(description = "养老保险公司缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal pensionCompany;

    @NotNull(message = "医疗保险个人缴纳不能为空")
    @Schema(description = "医疗保险个人缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal medicalPersonal;

    @NotNull(message = "医疗保险公司缴纳不能为空")
    @Schema(description = "医疗保险公司缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal medicalCompany;

    @NotNull(message = "失业保险个人缴纳不能为空")
    @Schema(description = "失业保险个人缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unemploymentPersonal;

    @NotNull(message = "失业保险公司缴纳不能为空")
    @Schema(description = "失业保险公司缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unemploymentCompany;

    @NotNull(message = "工伤保险公司缴纳不能为空")
    @Schema(description = "工伤保险公司缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal injuryCompany;

    @NotNull(message = "生育保险公司缴纳不能为空")
    @Schema(description = "生育保险公司缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal maternityCompany;

    @NotNull(message = "公积金个人缴纳不能为空")
    @Schema(description = "公积金个人缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal housingFundPersonal;

    @NotNull(message = "公积金公司缴纳不能为空")
    @Schema(description = "公积金公司缴纳", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal housingFundCompany;

    @Schema(description = "备注")
    private String remark;

}
