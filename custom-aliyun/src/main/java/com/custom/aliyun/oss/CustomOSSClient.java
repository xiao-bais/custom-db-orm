package com.custom.aliyun.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.custom.comm.utils.Asserts;
import com.custom.jdbc.back.BackResult;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Xiao-Bai
 * @date 2022/10/7 22:04
 * @desc
 */
public class CustomOSSClient {


    /**
     * 上传
     * @param key key
     * @param filePath 文件所在路径
     * @return
     */
    public BackResult<String> upload(String key, String filePath) {
        return upload(key, new File(filePath));
    }

    /**
     * 上传
     */
    public BackResult<String> upload(String key, File file) {
        Asserts.notNull(file);
        BackResult<String> result = new BackResult<>();
        OSS ossClient = new OSSClientBuilder().build(this.ossKey.getEndpoint(), this.ossKey.getAccessKeyId(), this.ossKey.getAccessKeySecret());
        try {
            if (!file.exists()) {
                result.error(String.format("filePath:[%s] is not exist", file.getPath()));
                return result;
            }
            ossClient.putObject(this.ossKey.getBucketName(), key, file);
        } catch (Exception e) {
            result.error("AliyunOSS Upload Error ====> " + e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }


    /**
     * 下载
     * @param key key
     * @param downloadPath 下载到指定的路径
     */
    public BackResult<String> download(String key, String downloadPath) {
        BackResult<String> result = new BackResult<>();
        OSS ossClient = new OSSClientBuilder().build(this.ossKey.getEndpoint(), this.ossKey.getAccessKeyId(), this.ossKey.getAccessKeySecret());
        try {
            ossClient.getObject(new GetObjectRequest(this.ossKey.getBucketName(), key), new File(downloadPath));
        } catch (Exception e) {
            result.error("AliyunOSS Download Error ====> " + e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }


    private boolean existFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 获取上传策略
     * @param fileName 自定义上传后的文件名称
     * @return {@link OssPolicyResult}
     */
    public OssPolicyResult getPolicySign(String fileName) {

        Asserts.notEmpty(fileName);
        OssPolicyResult result = new OssPolicyResult();

        // https 前缀
        String httpPrefix = "https://" + this.ossKey.getBucketName() + "." + this.ossKey.getEndpoint() + "/";
        // 存储文件前缀
        String dirPrefix = this.ossKey.getDirPrefix();

        // 实例化
        OSS client = new OSSClientBuilder().build(this.ossKey.getEndpoint(), this.ossKey.getAccessKeyId(), this.ossKey.getAccessKeySecret());

        // 设置5分钟的过期时间
        long expireTime = 60 * 5;
        long nowTime = System.currentTimeMillis();
        long expireEndTime = nowTime + expireTime * 1000;
        // 到期时间
        Date expiration = new Date(expireEndTime);

        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        //前缀必须是XX开头
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dirPrefix);
        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);

        result.setAccessKeyId(this.ossKey.getAccessKeyId());
        result.setExpireTime(expireEndTime / 1000);
        result.setSignature(postSignature);
        result.setPolicy(encodedPolicy);
        result.setHttpPrefix(httpPrefix);
        result.setKey(dirPrefix + fileName);

        return result;
    }




    private final AliyunOssConfig ossKey;


    public CustomOSSClient(AliyunOssConfig ossKey) {
        this.ossKey = ossKey;
    }




}
