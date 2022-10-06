package com.custom.proxy;

import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.exceptions.ExThrowsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Xiao-Bai
 * @date 2021/12/25 22:56
 * @desc:去除sql中的注释内容
 */
@Slf4j
public class ClearNotesOnSqlHandler {

    private final String filePath;
    private static final String SYMBOL_LINE;
    private static final String SYMBOL_START1;
    private static final String SYMBOL_START2;
    private static final String SYMBOL_END;


    static {
        SYMBOL_LINE = "-- ";
        SYMBOL_START1 = "/**";
        SYMBOL_START2 = "/*";
        SYMBOL_END = "*/";
    }


    public ClearNotesOnSqlHandler(String filePath) {
        this.filePath = filePath;
    }


    /**
     * 加载指定路径中文件的内容
     */
    public String loadSql() {
        String res = Constants.EMPTY;
        if(JudgeUtil.isEmpty(filePath)){
            ExThrowsUtil.toNull("filepath is null");
        }
        try {
            Resource resource = new ClassPathResource(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String str;
            boolean isAppend = true;
            while((str=br.readLine())!=null) {
                if(str.startsWith(SYMBOL_LINE)) continue;
                if(str.startsWith(SYMBOL_START2) && str.endsWith(SYMBOL_END)) continue;
                if(str.startsWith(SYMBOL_START1) || str.startsWith(SYMBOL_START2)) {
                    isAppend = false;
                    continue;
                }
                if(str.endsWith(SYMBOL_END)) {
                    isAppend = true;
                    continue;
                }
                if(isAppend) {
                    if(JudgeUtil.isNotBlank(str)) {
                        sb.append(" ");
                    }
                    sb.append(str);
                }
            }
            res = sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

}
