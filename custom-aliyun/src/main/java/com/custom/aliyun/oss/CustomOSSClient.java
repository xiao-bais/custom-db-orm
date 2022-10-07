package com.custom.aliyun.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.custom.comm.utils.BackResult;

import java.io.File;

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
        BackResult<String> result = new BackResult<>();
        OSS ossClient = new OSSClientBuilder().build(this.ossKey.getEndpoint(), this.ossKey.getAccessKeyId(), this.ossKey.getAccessKeySecret());
        try {
            if (!existFile(filePath)) {
                result.error(String.format("filePath:[%s] is not exist", filePath));
                return result;
            }
            ossClient.putObject(this.ossKey.getEndpoint(), key, new File(filePath));
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
            ossClient.getObject(new GetObjectRequest(this.ossKey.getEndpoint(), key), new File(downloadPath));
        } catch (Exception e) {
            result.error("AliyunOSS Download Error ====> " + e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }


    public boolean existFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }



    private final CustomOSSKey ossKey;


    public CustomOSSClient(CustomOSSKey ossKey) {
        this.ossKey = ossKey;
    }




}
