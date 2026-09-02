package com.seiko.work.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.seiko.work.base.BaseEnum;
import lombok.Getter;

/**
 * 请假状态枚举
 */
@Getter
public enum LeaveStatusEnum implements BaseEnum<Integer> {

    /**
     * 待审批
     */
    PENDING(0, "待审批"),

    /**
     * 已通过
     */
    APPROVED(1, "已通过"),

    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝");

    @EnumValue
    @JsonValue
    private final Integer value;
    private final String description;

    LeaveStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}
