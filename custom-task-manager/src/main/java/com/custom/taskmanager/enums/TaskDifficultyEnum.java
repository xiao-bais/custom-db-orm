package com.custom.taskmanager.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 18:38
 * @Desc
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskDifficultyEnum {

    UN_HARD(1, "不难"),
    LITTLE_HARD(2, "有点难"),
    VERY_HARD(3, "非常难");

    TaskDifficultyEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private final Integer code;

    private final String name;

    public static String getName(Integer code) {
        for (TaskDifficultyEnum value : values()) {
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
