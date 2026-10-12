package com.seiko.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 攒钱目标 DTO
 */
@Data
@Schema(description = "攒钱目标请求参数")
public class SavingsGoalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 50, message = "目标名称不能超过 50 个字符")
    @Schema(description = "目标名称，留空则使用默认名称")
    private String name;

    @NotEmpty(message = "至少需要一个档位")
    @Size(max = 10, message = "档位最多 10 个")
    @Valid
    @Schema(description = "分配档位列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SavingsBucketDTO> buckets;

}
