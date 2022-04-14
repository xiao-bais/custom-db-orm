package com.custom.sqlparser;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.CustomApplicationUtils;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.FillStrategy;
import com.custom.exceptions.CustomCheckException;
import com.custom.fieldfill.AutoFillColumnHandler;
import com.custom.fieldfill.TableFillObject;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @author Xiao-Bai
 * @date 2022/4/10 15:07
 * @desc:构建删除sql
 */
public class HandleDeleteSqlBuilder<T> extends AbstractSqlBuilder<T> {

    @Override
    public String buildSql() {
        String deleteSql = String.format(" delete from %s %s where ", getTable(), getAlias());
        try {
            if ((JudgeUtilsAx.isNotEmpty(getLogicColumn()) && checkLogicFieldIsExist())) {
                return String.format(" update %s %s set %s where %s ", getTable(), getAlias(), getLogicDeleteUpdateSql(), getLogicDeleteQuerySql());
            }
        }catch (Exception e) {
            return null;
        }
        return deleteSql;
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
                throw new CustomCheckException("未找到可匹配的java属性字段");
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

    /**
     * 获取根据主键删除的sql
     */
//    public String getLogicDeleteKeySql(String key, String dbKey, String alias, boolean isMore) throws Exception {
//        String sql;
//        String keySql  = String.format("%s.%s%s%s", alias,
//                dbKey, isMore ? SymbolConst.IN : SymbolConst.EQUALS, key);
//
//        if (JudgeUtilsAx.isNotEmpty(getLogicDeleteUpdateSql())) {
//            String logicDeleteQuerySql = String.format("%s.%s", alias, getLogicDeleteQuerySql());
//            String logicDeleteUpdateSql = String.format("%s.%s", alias, getLogicDeleteUpdateSql());
//            if(checkLogicFieldIsExist()) {
//                sql = String.format("update %s %s set %s where %s and %s", table,
//                        alias, logicDeleteUpdateSql, logicDeleteQuerySql, keySql);
//            }else {
//                sql = String.format("delete from %s %s where %s", table, alias, keySql);
//            }
//        }else {
//            sql = String.format("delete from %s %s where %s", table, alias, keySql);
//        }
//        return sql;
//    }
}
