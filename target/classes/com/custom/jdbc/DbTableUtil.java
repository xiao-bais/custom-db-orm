package com.custom.jdbc;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/12 0012 20:03
 * @Version 1.0
 * @Description DbTableApiUtils
 */

import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.GlobalConst;
import com.custom.exceptions.CustomCheckException;
import com.custom.utils.JudgeUtilsAx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

/**
 * 该类只为对数据库表结构进行操作,不操作数据,属于(DDL)
 */
public class DbTableUtil {

    private Logger logger = LoggerFactory.getLogger(DbTableUtil.class);

    private JdbcUtils jdbcUtils;
    private TableSpliceSql tableSpliceSql;
    private DbAnnotationsParser annotationsParser;
    private static String dataBase = "";
    private DbParserFieldHandler dbParserFieldHandler;

    DbTableUtil(DbDataSource dbDataSource){
        dbParserFieldHandler = new DbParserFieldHandler();
        jdbcUtils = new JdbcUtils(dbDataSource, dbParserFieldHandler);
        annotationsParser = new DbAnnotationsParser();
        tableSpliceSql = new TableSpliceSql(new DbParserFieldHandler(), annotationsParser);
        dataBase = String.valueOf(GlobalConst.currMap.get("database"));
    }

    /**
     * 查看该表是否存在
     */
    private <T> boolean existTable(Class<T> t) throws Exception{
        JudgeUtilsAx.isTableTag(t);
        long count = 0;
        try{
            Map<String, Object> tableMap = annotationsParser.getParserByDbTable(t);
            String tableName = tableMap.get("tableName").toString();
            String isTable = String.format("SELECT COUNT(1) COUNT FROM " +
                    "`information_schema`.`TABLES` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s';", tableName, dataBase);
            count = jdbcUtils.executeSql(isTable);
        }catch (SQLException e){
            logger.error(e.toString(), e);
        }
        return  count > 0;//表示该表存在
    }


    /**
     * 创建多个表
     */
    @SafeVarargs
    final <T> void createTables(Class<T>... arr) throws Exception{
        for (int i = arr.length - 1; i >= 0; i--) {
            if(existTable(arr[i])) continue;
            String createTableSql = tableSpliceSql.getCreateTableSql(arr[i]);
            logger.info("createTableSql -> " + createTableSql);
            jdbcUtils.executeUpdate(createTableSql);
        }
    }


    /**
     * 删除多个表
     */
    @SafeVarargs
    final <T> void dropTables(Class<T>... arr) throws Exception{
        for (int i = arr.length - 1; i >= 0; i--) {
            if(!existTable(arr[i])) {
                throw new CustomCheckException(GlobalConst.EX_DROP_TABLE_NOT_FOUND + arr[i].getName());
            }
            dropTable(arr[i]);
        }
    }


    /**
     * 删除一个表
     */
    private  <T> void dropTable(Class<T> t) throws Exception{
        String dropTableSql = String.format("DROP TABLE IF EXISTS %s ",
                annotationsParser.getFieldKey(t).get("tableName").toString());
        jdbcUtils.executeUpdate(dropTableSql);
    }





}
