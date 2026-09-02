package com.seiko.work.module.workstation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seiko.work.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 五险一金实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("social_insurance")
@Schema(description = "五险一金实体")
public class SocialInsurance extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 年月，格式：yyyy-MM
     */
    @TableField("year_month")
    @Schema(description = "年月，格式：yyyy-MM")
    private String yearMonth;

    /**
     * 养老保险个人缴纳
     */
    @TableField("pension_personal")
    @Schema(description = "养老保险个人缴纳")
    private BigDecimal pensionPersonal;

    /**
     * 养老保险公司缴纳
     */
    @TableField("pension_company")
    @Schema(description = "养老保险公司缴纳")
    private BigDecimal pensionCompany;

    /**
     * 医疗保险个人缴纳
     */
    @TableField("medical_personal")
    @Schema(description = "医疗保险个人缴纳")
    private BigDecimal medicalPersonal;

    /**
     * 医疗保险公司缴纳
     */
    @TableField("medical_company")
    @Schema(description = "医疗保险公司缴纳")
    private BigDecimal medicalCompany;

    /**
     * 失业保险个人缴纳
     */
    @TableField("unemployment_personal")
    @Schema(description = "失业保险个人缴纳")
    private BigDecimal unemploymentPersonal;

    /**
     * 失业保险公司缴纳
     */
    @TableField("unemployment_company")
    @Schema(description = "失业保险公司缴纳")
    private BigDecimal unemploymentCompany;

    /**
     * 工伤保险公司缴纳
     */
    @TableField("injury_company")
    @Schema(description = "工伤保险公司缴纳")
    private BigDecimal injuryCompany;

    /**
     * 生育保险公司缴纳
     */
    @TableField("maternity_company")
    @Schema(description = "生育保险公司缴纳")
    private BigDecimal maternityCompany;

    /**
     * 公积金个人缴纳
     */
    @TableField("housing_fund_personal")
    @Schema(description = "公积金个人缴纳")
    private BigDecimal housingFundPersonal;

    /**
     * 公积金公司缴纳
     */
    @TableField("housing_fund_company")
    @Schema(description = "公积金公司缴纳")
    private BigDecimal housingFundCompany;

    /**
     * 个人缴纳合计
     */
    @TableField("total_personal")
    @Schema(description = "个人缴纳合计")
    private BigDecimal totalPersonal;

    /**
     * 公司缴纳合计
     */
    @TableField("total_company")
    @Schema(description = "公司缴纳合计")
    private BigDecimal totalCompany;

    /**
     * 备注
     */
    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

}
