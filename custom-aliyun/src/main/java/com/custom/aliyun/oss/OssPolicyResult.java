package com.custom.aliyun.oss;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Xiao-Bai
 * @date 2022/10/9 3:50
 * @desc
 */
@Getter
@Setter
public class OssPolicyResult {

    /**
     * Oss访问ID
     */
    private String accessKeyId;

    /**
     * Oss图片访问key
     */
    private String key;

    /**
     * 策略
     */
    private String policy;

    /**
     * 图片地址前缀
     */
    private String httpPrefix;

    /**
     * 签名
     */
    private String signature;

    /**
     * 截止过期时间戳
     */
    private Long expireTime;

}
