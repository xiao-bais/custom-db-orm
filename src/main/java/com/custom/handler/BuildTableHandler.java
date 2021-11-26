package com.custom.handler;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/12 0012 20:03
 * @Version 1.0
 * @Description DbTableApiUtils
 */

import com.custom.comm.CustomUtil;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.DbFieldsConst;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.ExceptionConst;
import com.custom.exceptions.CustomCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

/**
 * 该类只为对数据库表结构进行操作,不操作数据,属于(DDL)
 */
public class BuildTableHandler {

    private Logger logger = LoggerFactory.getLogger(BuildTableHandler.class);

    private SqlExecuteHandler sqlExecuteHandler;
    private TableSpliceSql tableSpliceSql;
    private DbAnnotationsParserHandler annotationsParser;
    private static String dataBase = SymbolConst.EMPTY;

    BuildTableHandler(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy){
        sqlExecuteHandler = new SqlExecuteHandler(dbDataSource, dbCustomStrategy, new DbParserFieldHandler());
        annotationsParser = new DbAnnotationsParserHandler();
        tableSpliceSql = new TableSpliceSql(annotationsParser);
        dataBase = String.valueOf(ExceptionConst.currMap.get(DbFieldsConst.DATA_BASE));
    }

    /**
     * 查看该表是否存在
     */
    private <T> boolean existTable(Class<T> t) {
        CustomUtil.isTableTag(t);
        long count = 0;
        try{
            Map<String, Object> tableMap = annotationsParser.getParserByDbTable(t);
            String tableName = tableMap.get(DbFieldsConst.TABLE_NAME).toString();
            String isTable = String.format("SELECT COUNT(1) COUNT FROM " +
                    "`information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s';", tableName, dataBase);
            count = sqlExecuteHandler.executeTableExist(isTable);
        }catch (SQLException e){
            logger.error(e.toString(), e);
        }
        return  count > 0;//表示该表存在
    }


    /**
     * 创建多个表
     */
    final void createTables(Class<?>... arr) throws Exception{
        for (int i = arr.length - 1; i >= 0; i--) {
            if(existTable(arr[i])) continue;
            String createTableSql = tableSpliceSql.getCreateTableSql(arr[i]);
            sqlExecuteHandler.executeTableSql(createTableSql);
            logger.info("createTableSql ->\n " + createTableSql);
        }
    }


    /**
     * 删除多个表
     */
    final void dropTables(Class<?>... arr) throws Exception{
        for (int i = arr.length - 1; i >= 0; i--) {
            if(!existTable(arr[i])) {
                throw new CustomCheckException(ExceptionConst.EX_DROP_TABLE_NOT_FOUND + arr[i].getName());
            }
            dropTable(arr[i]);
        }
    }


    /**
     * 删除一个表
     */
    private <T> void dropTable(Class<T> t) throws Exception{
        String table = String.valueOf(annotationsParser.getParserByDbTable(t).get(DbFieldsConst.TABLE_NAME));
        String dropTableSql = String.format("DROP TABLE IF EXISTS %s ", table);
        sqlExecuteHandler.executeTableSql(dropTableSql);
        logger.warn("drop table '{}' completed\n", table);
    }





}
