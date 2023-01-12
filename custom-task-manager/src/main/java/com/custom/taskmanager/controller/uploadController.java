package com.custom.taskmanager.controller;

import com.custom.aliyun.oss.AliyunOssConfig;
import com.custom.aliyun.oss.CustomOSSClient;
import com.custom.aliyun.oss.OssPolicyResult;
import com.custom.comm.date.DateTimeUtils;
import com.custom.comm.utils.back.BackResult;
import com.custom.taskmanager.config.AliyunOss;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Xiao-Bai
 * @date 2022/10/9 1:32
 * @desc
 */
@Api(tags = "图片上传管理")
@RestController
@RequestMapping("/upload_oss")
public class uploadController {

    @Resource
    private AliyunOss aliyunOss;


    @ApiOperation("获取oss上传地址")
    @GetMapping("/get_oss_policy")
    public BackResult<OssPolicyResult> getOssPolicy() {

        CustomOSSClient ossClient = this.initAliyunOssConfig();

        // 初始化文件名
        int thisTime = DateTimeUtils.getThisTime();
        OssPolicyResult result = ossClient.getPolicySign("cmTask-" + thisTime + ".jpg");

        return BackResult.bySuccess(result);
    }

    @ApiOperation("图片上传")
    @GetMapping("/upload")
    public BackResult<String> uploadPicture() {
        CustomOSSClient ossClient = this.initAliyunOssConfig();
        BackResult<String> backResult = ossClient.upload(aliyunOss.getDirPrefix() + System.currentTimeMillis() + ".jpg", "C:\\Users\\Administrator\\Desktop\\152.jpg");
        return backResult;
    }


    /**
     * 初始化阿里云配置
     */
    private CustomOSSClient initAliyunOssConfig() {
        AliyunOssConfig config = new AliyunOssConfig();
        config.setAccessKeyId(aliyunOss.getAccessKeyId());
        config.setAccessKeySecret(aliyunOss.getAccessKeySecret());
        config.setEndpoint(aliyunOss.getEndpoint());
        config.setBucketName(aliyunOss.getBucket());

        CustomOSSClient ossClient = new CustomOSSClient(config);
        return ossClient;
    }

}
