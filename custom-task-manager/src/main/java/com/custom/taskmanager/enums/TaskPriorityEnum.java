package com.custom.taskmanager.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author  Xiao-Bai
 * @since  2022/7/10 18:53
 * @Desc
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskPriorityEnum {

    LOW(1, "低等"),
    MIDDLE(2, "中等"),
    HIGH(3, "高等");

    TaskPriorityEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private final Integer code;

    private final String name;

    public static String getName(Integer code) {
        for (TaskPriorityEnum value : values()) {
            if (value.code.equals(code)) {
                return value.name;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
