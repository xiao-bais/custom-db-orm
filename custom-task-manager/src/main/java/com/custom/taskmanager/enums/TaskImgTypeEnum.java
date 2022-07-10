package com.custom.taskmanager.enums;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/10 19:12
 * @Desc
 */
public enum TaskImgTypeEnum {

    TASK(1, "任务图片附件"),
    TEST_RESULT(2, "测验结果图片附件");

    TaskImgTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private final Integer code;

    private final String name;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
