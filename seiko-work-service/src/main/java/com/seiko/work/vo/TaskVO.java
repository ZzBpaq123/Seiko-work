package com.seiko.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seiko.work.entity.Task;
import com.seiko.work.enums.TaskPriorityEnum;
import com.seiko.work.enums.TaskStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.time.LocalDate;

/**
 * 工作事项 VO
 */
@Data
@Schema(description = "工作事项响应")
public class TaskVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "计划日期")
    private LocalDate planDate;

    @Schema(description = "状态")
    private TaskStatusEnum status;

    @Schema(description = "优先级")
    private TaskPriorityEnum priority;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    public static TaskVO from(Task task) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }

}
