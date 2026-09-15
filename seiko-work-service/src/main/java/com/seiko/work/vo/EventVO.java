package com.seiko.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seiko.work.entity.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 日程事件 VO
 */
@Data
@Schema(description = "日程事件响应")
public class EventVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "是否全天：0-否，1-是")
    private Integer isAllDay;

    @Schema(description = "地点")
    private String location;

    @Schema(description = "内容")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    public static EventVO from(Event event) {
        EventVO vo = new EventVO();
        BeanUtils.copyProperties(event, vo);
        return vo;
    }

}
