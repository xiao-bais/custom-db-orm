package com.custom.taskmanager.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author  Xiao-Bai
 * @since  2022/7/10 14:57
 * @Desc
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskProgressEnum {

    UN_START(1, "未开始"),
    IN_PROGRESS(2, "进行中"),
    UN_FINISHED(3, "未完成"),
    FINISH_TO_CHECK(4, "完成待检验"),
    ENDED(5, "已结束");

    TaskProgressEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private final Integer code;

    private final String name;

    public static String getName(Integer code) {
        for (TaskProgressEnum value : values()) {
            if (value.code.equals(code)) {
                return value.name;
            }
        }
        return null;
    }

    public static TaskProgressEnum valueOf(Integer code) {
        for (TaskProgressEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }


    public static boolean isNotAllowEdit(TaskProgressEnum taskProgress) {
        return taskProgress == TaskProgressEnum.FINISH_TO_CHECK
                || taskProgress == TaskProgressEnum.ENDED
                || taskProgress == TaskProgressEnum.UN_FINISHED;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
