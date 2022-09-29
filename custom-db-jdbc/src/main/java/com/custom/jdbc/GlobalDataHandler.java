package com.custom.jdbc;

import com.custom.comm.Asserts;
import com.custom.comm.RexUtil;
import com.custom.comm.SymbolConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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
                SQL_KEYWORDS.addAll(Arrays.asList(str.split(SymbolConstant.SEPARATOR_COMMA_1)));
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * 该字段是否是sql关键字
     */
    public static boolean hasSqlKeyword(String column) {
        Asserts.notEmpty(column);
        if (RexUtil.hasRegex(RexUtil.back_quotes, column)) {
            return false;
        }
       return SQL_KEYWORDS.contains(column.toUpperCase(Locale.ROOT));
    }

    /**
     * 转换成包装的sql字段
     * <br/>  name -> `name`
     */
    public static String wrapperSqlKeyword(String column) {
        return "`" + column + "`";
    }

    /**
     * 去除sql字段上的包装
     * <br/>  `name` -> name
     */
    public static String removeSqlKeywordWrapper(String column) {
        return column.replace("`", "");
    }

    /**
     * 全局配置对象暂存
     */
   private static final ThreadLocal<CustomConfigHelper> CONFIG_HELPER = new ThreadLocal<>();

   protected static void setConfigHelper(CustomConfigHelper configHelper) {
       if (CONFIG_HELPER.get() == null) {
           CONFIG_HELPER.set(configHelper);
       }
   }

    public static CustomConfigHelper getConfigHelper() {
       if (CONFIG_HELPER.get() == null) {
           return null;
       }
        return CONFIG_HELPER.get();
    }




}
