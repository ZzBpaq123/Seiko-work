package com.seiko.work.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.seiko.work.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 攒钱目标配置实体
 * <p>
 * 一个用户一条记录，把每月收入按比例分配到若干档位。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_savings_goal")
@Schema(description = "攒钱目标配置实体")
public class SavingsGoal extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 目标名称
     */
    @TableField("name")
    @Schema(description = "目标名称")
    private String name;

    /**
     * 分配档位的 JSON 数组字符串，与档位列表的互转在 Service 层完成
     * （形如 [{"name":"储蓄","percent":70,"saving":true}]）
     */
    @TableField("buckets")
    @Schema(description = "分配档位 JSON 字符串")
    private String buckets;

}
