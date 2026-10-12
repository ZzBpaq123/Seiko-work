package com.seiko.work.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 攒钱目标档位 VO
 */
@Data
@Schema(description = "攒钱目标档位")
public class SavingsBucketVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "档位名称")
    private String name;

    @Schema(description = "占收入的比例（整数百分比）")
    private Integer percent;

    @Schema(description = "是否储蓄档")
    private Boolean saving;

}
