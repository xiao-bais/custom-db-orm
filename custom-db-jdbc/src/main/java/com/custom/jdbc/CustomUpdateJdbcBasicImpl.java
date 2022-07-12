package com.custom.jdbc;

import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.condition.SaveSqlParamInfo;
import com.custom.jdbc.update.CustomUpdateJdbcBasic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/18 21:40
 * @Desc 增、删、改 基础jdbc操作
 */
public class CustomUpdateJdbcBasicImpl extends CustomJdbcManagement implements CustomUpdateJdbcBasic {

    private static final Logger logger = LoggerFactory.getLogger(CustomUpdateJdbcBasicImpl.class);
    private final DbCustomStrategy dbCustomStrategy;

    public CustomUpdateJdbcBasicImpl(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        super(dbDataSource, dbCustomStrategy);
        this.dbCustomStrategy = getDbCustomStrategy();
    }

    /**
     * 通用添加、修改、删除
     */
    @Override
    public int executeUpdate(SaveSqlParamInfo<Object> params) throws Exception {
        int res;
        try {
            statementUpdate(false, params.getPrepareSql(), params.getSqlParams());
            res = getStatement().executeUpdate();
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }finally {
            closeAll();
        }
        return res;
    }

    /**
     * 插入（实体可生成主键值）
     */
    public <T> int executeSave(SaveSqlParamInfo<T> params) throws Exception {
        int res;
        try {
            statementUpdate(true, params.getPrepareSql(), params.getSqlParams());
            res = handleUpdateStatement();
            ResultSet resultSet = handleGenerateKeysStatement();
            int count = 0;
            Field keyField = params.getKeyField();
            while (resultSet.next()) {
                T t = params.getDataList().get(count);
                PropertyDescriptor pd = new PropertyDescriptor(keyField.getName(), t.getClass());
                Method writeMethod = pd.getWriteMethod();
                String val = String.valueOf(resultSet.getObject(1));
                if (Long.class.isAssignableFrom(keyField.getType()) || keyField.getType().equals(Long.TYPE)) {
                    writeMethod.invoke(t, Long.parseLong(val));
                } else if (Integer.class.isAssignableFrom(keyField.getType()) || keyField.getType().equals(Integer.TYPE)) {
                    writeMethod.invoke(t, Integer.parseInt(val));
                }
                // else ignore...
                count++;
            }
        } catch (SQLException e) {
            SqlOutPrintBuilder
                    .build(params.getPrepareSql(), params.getSqlParams(), dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            throw e;
        }finally {
            closeAll();
        }
        return res;
    }

    /**
     * 执行表(字段)结构创建或删除
     */
    public void execTableInfo(String sql) {
        try {
            handleExecTableInfo(sql);
        }catch (Exception e) {
            SqlOutPrintBuilder
                    .build(sql, new String[]{}, dbCustomStrategy.isSqlOutPrintExecute())
                    .sqlErrPrint();
            logger.error(e.toString(), e);
        }finally {
            closeAll();
        }
    }
}
