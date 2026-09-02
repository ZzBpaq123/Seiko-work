package com.seiko.work.module.workstation.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.seiko.work.base.BaseEnum;
import lombok.Getter;

/**
 * 请假类型枚举
 */
@Getter
public enum LeaveTypeEnum implements BaseEnum<String> {

    /**
     * 年假
     */
    ANNUAL("annual", "年假"),

    /**
     * 病假
     */
    SICK("sick", "病假"),

    /**
     * 事假
     */
    PERSONAL("personal", "事假"),

    /**
     * 调休
     */
    COMPENSATORY("compensatory", "调休"),

    /**
     * 其他
     */
    OTHER("other", "其他");

    @EnumValue
    @JsonValue
    private final String value;
    private final String description;

    LeaveTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

}
