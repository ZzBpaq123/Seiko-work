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
 * 工资记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("salary_record")
@Schema(description = "工资记录实体")
public class SalaryRecord extends BaseEntity {

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
     * 基本工资
     */
    @TableField("base_salary")
    @Schema(description = "基本工资")
    private BigDecimal baseSalary;

    /**
     * 绩效工资
     */
    @TableField("performance_salary")
    @Schema(description = "绩效工资")
    private BigDecimal performanceSalary;

    /**
     * 补贴
     */
    @TableField("subsidy")
    @Schema(description = "补贴")
    private BigDecimal subsidy;

    /**
     * 税前总额
     */
    @TableField("gross_salary")
    @Schema(description = "税前总额")
    private BigDecimal grossSalary;

    /**
     * 个税
     */
    @TableField("tax")
    @Schema(description = "个税")
    private BigDecimal tax;

    /**
     * 五险一金个人部分
     */
    @TableField("social_security_personal")
    @Schema(description = "五险一金个人部分")
    private BigDecimal socialSecurityPersonal;

    /**
     * 公积金个人部分
     */
    @TableField("housing_fund_personal")
    @Schema(description = "公积金个人部分")
    private BigDecimal housingFundPersonal;

    /**
     * 实发工资
     */
    @TableField("net_salary")
    @Schema(description = "实发工资")
    private BigDecimal netSalary;

    /**
     * 备注
     */
    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

}
