package com.custom.comm.enums;

/**
 * @author Xiao-Bai
 * @date 2022/10/27 12:26
 * @desc
 */
public enum SqlExecTemplate {

    SELECT_LIST("查询多条记录", "SELECT %s\n FROM %s %s"),

    SELECT_COUNT("查询记录总数量", "SELECT COUNT(0) FROM (\n%s\n) xxx"),

    JOIN_TABLE("关联表的SQL", "\n%s %s %s ON %s"),

    INSERT_DATA("插入记录", "INSERT INTO %s(%s) VALUES"),

    DELETE_DATA("删除记录", "DELETE FROM %s %s"),

    UPDATE_DATA("修改记录", "UPDATE %s %s SET %s %s"),

    LOGIC_DELETE("逻辑删除(实则修改)", "UPDATE %s %s SET %s");



    private final String name;
    private final String template;

    SqlExecTemplate(String name, String template) {
        this.name = name;
        this.template = template;
    }

    public String getName() {
        return name;
    }

    public String getTemplate() {
        return template;
    }

    public static StringBuilder format(SqlExecTemplate template, Object... params) {
        return new StringBuilder().append(String.format(template.getTemplate(), params));
    }
}
