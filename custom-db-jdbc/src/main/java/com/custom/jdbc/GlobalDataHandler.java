package com.custom.jdbc;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.RexUtil;
import com.custom.comm.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
                SQL_KEYWORDS.addAll(Arrays.asList(str.split(Constants.SEPARATOR_COMMA_1)));
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
     * 是否是sql关键字
     * <li> 该方法与{@link #hasSqlKeyword(String)}相比，略有不同 </li>
     * <li> 前者是判断是否已经是sql关键字，也就是说该字段是否已被包装成[`name`]这样的格式 </li>
     * <li> 后者(本方法)是判断是否可认定为sql关键字，此时该字段还未被包装</li>
     */
    public static boolean isSqlKeywordWrapping(String column) {
        char[] chars = column.toCharArray();
        return chars[0] == 96 && chars[chars.length - 1] == 96;
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
     * 全局对象暂存
     */
   private static final Map<String, Object> GLOBAL_CACHE = new ConcurrentHashMap<>();

   public static void addGlobalHelper(String key, Object value) {
       GLOBAL_CACHE.putIfAbsent(key, value);
   }

    public static Object readGlobalObject(String key) {
        return GLOBAL_CACHE.get(key);
    }




}
