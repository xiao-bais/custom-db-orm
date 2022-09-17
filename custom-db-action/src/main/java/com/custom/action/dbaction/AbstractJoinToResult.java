package com.custom.action.dbaction;

import com.custom.action.sqlparser.DbKeyParserModel;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.Asserts;
import com.custom.comm.JudgeUtil;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbOneToOne;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author Xiao-Bai
 * @date 2022/8/21 2:00
 * @desc 用于DbOneToOne注解的解析对象
 */
public abstract class AbstractJoinToResult {

    /**
     * 一对一关联的实体对象
     * <br/> 若不填，则默认取被该注解标注的类型对象
     * <br/> 注意: 该注解不可作用在java自带的类型下({@link Object}, {@link java.util.Map} 类除外)，否则在查询时会抛错
     */
    private Class<?> joinTarget;

    /**
     * 主表的类型
     */
    private Class<?> thisClass;

    /**
     * 当前类的关联字段(java属性即可)
     * <br/> 若不填，则默认取当前对象的主键 {@link DbKey}
     */
    private String thisField;

    /**
     * 对应当前类的关联sql字段
     */
    private String thisColumn;

    /**
     * 与当前对象关联的字段(java属性即可)
     * <br/> 若不填，则默认取注解作用在该属性对象上的主键 {@link DbKey}
     */
    private String joinField;

    /**
     * 对应被关联对象的sql字段
     */
    private String joinColumn;

    /**
     * 被关联对象的表别名
     */
    private String joinAlias;

    /**
     * 初始化剩余字段
     */
    protected void initJoinProperty() {

        // 初始化主表的字段
        TableSqlBuilder<?> thisTableModel = TableInfoCache.getTableModel(thisClass);
        // this ....
        if (JudgeUtil.isBlank(thisField)) {
            DbKeyParserModel<?> keyParserModel = thisTableModel.getKeyParserModel();
            Asserts.notNull(keyParserModel, "The defined primary key was not found on " + thisClass);
            this.thisField = keyParserModel.getKey();
            this.thisColumn = keyParserModel.getDbKey();

        } else {
            this.thisColumn = thisTableModel.getFieldParserModels().stream()
                    .filter(x -> x.getFieldName().equals(this.thisField)).findFirst()
                    .orElseThrow(this::throwNotFoundFieldExp)
                    .getColumn();

        }

        // 初始化关联表的字段 join ....
        TableSqlBuilder<?> targetTableModel = TableInfoCache.getTableModel(this.joinTarget);
        this.joinAlias = targetTableModel.getAlias();
        if (JudgeUtil.isBlank(joinField)) {
            DbKeyParserModel<?> keyParserModel = targetTableModel.getKeyParserModel();
            Asserts.notNull(keyParserModel, "The defined primary key was not found on " + this.joinTarget);
            this.joinField = keyParserModel.getKey();
            this.joinColumn = keyParserModel.getDbKey();
        }else {
            this.joinColumn = targetTableModel.getFieldParserModels().stream()
                    .filter(x -> x.getFieldName().equals(this.joinField)).findFirst()
                    .orElseThrow(this::throwNotFoundFieldExp)
                    .getColumn();
        }


    }

    private CustomCheckException throwNotFoundFieldExp() {
        return new CustomCheckException("%s DbOneToOne.thisField() could not find the corresponding Java property: '%s' in %s",
                this.thisClass.getName(), this.joinField, this.joinTarget
        );
    }

    public String getThisField() {
        return thisField;
    }

    public String getThisColumn() {
        return thisColumn;
    }

    public String getJoinField() {
        return joinField;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public void setThisField(String thisField) {
        this.thisField = thisField;
    }

    public void setThisColumn(String thisColumn) {
        this.thisColumn = thisColumn;
    }

    public void setJoinField(String joinField) {
        this.joinField = joinField;
    }

    public void setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
    }

    public Class<?> getJoinTarget() {
        return joinTarget;
    }

    public Class<?> getThisClass() {
        return thisClass;
    }

    public String getJoinAlias() {
        return joinAlias;
    }

    public void setJoinAlias(String joinAlias) {
        this.joinAlias = joinAlias;
    }

    public void setJoinTarget(Class<?> joinTarget) {
        this.joinTarget = joinTarget;
    }

    public void setThisClass(Class<?> thisClass) {
        this.thisClass = thisClass;
    }

    /**
     * 查询条件实现
     */
    public abstract String queryCondition();

}
