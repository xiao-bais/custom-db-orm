package com.custom.proxy;

import com.custom.comm.JudgeUtilsAx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Xiao-Bai
 * @date 2021/12/25 22:56
 * @desc:去除sql中没必要的注释内容
 */
@Slf4j
public class ClearNotesOnSqlHandler {


    private final static String Symbol_ONE = "--";


    /**
     * 加载指定路径中文件的内容
     */
    public static String loadSql(String filePath){
        String res = "";
        if(JudgeUtilsAx.isEmpty(filePath)){
            log.error("找不到文件或不存在该路径");
            return res;
        }
        try {
            Resource resource = new ClassPathResource(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String str;
            while((str=br.readLine())!=null) {

                sb.append(str);
            }
            res = sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return res;
    }

}
