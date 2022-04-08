package com.custom.comm;

import com.custom.dbconfig.SymbolConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/8 13:40
 * @Desc：全局静态数据结构管理
 **/
public class GlobalDataHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalDataHandler.class);

    /**
     * 全局sql关键字暂存
     */
    private static final Set<String> SQL_KEYWORDS = new HashSet<>(225);


    static {
        Resource resource = new ClassPathResource("/keywords.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                SQL_KEYWORDS.addAll(Arrays.asList(str.split(SymbolConst.SEPARATOR_COMMA_1)));
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 该字段是否是sql关键字
     */
    public static boolean hasSqlKeyword(String column) {
       return SQL_KEYWORDS.contains(column.toUpperCase(Locale.ROOT));
    }




}
