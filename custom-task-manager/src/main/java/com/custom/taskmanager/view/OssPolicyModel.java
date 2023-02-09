package com.custom.taskmanager.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author  Xiao-Bai
 * @since  2022/10/9 1:58
 * @desc
 */
@Getter
@Setter
public class OssPolicyModel {
    

    @ApiModelProperty("Oss访问ID")
    private String accessKeyId;

    @ApiModelProperty("Oss图片访问key")
    private String key;

    @ApiModelProperty("策略")
    private String policy;

    @ApiModelProperty("图片地址前缀")
    private String httpPrefix;

    @ApiModelProperty("签名")
    private String signature;

    @ApiModelProperty("过期时间")
    private Long expireTime;



}
