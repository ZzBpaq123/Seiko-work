package com.seiko.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 攒钱目标档位 DTO
 */
@Data
@Schema(description = "攒钱目标档位")
public class SavingsBucketDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "档位名称不能为空")
    @Size(max = 20, message = "档位名称不能超过 20 个字符")
    @Schema(description = "档位名称，如：储蓄", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "档位比例不能为空")
    @Min(value = 0, message = "档位比例不能小于 0")
    @Max(value = 100, message = "档位比例不能大于 100")
    @Schema(description = "占收入的比例（整数百分比），各档合计必须为 100",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer percent;

    @Schema(description = "是否储蓄档：非储蓄档的金额合计会被当作本月可用预算，与实际支出对比")
    private Boolean saving;

}
