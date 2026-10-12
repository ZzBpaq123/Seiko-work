package com.seiko.work.dto;

import com.seiko.work.enums.BillTypeEnum;
import com.seiko.work.enums.PaymentMethodEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 账单明细 DTO
 */
@Data
@Schema(description = "账单请求参数")
public class BillDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 255, message = "分类长度不能超过 255 个字符")
    @Schema(description = "分类")
    private String category;

    @NotNull(message = "账单类型不能为空")
    @Schema(description = "账单类型：0-支出，1-收入", requiredMode = Schema.RequiredMode.REQUIRED)
    private BillTypeEnum type;

    /**
     * 金额恒为正数，方向由 type 决定，因此这里下限卡在 0.01；
     * 上限与 DECIMAL(12,2) 对齐：整数位 10 位、小数位 2 位。
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于 0")
    @Digits(integer = 10, fraction = 2, message = "金额最多 10 位整数、2 位小数")
    @Schema(description = "金额（元），恒为正数", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotNull(message = "账单日期不能为空")
    @Schema(description = "账单日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate billDate;

    @Schema(description = "支付方式：0-其他，1-现金，2-微信，3-支付宝，4-储蓄卡，5-信用卡")
    private PaymentMethodEnum paymentMethod;

    @Size(max = 255, message = "备注长度不能超过 255 个字符")
    @Schema(description = "备注")
    private String remark;

}
