package com.seiko.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seiko.work.entity.Bill;
import com.seiko.work.enums.BillTypeEnum;
import com.seiko.work.enums.PaymentMethodEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 账单明细 VO
 */
@Data
@Schema(description = "账单明细响应")
public class BillVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "账单类型")
    private BillTypeEnum type;

    @Schema(description = "金额（元），恒为正数")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "账单日期")
    private LocalDate billDate;

    @Schema(description = "支付方式")
    private PaymentMethodEnum paymentMethod;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    public static BillVO from(Bill bill) {
        BillVO vo = new BillVO();
        BeanUtils.copyProperties(bill, vo);
        return vo;
    }

}
