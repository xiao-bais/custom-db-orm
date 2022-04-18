package com.custom.comm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/8 15:02
 * @Desc：
 **/
public class RexUtil {

    /**
     * 匹配单引号
     */
    public final static String single_quotes = "(?<=').*?(?=')";
    
    /**
     * 匹配反单引号
     */
    public final static String back_quotes = "(?<=`).*?(?=`)";

    /**
     * 匹配双引号
     */
    public final static String double_quotes = "(?<=\").*?(?=\")";

    /**
     * 匹配mybatis参数格式
     */
    public final static String sql_param = "(?<=#\\{).*?(?=\\})";


    /**
     * 匹配正则
     */
    public static String regexStr(String str, String regex) {
        Pattern pattern= Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 是否匹配
     */
    public static boolean hasRegex(String str, String regex) {
        Pattern pattern= Pattern.compile(regex);
        return pattern.matcher(str).find();
    }
}
