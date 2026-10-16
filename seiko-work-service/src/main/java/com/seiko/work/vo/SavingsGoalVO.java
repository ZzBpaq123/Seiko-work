package com.seiko.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 攒钱目标 VO
 */
@Data
@Schema(description = "攒钱目标响应")
public class SavingsGoalVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "目标名称")
    private String name;

    @Schema(description = "分配档位列表")
    private List<SavingsBucketVO> buckets;

    @Schema(description = "每月发薪日期(1-31)，必填，默认 1 号")
    private Integer salaryDay;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

}
