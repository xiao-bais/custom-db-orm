package com.custom.action.condition;

import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.SqlAggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/19 17:28
 * @Desc：sql函数方法
 **/
@SuppressWarnings("all")
public abstract class AbstractSqlFunc<T, Children> {

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge)
     * @param column 需要求和的属性 Student::getAge
     * @return SqlFunc
     */
    public abstract Children sum(SFunction<T, ?> column);
    public abstract Children sum(boolean isNullToZero, SFunction<T, ?> column);

    /**
     * sql sum函数
     * 例：x -> x.avg(Student::getAge)
     * @param column 需要求平均的属性 Student::getAge
     * @return SqlFunc
     */



    public abstract Children avg(SFunction<T, ?> column);
    public abstract Children avg(boolean isNullToZero, SFunction<T, ?> column);

    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true, Student::getCountAge)
     * @param column 需要求和的字段属性 Student::getAge
     * @param distinct 是否去重？
     * @return SqlFunc
     */
    public Children count(SFunction<T, ?> column) {
        return count(column, false);
    }
    public abstract Children count(SFunction<T, ?> column, boolean distinct);

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0)
     * @param column 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值
     * @return SqlFunc
     */
    public abstract Children ifNull(SFunction<T, ?> column, Object elseVal);

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge)
     * @param column 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    public abstract Children max(SFunction<T, ?> column);
    public abstract Children max(boolean isNullToZero, SFunction<T, ?> column);

    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge)
     * @param column 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    public abstract Children min(SFunction<T, ?> column);
    public abstract Children min(boolean isNullToZero, SFunction<T, ?> column);


    /**
     * SFunction接口实体字段解析对象
     */
    private ColumnParseHandler<T> columnParseHandler;
    /**
     * 实体字段到表字段的映射缓存
     */
    private Map<String, String> fieldMapper;
    /**
     * 表字段到实体字段的映射缓存
     */
    private Map<String, String> columnMapper;
    /**
     * sql片段
     */
    private List<String> sqlFragments;

    /**
     * 主表的别名
     */
    private String alias;


    // 初始化
    protected void init(Class<T> cls) {
        columnParseHandler = new DefaultColumnParseHandler<>(cls);
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(cls);
        fieldMapper = tableModel.getFieldMapper();
        columnMapper = tableModel.getColumnMapper();
        alias = tableModel.getAlias();
        sqlFragments = new ArrayList<>();
    }


    /**
     * 获取格式化的sql函数模板
     */
    protected String formatRex(SqlAggregate aggregate, Boolean distinct) {
        return formatRex(aggregate, false, distinct);
    }

    protected String formatRex(SqlAggregate aggregate) {
        return formatRex(aggregate, false, false);
    }

    protected String formatRex(SqlAggregate aggregate, boolean isNullToZero,  Boolean distinct) {
        String template = SymbolConstant.EMPTY;
        switch (aggregate) {
            case SUM:
            case MAX:
            case MIN:
            case AVG:
                template = isNullToZero ? "ifnull(%s(%s), 0) %s" : "%s(%s) %s";
                break;
            case COUNT:
                template = distinct ? "%s(distinct %s) %s" : "%s(%s) %s";
                break;
            case IFNULL:
                template = "%s(%s, %s) %s";
                break;
        }
        return template;
    }


    /**
     * 适配函数的拼接
     * @param format 格式化的函数
     * @param params 参数
     * @return SqlFunc
     */
    protected Children doFunc(String format, Object... params) {
        sqlFragments.add(String.format(format, params));
        return childrenClass;
    }

    public List<String> getSqlFragments() {
        return sqlFragments;
    }

    protected String getColumns() {
        return sqlFragments.stream().collect(Collectors.joining(SymbolConstant.SEPARATOR_COMMA_2));
    }

    protected ColumnParseHandler<T> getColumnParseHandler() {
        return columnParseHandler;
    }

    protected Map<String, String> getFieldMapper() {
        return fieldMapper;
    }

    public String getAlias() {
        return alias;
    }

    public Map<String, String> getColumnMapper() {
        return columnMapper;
    }

    private final Children childrenClass = (Children) this;
}
