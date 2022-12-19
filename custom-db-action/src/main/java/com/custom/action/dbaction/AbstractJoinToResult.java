package com.custom.action.dbaction;

import com.custom.action.core.DbKeyParserModel;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.exceptions.QueryMultiException;
import com.custom.comm.enums.MultiStrategy;
import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.exceptions.CustomCheckException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Xiao-Bai
 * @date 2022/8/21 2:00
 * @desc 一对一，一对多注解的解析对象公共抽象父类
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
     * 查询策略
     */
    private MultiStrategy strategy;

    /**
     * 初始化剩余字段
     * @param setFieldName 若出现循环引用，目标循环引用的字段
     * @param strategy 策略
     * @param topNode 最顶级的父节点
     */
    protected void initJoinProperty(String setFieldName, MultiStrategy strategy, Class<?> topNode) {
        System.out.println("setFieldName = " + setFieldName);
        this.strategy = strategy;

        // 初始化主表的字段
        TableParseModel<?> thisTableModel = TableInfoCache.getTableModel(thisClass);
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
        TableParseModel<?> targetTableModel = TableInfoCache.getTableModel(this.joinTarget);
        this.joinAlias = targetTableModel.getAlias();
        if (JudgeUtil.isBlank(this.joinField)) {
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

        // 若多个对象之间存在循环引用一对一(多)注解的关系，则抛出异常
        if(this.existCrossReference(topNode, setFieldName) && this.strategy != MultiStrategy.NONE) {
            String exInfo = String.format("Wrong reference. One to one annotation is not allowed to act on the mutual reference relationship between two objects in [%s] and [%s.%s] ",
                    this.joinTarget, this.thisClass, setFieldName);
            throw new QueryMultiException(this.strategy, exInfo);
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

    public MultiStrategy getStrategy() {
        return strategy;
    }

    /**
     * 查询条件实现
     */
    public abstract String queryCondPrefix();
    public abstract String queryCondSuffix();


    /**
     * 存储实体一对一，一对多的引用情况
     */
    private final static Map<String, Set<String>> CROSS_REFERENCE = new ConcurrentHashMap<>();


    /**
     * 实体查询时，是否存在相互引用的情况
     */
    protected boolean existCrossReference(Class<?> topNode, String setFieldName) {
        String crossKey = topNode.getName();
        String refValue = this.joinTarget.getName() + "." + setFieldName;
        Set<String> crossReferenceSet = CROSS_REFERENCE.get(crossKey);
        if (JudgeUtil.isEmpty(crossReferenceSet)) {
            crossReferenceSet = new CopyOnWriteArraySet<>();
            crossReferenceSet.add(refValue);
            CROSS_REFERENCE.put(crossKey, crossReferenceSet);
            return false;
        }
        boolean exists = crossReferenceSet.contains(refValue);
        if (exists == false) {
            crossReferenceSet.add(refValue);
            CROSS_REFERENCE.put(crossKey, crossReferenceSet);
        }
        return exists;
    }


}
