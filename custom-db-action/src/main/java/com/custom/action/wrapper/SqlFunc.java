package com.custom.action.wrapper;

import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.SqlAggregate;

import java.util.Map;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/19 17:28
 * @Desc：sql函数方法
 **/
@SuppressWarnings("all")
public abstract class SqlFunc<T, Child> {

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge)
     * @param func 需要求和的属性 Student::getAge
     * @return SqlFunc
     */
    public abstract Child sum(SFunction<T, ?> func);

    /**
     * sql sum函数
     * 例：x -> x.avg(Student::getAge)
     * @param func 需要求平均的属性 Student::getAge
     * @return SqlFunc
     */
    public abstract Child avg(SFunction<T, ?> func);

    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true, Student::getCountAge)
     * @param func 需要求和的字段属性 Student::getAge
     * @param distinct 是否去重？
     * @return SqlFunc
     */
    public Child count(SFunction<T, ?> func) {
        return count(func, false);
    }
    public abstract Child count(SFunction<T, ?> func, boolean distinct);

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0)
     * @param func 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值
     * @return SqlFunc
     */
    public abstract Child ifNull(SFunction<T, ?> func, Object elseVal);

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    public abstract Child max(SFunction<T, ?> func);

    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    public abstract Child min(SFunction<T, ?> func);

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
    private StringJoiner sqlFragment;

    /**
     * 主表的别名
     */
    private String alias;


    // 初始化
    protected void init(Class<T> cls) {
        columnParseHandler = new ColumnParseHandler<>(cls);
        fieldMapper = TableInfoCache.getFieldMap(cls);
        columnMapper = TableInfoCache.getColumnMap(cls);
        alias = TableInfoCache.getTableModel(cls).getAlias();
        sqlFragment = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
    }


    /**
     * 获取格式化的sql函数模板
     */
    protected String formatRex(SqlAggregate aggregate, Boolean distinct) {
        String template = SymbolConstant.EMPTY;
        switch (aggregate) {
            case SUM:
            case MAX:
            case MIN:
            case AVG:
                template = "%s(%s) %s";
                break;
            case COUNT:
                template = distinct ? "%s(distinct %s) %s" : "%s(%s) %s";
                break;
            case IFNULL:
                template = "%s(%s,%s) %s";
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
    protected Child doFunc(String format, Object... params) {
        sqlFragment.add(String.format(format, params));
        return childClass;
    }

    protected StringJoiner getSqlFragment() {
        return sqlFragment;
    }

    protected String getColumns() {
        return sqlFragment.toString();
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

    private final Child childClass = (Child) this;
}
