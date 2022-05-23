package com.custom.springboot.tableinit;

import com.custom.action.sqlparser.DbFieldParserModel;
import com.custom.action.sqlparser.DbKeyParserModel;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.*;
import com.custom.comm.annotations.DbTable;
import com.custom.jdbc.ExecuteSqlHandler;
import com.custom.springboot.scanner.CustomBeanScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/16 15:41
 * @Desc：表结构初始化
 **/
public class TableStructsInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TableStructsInitializer.class);

    /**
     * 扫描的实体包路径
     */
    private final String[] packageScans;

    /**
     * jdbc执行对象
     */
    private final ExecuteSqlHandler sqlHandler;

    /**
     * 连接的数据库
     */
    private final String dataBaseName;

    /**
     * 需要添加的字段列表
     */
    private final List<String> addColumnSqlList;

    /**
     * 待创建的表合集
     */
    private final Map<String, TableSqlBuilder<?>> waitCreateMapper;

    private final static String SELECT_COLUMN_SQL = "SELECT COLUMN_NAME FROM `information_schema`.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
    private final static String CREATE_COLUMN_AFTER_SQL = "ALTER TABLE `%s` add %s AFTER `%s`";
    private final static String CREATE_COLUMN_FIRST_SQL = "ALTER TABLE `%s` add %s FIRST";



    public TableStructsInitializer(String[] packageScans, ExecuteSqlHandler sqlHandler) {
        this.packageScans = packageScans;
        this.sqlHandler = sqlHandler;
        this.addColumnSqlList = new ArrayList<>();
        this.waitCreateMapper = new HashMap<>();
        this.dataBaseName = sqlHandler.getDataBase();
    }


    public void initStart() throws Exception {
        CustomBeanScanner scanner = new CustomBeanScanner(packageScans);
        Set<Class<?>> tableInfoList = scanner.getBeanRegisterList();
        buildTableInfo(tableInfoList);

        // 执行更新
        if(!addColumnSqlList.isEmpty()) {
            StringJoiner createNewColumnSql = new StringJoiner(";");
            addColumnSqlList.forEach(x -> {
                createNewColumnSql.add(x);
                logger.info("Added new column as '{}'\n", x);
            });
            sqlHandler.executeTableSql(createNewColumnSql.toString());
        }

//        if (!addTableSqlList.isEmpty()) {
//            StringJoiner createNewTableSql = new StringJoiner(";");
//            addTableSqlList.forEach(x -> {
//                createNewTableSql.add(x);
//                logger.info("\nCreated new tableInfo as \n{}", x);
//            });
//            sqlHandler.executeTableSql(createNewTableSql.toString());
//        }
    }

    /**
     * 查询待创建的表信息
     */
    public void buildTableInfo(Set<Class<?>> tableInfoList) throws Exception {
        for (Class<?> entityClass : tableInfoList) {
            if (!entityClass.isAnnotationPresent(DbTable.class)) {
                continue;
            }
            TableSqlBuilder<?> sqlBuilder = TableInfoCache.getTableModel(entityClass);
            TableSqlBuilder<?> waitUpdateSqlBuilder = sqlBuilder.clone();
            String exitsTableSql = waitUpdateSqlBuilder.getExitsTableSql(entityClass);
            String table = waitUpdateSqlBuilder.getTable();

            // 若存在，则进行下一步判断表字段是否存在
            if (ConvertUtil.conBool(sqlHandler.executeExist(exitsTableSql))) {
                buildTableColumnInfo(waitUpdateSqlBuilder, table);
                continue;
            }

            if (waitCreateMapper.containsKey(table)) {
                TableSqlBuilder<?> tableSqlBuilder = waitCreateMapper.get(table);
                // 已同步待更新的字段列表
                List<? extends DbFieldParserModel<?>> waitUpdateFieldParserModels = tableSqlBuilder.getFieldParserModels();
                // 本次待同步的字段列表
                List<? extends DbFieldParserModel<?>> waitSyncFieldParserModels = waitUpdateSqlBuilder.getFieldParserModels();
//                if (waitSyncFieldParserModels.containsAll(waitUpdateFieldParserModels)) {
//                    waitUpdateSqlBuilder.setFieldParserModels(waitSyncFieldParserModels);
//                }
            }
            waitCreateMapper.put(table, waitUpdateSqlBuilder);
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
        int size = truthColumnList.size();
        for (int i = 0; i < size; i++) {
            String currColumn = truthColumnList.get(i);
            if (RexUtil.hasRegex(currColumn, RexUtil.back_quotes)) {
                currColumn = RexUtil.regexStr(currColumn, RexUtil.back_quotes);
            }
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

    /**
     * 构建创建表的sql语句
     */
    private String buildCreateTableSql(TableSqlBuilder<?> sqlBuilder) {
        StringBuilder createTableSql = new StringBuilder();
        StringJoiner fieldSql = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_1);
        if (Objects.nonNull(sqlBuilder.getKeyParserModel())) {
            DbKeyParserModel<?> keyParserModel = sqlBuilder.getKeyParserModel();
            fieldSql.add(keyParserModel.buildTableSql() + "\n");
        }
        List<? extends DbFieldParserModel<?>> fieldParserModels = sqlBuilder.getFieldParserModels();
        fieldParserModels.stream().map(dbFieldParserModel -> dbFieldParserModel.buildTableSql() + "\n").forEach(fieldSql::add);

        createTableSql.append(String.format("create table `%s` (\n%s)", sqlBuilder.getTable(), fieldSql));
        if (JudgeUtilsAx.isNotEmpty(sqlBuilder.getDesc())) {
            createTableSql.append(String.format(" COMMENT = '%s'", sqlBuilder.getDesc()));
        }
        return createTableSql.toString();
    }


}
