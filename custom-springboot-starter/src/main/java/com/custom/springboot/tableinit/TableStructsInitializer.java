package com.custom.springboot.tableinit;

import com.custom.action.sqlparser.DbFieldParserModel;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.ConvertUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConstant;
import com.custom.jdbc.ExecuteSqlHandler;
import com.custom.springboot.scanner.CustomBeanScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/16 15:41
 * @Desc：表结构初始化
 **/
public class TableStructsInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TableStructsInitializer.class);

    private final String[] packageScans;
    private final ExecuteSqlHandler sqlHandler;
    private final String dataBaseName;
    private final static String SELECT_COLUMN_SQL = "SELECT COLUMN_NAME FROM `information_schema`.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
    private final static String CREATE_COLUMN_AFTER_SQL = "ALTER TABLE `%s` add %s AFTER `%s`";
    private final static String CREATE_COLUMN_FIRST_SQL = "ALTER TABLE `%s` add %s FIRST";
    private final List<String> addColumnSqlList;
    private final List<String> addTableSqlList;


    public TableStructsInitializer(String[] packageScans, ExecuteSqlHandler sqlHandler) {
        this.packageScans = packageScans;
        this.sqlHandler = sqlHandler;
        this.addColumnSqlList = new ArrayList<>();
        this.addTableSqlList = new ArrayList<>();
        this.dataBaseName = sqlHandler.getDataBase();
    }


    public void initStart() throws Exception {
        CustomBeanScanner scanner = new CustomBeanScanner(packageScans);
        Set<Class<?>> beanRegisterList = scanner.getBeanRegisterList();
        buildTableInfo(beanRegisterList);

        // 执行更新
        if(!addColumnSqlList.isEmpty()) {
            sqlHandler.setAutoCommit(false);
            StringJoiner createNewColumnSql = new StringJoiner(";");
            addColumnSqlList.forEach(x -> {
                createNewColumnSql.add(x);
                logger.info("\nAdded new column as '{}'", x);
            });
            sqlHandler.executeTableSql(createNewColumnSql.toString());
        }

        if (!addTableSqlList.isEmpty()) {
            sqlHandler.setAutoCommit(false);
            StringJoiner createNewTableSql = new StringJoiner(";");
            addTableSqlList.forEach(x -> {
                createNewTableSql.add(x);
                logger.info("\nCreated new tableInfo as '\n{}'", x);
            });
            sqlHandler.executeTableSql(createNewTableSql.toString());
        }
    }

    /**
     * 更新表结构
     */
    public void buildTableInfo(Set<Class<?>> beanRegisterList) throws Exception {
        for (Class<?> entityClass : beanRegisterList) {
            TableSqlBuilder<?> sqlBuilder = TableInfoCache.getTableModel(entityClass);
            String exitsTableSql = sqlBuilder.getExitsTableSql(entityClass);
            String table = sqlBuilder.getTable();

            // 若存在，则进行下一步判断表字段是否存在
            if (ConvertUtil.conBool(sqlHandler.executeExist(exitsTableSql))) {
                buildTableColumnInfo(sqlBuilder, table);
                continue;
            }
            addTableSqlList.add(sqlBuilder.geCreateTableSql());
        }
    }

    /**
     * 更新表新增字段
     */
    private void buildTableColumnInfo(TableSqlBuilder<?> sqlBuilder, String table) throws Exception {
        String selectColumnSql = String.format(SELECT_COLUMN_SQL,
                sqlBuilder.getTable(), dataBaseName);
        List<String> columnList = sqlHandler.query(String.class, false, selectColumnSql);
        List<String> truthColumnList = sqlBuilder.getFieldParserModels().stream().map(DbFieldParserModel::getColumn).collect(Collectors.toList());

        for (int i = 0; i < truthColumnList.size(); i++) {
            String currColumn = truthColumnList.get(i);
            if (columnList.contains(currColumn)) {
                continue;
            }
            DbFieldParserModel<?> fieldParserModel = sqlBuilder.getFieldParserModels().get(i);
            String addColumnSql;
            if (i == 0) {
                addColumnSql = String.format(CREATE_COLUMN_FIRST_SQL, table, fieldParserModel.buildTableSql());
            }else {
                String beforeColumn = truthColumnList.get(i - 1);
                addColumnSql = String.format(CREATE_COLUMN_AFTER_SQL, table, fieldParserModel.buildTableSql(), beforeColumn);
            }
            addColumnSqlList.add(addColumnSql);
        }
    }


}
