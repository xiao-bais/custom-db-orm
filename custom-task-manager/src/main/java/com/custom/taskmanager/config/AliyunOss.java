package com.custom.taskmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Xiao-Bai
 * @date 2022/10/9 1:55
 * @desc
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOss {

    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private String bucket;

    private String dirPrefix;

}
