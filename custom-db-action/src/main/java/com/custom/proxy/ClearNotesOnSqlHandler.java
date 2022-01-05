package com.custom.proxy;

import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.SymbolConst;
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

    private String filePath;
    private static String SYMBOL_LINE;
    private static String SYMBOL_START1;
    private static String SYMBOL_START2;
    private static String SYMBOL_END;


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
    public String loadSql(){
        String res = SymbolConst.EMPTY;
        if(JudgeUtilsAx.isEmpty(filePath)){
            log.error("找不到文件或不存在该路径");
            return res;
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
                    if(JudgeUtilsAx.isNotBlank(str)) {
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
