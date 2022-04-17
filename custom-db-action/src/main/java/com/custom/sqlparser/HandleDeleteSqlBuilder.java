package com.custom.sqlparser;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.CustomApplicationUtils;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.FillStrategy;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExThrowsUtil;
import com.custom.fieldfill.AutoFillColumnHandler;
import com.custom.fieldfill.TableFillObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Xiao-Bai
 * @date 2022/4/10 15:07
 * @desc:构建删除sql
 */
public class HandleDeleteSqlBuilder<T> extends AbstractSqlBuilder<T> {

    private static final Logger logger = LoggerFactory.getLogger(HandleDeleteSqlBuilder.class);

    private Object key;
    private Collection<? extends Serializable> keys;
    private String deleteCondition;

    @Override
    public String buildSql() {
        String deleteSql = String.format(" delete from %s %s where %s", getTable(), getAlias(), deleteCondition);
        try {
            if (checkLogicFieldIsExist()) {
                return String.format(" update %s %s set %s where %s %s", getTable(), getAlias(), getLogicDeleteUpdateSql(), getLogicDeleteQuerySql(), deleteCondition);
            }
        }catch (Exception e) {
            logger.error(e.toString(), e);
            return null;
        }
        return deleteSql;
    }

    /**
     * 处理单个主键删除
     */
    private void handleByKey() {
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();
        try {
            this.deleteCondition = checkLogicFieldIsExist() ? String.format("and %s = ?", keyParserModel.getFieldSql())
                    : String.format("%s = ?", keyParserModel.getFieldSql());
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return;
        }
        if(!CustomUtil.isKeyAllowType(keyParserModel.getType(), key)) {
            ExThrowsUtil.toCustom("不允许的主键参数: " + key);
        }
        getSqlParams().add(key);
    }

    /**
     * 处理多个主键删除
     */
    private void handleByKeys() {
        DbKeyParserModel<T> keyParserModel = getKeyParserModel();
        try {
            StringJoiner delSymbols = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);
            IntStream.range(0, keys.size()).mapToObj(i -> SymbolConst.QUEST).forEach(delSymbols::add);
            this.deleteCondition = checkLogicFieldIsExist() ? String.format("and %s in %s", keyParserModel.getFieldSql(), delSymbols)
                    : String.format("%s in %s", keyParserModel.getFieldSql(), delSymbols);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return;
        }
        if (Objects.nonNull(keys) && keys.stream().noneMatch(x -> CustomUtil.isKeyAllowType(keyParserModel.getType(), x))) {
            ExThrowsUtil.toCustom("不允许的主键参数: " + keys);
        }
        getSqlParams().addAll(keys);
    }

    private void handleByCondition() {
        try {
            deleteCondition = checkLogicFieldIsExist() ? CustomUtil.replaceOrWithAndOnSqlCondition(deleteCondition) : CustomUtil.trimSqlCondition(deleteCondition);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }


    /**
     * 在删除数据时，若是有逻辑删除，则在逻辑删除后，进行固定字段的自动填充
     */
    protected void handleLogicDelAfter(Class<?> t, String deleteSql, Object... params) throws Exception {
        AutoFillColumnHandler fillColumnHandler = CustomApplicationUtils.getBean(AutoFillColumnHandler.class);
        if(Objects.isNull(fillColumnHandler)) {
            return;
        }
        Optional<TableFillObject> first = fillColumnHandler.fillStrategy().stream().filter(x -> x.getEntityClass().equals(t)).findFirst();
        first.ifPresent(op -> {
            String autoUpdateWhereSqlCondition = deleteSql.substring(deleteSql.indexOf(SymbolConst.WHERE)).replace(getLogicDeleteQuerySql(), getLogicDeleteUpdateSql());
            FillStrategy strategy = op.getStrategy();
            if(strategy.equals(FillStrategy.DEFAULT)) {
                return;
            }
            String autoUpdateSql = buildLogicDelAfterAutoUpdateSql(strategy, autoUpdateWhereSqlCondition, params);
            if(!ObjectUtils.isEmpty(autoUpdateSql)) {
                try {
                    executeUpdateNotPrintSql(autoUpdateSql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 自动填充的sql构造（采用逻辑后进行Update操作的方式进行自动填充）
     */
    private String buildLogicDelAfterAutoUpdateSql(FillStrategy strategy, String whereKeySql, Object... params) {
        StringBuilder autoUpdateSql = new StringBuilder();
        Optional<TableFillObject> first = Objects.requireNonNull(CustomApplicationUtils.getBean(AutoFillColumnHandler.class))
                .fillStrategy().stream().filter(x -> x.getEntityClass().equals(getEntityClass())).findFirst();
        first.ifPresent(op -> {
            autoUpdateSql.append(SymbolConst.UPDATE)
                    .append(getTable())
                    .append(" ")
                    .append(getAlias())
                    .append(SymbolConst.SET);

            if (strategy.toString().contains(op.getStrategy().toString())) {
                String sqlFragment = buildAssignAutoUpdateSqlFragment(op.getTableFillMapper());
                if (Objects.nonNull(sqlFragment)) {
                    autoUpdateSql.append(sqlFragment);
                }
                autoUpdateSql.append(CustomUtil.handleExecuteSql(whereKeySql, params));
            }
        });
        return autoUpdateSql.toString();
    }

    /**
     * 构建指定逻辑删除时自动填充的sql片段
     */
    private String buildAssignAutoUpdateSqlFragment(Map<String, Object> tableFillObjects) {
        StringJoiner autoUpdateFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        StringBuilder updateField;
        if (ObjectUtils.isEmpty(tableFillObjects)) {
            return autoUpdateFieldSql.toString();
        }
        for (String fieldName : tableFillObjects.keySet()) {
            if (ObjectUtils.isEmpty(getFieldMapper().get(fieldName))) {
                ExThrowsUtil.toCustom("未找到可匹配的java属性字段");
            }
            updateField = new StringBuilder();
            Object fieldVal = tableFillObjects.get(fieldName);
            if (ObjectUtils.isEmpty(fieldVal)) continue;
            updateField.append(getFieldMapper().get(fieldName)).append(SymbolConst.EQUALS).append(fieldVal);
            autoUpdateFieldSql.add(updateField);
            getFieldParserModels().stream().filter(x -> x.getFieldName().equals(fieldName)).findFirst().ifPresent(op -> {
                op.setValue(fieldVal);
            });
        }
        return autoUpdateFieldSql.toString();
    }

    protected void setKey(Object key) {
        this.key = key;
        handleByKey();
    }

    public void setKeys(Collection<? extends Serializable> keys) {
        this.keys = keys;
        handleByKeys();
    }

    public void setDeleteCondition(String deleteCondition) {
        this.deleteCondition = deleteCondition;
        handleByCondition();
    }
}
