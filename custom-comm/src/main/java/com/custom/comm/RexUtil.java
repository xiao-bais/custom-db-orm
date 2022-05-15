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
    public final static String sql_param = "\\#\\{(.+?)\\}";

    /**
     * 匹配sql-ifnull函数
     */
    public final static String sql_if_null = "ifnull(.+?)$";

    /**
     * 匹配自定义格式化设定
     * this is {name} -> this is zhangsan
     */
    public final static String custom_format = "\\{(.+?)\\}";


    /**
     * 返回第一个满足正则条件的字符串
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
     * 替换匹配正则的字符串(只替换首次出现的)
     * 例: this is {name} -> this is {zhangsan}
     */
    public static String replaceStr(String str, String regex, String replaceStr) {
        Pattern pattern= Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        if (matcher.find()) {
            return matcher.replaceFirst(replaceStr);
        }
        return str;
    }

    /**
     * 替换匹配正则的字符串(全部替换)
     * 例: this is {name} -> this is zhangsan
     */
    public static String replaceStr(String str, String regex, String oldStr, String replaceStr) {
        Pattern pattern= Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            if (oldStr.equals(matcher.group(1))) {
                matcher.appendReplacement(sb, JudgeUtilsAx.isEmpty(replaceStr) ? SymbolConstant.EMPTY : replaceStr);
                 return matcher.appendTail(sb).toString();
            }
        }
        return str;
    }

    /**
     * 替换匹配正则的字符串(替换首次出现的字符)
     * 例: this is {name} and {name} years -> this is {zahngsan} and {zahngsan}
     */
    public static String replaceAllRex(String str, String regex, String replaceStr) {
        Pattern pattern= Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        while (matcher.find()) {
           str = matcher.replaceFirst(replaceStr);
        }
        return str;
    }

    /**
     * 替换匹配正则的字符串(替换全部符合条件的)
     * this is {name} and {age} years -> this is zahngsan and 18
     */
    public static String replaceAllRex(String str, String regex, String oldStr, String replaceStr) {
        Pattern pattern= Pattern.compile(regex);
        Matcher matcher=pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            if (key.equals(oldStr)) {
                matcher.appendReplacement(sb, JudgeUtilsAx.isEmpty(replaceStr) ? SymbolConstant.EMPTY : replaceStr);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 是否匹配
     */
    public static boolean hasRegex(String str, String regex) {
        Pattern pattern= Pattern.compile(regex);
        return pattern.matcher(str).find();
    }
}
