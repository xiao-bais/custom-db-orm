package com.custom.springboot.tableinit;

import com.custom.action.core.DbFieldParserModel;
import com.custom.action.core.DbKeyParserModel;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.comm.annotations.DbTable;
import com.custom.comm.utils.*;
import com.custom.jdbc.executebody.BaseExecutorBody;
import com.custom.jdbc.executebody.ExecuteBodyHelper;
import com.custom.jdbc.executebody.SelectExecutorBody;
import com.custom.jdbc.executor.JdbcSqlSessionFactory;
import com.custom.jdbc.interfaces.CustomSqlSession;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.custom.springboot.scanner.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author  Xiao-Bai
 * @since  2022/5/16 15:41
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
    private final JdbcSqlSessionFactory sqlSessionFactory;

    /**
     * 连接的数据库
     */
    private final String dataBaseName;
    private final DatabaseAdapter databaseAdapter;

    /**
     * 需要添加的字段列表
     */
    private final List<String> addColumnSqlList;

    /**
     * 待创建的表合集
     */
    private final Map<String, TableCreateInfo> waitCreateMapper;

    private final static String SELECT_COLUMN_SQL = "SELECT COLUMN_NAME FROM `information_schema`.`COLUMNS` WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'";
    private final static String CREATE_COLUMN_AFTER_SQL = "ALTER TABLE `%s` add %s AFTER `%s`";
    private final static String CREATE_COLUMN_FIRST_SQL = "ALTER TABLE `%s` add %s FIRST";



    public TableStructsInitializer(String[] packageScans, JdbcSqlSessionFactory sqlSessionFactory) {
        this.packageScans = packageScans;
        AssertUtil.npe(sqlSessionFactory);
        this.sqlSessionFactory = sqlSessionFactory;
        this.databaseAdapter = sqlSessionFactory.getDatabaseAdapter();
        this.addColumnSqlList = new ArrayList<>();
        this.waitCreateMapper = new HashMap<>();
        this.dataBaseName = databaseAdapter.databaseName();
    }


    public void initStart() throws Exception {
        PackageScanner scanner = new PackageScanner(packageScans);
        Set<Class<?>> tableInfoList = scanner.getBeanRegisterList();
        buildTableInfo(tableInfoList);

        // 执行更新
        if(!addColumnSqlList.isEmpty()) {
            StringJoiner createNewColumnSql = new StringJoiner(";");
            addColumnSqlList.forEach(x -> {
                createNewColumnSql.add(x);
                logger.info("Added new column as '{}'\n", x);
            });
            this.handleExecSql(createNewColumnSql);
        }

        if (!waitCreateMapper.isEmpty()) {
            StringJoiner createNewTableSql = new StringJoiner(";");
            waitCreateMapper.forEach((table, tableInfo) -> {
                String createTableSql = buildCreateTableSql(tableInfo);
                createNewTableSql.add(createTableSql);
                logger.info("\nCreated new table for '{}' as ===================>\n\n{}\n", table, createTableSql);
            });
            this.handleExecSql(createNewTableSql);
        }
    }

    /**
     * 执行sql
     */
    private void handleExecSql(StringJoiner sql) throws Exception {
        BaseExecutorBody executorBody = ExecuteBodyHelper.createExecUpdate(sql.toString());
        CustomSqlSession sqlSession = sqlSessionFactory.createSqlSession(executorBody);
        sqlSessionFactory.getJdbcExecutor().execTableInfo(sqlSession);
    }

    /**
     * 查询待创建的表信息
     */
    public void buildTableInfo(Set<Class<?>> tableInfoList) throws Exception {
        for (Class<?> entityClass : tableInfoList) {
            if (!entityClass.isAnnotationPresent(DbTable.class)) {
                continue;
            }
            TableParseModel<?> tableModel = TableInfoCache.getTableModel(entityClass);
            TableParseModel<?> waitUpdateSqlBuilder = tableModel.clone();

            // 若表已存在，则进行下一步判断表字段是否存在
            boolean exists = databaseAdapter.existTable(tableModel.getTable());
            if (exists) {
                buildColumnInfo(waitUpdateSqlBuilder, tableModel.getTable());
                continue;
            }
            // // 若不存在，则加入创建表的对象中，保存待创建的表信息
            saveWaitCreateTableInfo(waitUpdateSqlBuilder, tableModel.getTable());
        }
    }

    /**
     * 保存待创建的表
     */
    private void saveWaitCreateTableInfo(TableParseModel<?> waitUpdateSqlBuilder, String table) {
        TableCreateInfo tableCreateInfo;
        if (waitCreateMapper.containsKey(table)) {
            tableCreateInfo = waitCreateMapper.get(table);
            addColumnInfos(waitUpdateSqlBuilder, tableCreateInfo.getColumnCreateInfos());
        }else {
            tableCreateInfo = new TableCreateInfo();
            tableCreateInfo.setTable(table);
            tableCreateInfo.setComment(waitUpdateSqlBuilder.getDesc());
            Set<ColumnCreateInfo> buildColumnSqls = new LinkedHashSet<>();
            addColumnInfos(waitUpdateSqlBuilder, buildColumnSqls);
            tableCreateInfo.setColumnCreateInfos(buildColumnSqls);
            waitCreateMapper.put(table, tableCreateInfo);
        }
        // 若主键为空，则加入主键的创建sql
        if (JudgeUtil.isEmpty(tableCreateInfo.getPrimaryKeyCreateSql())) {
            DbKeyParserModel<?> keyParserModel = waitUpdateSqlBuilder.getKeyParserModel();
            if (JudgeUtil.isNotEmpty(keyParserModel)) {
                tableCreateInfo.setPrimaryKeyCreateSql(keyParserModel.createTableSql());
            }
        }
    }


    private void addColumnInfos(TableParseModel<?> waitUpdateSqlBuilder, Set<ColumnCreateInfo> buildColumnSqls) {
        List<? extends DbFieldParserModel<?>> fieldParserModels = waitUpdateSqlBuilder.getDbFieldParseModels();
        for (DbFieldParserModel<?> fieldParserModel : fieldParserModels) {
            ColumnCreateInfo columnCreateInfo = new ColumnCreateInfo();
            columnCreateInfo.setColumn(fieldParserModel.getColumn());
            columnCreateInfo.setCreateColumnSql(fieldParserModel.createTableSql());
            if (buildColumnSqls.stream().noneMatch(x -> x.equals(columnCreateInfo))) {
                buildColumnSqls.add(columnCreateInfo);
            }
        }
    }

    /**
     * 更新表新增字段
     */
    private void buildColumnInfo(TableParseModel<?> sqlBuilder, String table) throws Exception {
        String selectColumnSql = String.format(SELECT_COLUMN_SQL,
                sqlBuilder.getTable(), dataBaseName);
        SelectExecutorBody<String> paramInfo = new SelectExecutorBody<>(String.class, selectColumnSql, false);
        CustomSqlSession sqlSession = sqlSessionFactory.createSqlSession(paramInfo);
        List<String> columnList = sqlSessionFactory.getJdbcExecutor().selectObjs(sqlSession);
        List<String> truthColumnList = sqlBuilder.getDbFieldParseModels().stream()
                .map(DbFieldParserModel::getColumn)
                .collect(Collectors.toList());
        int size = truthColumnList.size();
        for (int i = 0; i < size; i++) {
            String currColumn = truthColumnList.get(i);
            if (RexUtil.hasRegex(currColumn, RexUtil.back_quotes)) {
                currColumn = RexUtil.regexStr(currColumn, RexUtil.back_quotes);
            }
            if (columnList.contains(currColumn)) {
                continue;
            }
            List<? extends DbFieldParserModel<?>> fieldParserModels = sqlBuilder.getDbFieldParseModels();
            DbFieldParserModel<?> fieldParserModel = fieldParserModels.get(i);
            String addColumnSql;
            if (i == 0) {
                addColumnSql = String.format(CREATE_COLUMN_FIRST_SQL, table, fieldParserModel.createTableSql());
            } else {
                String beforeColumn = truthColumnList.get(i - 1);
                addColumnSql = String.format(CREATE_COLUMN_AFTER_SQL, table, fieldParserModel.createTableSql(), beforeColumn);
            }
            addColumnSqlList.add(addColumnSql);
        }
    }

    /**
     * 构建创建表的sql语句
     */
    private String buildCreateTableSql(TableCreateInfo tableCreateInfo) {
        StringBuilder createTableSql = new StringBuilder();
        StringJoiner fieldSql = new StringJoiner(Constants.SEPARATOR_COMMA_1);
        if (Objects.nonNull(tableCreateInfo.getPrimaryKeyCreateSql())) {
            fieldSql.add(tableCreateInfo.getPrimaryKeyCreateSql() + "\n");
        }
        Set<ColumnCreateInfo> columnCreateInfos = tableCreateInfo.getColumnCreateInfos();
        columnCreateInfos.stream().map(column -> column.getCreateColumnSql() + "\n").forEach(fieldSql::add);

        createTableSql.append(String.format("CREATE TABLE `%s` (\n%s)", tableCreateInfo.getTable(), fieldSql));
        if (JudgeUtil.isNotEmpty(tableCreateInfo.getComment())) {
            createTableSql.append(String.format(" COMMENT = '%s'", tableCreateInfo.getComment()));
        }
        return createTableSql.toString();
    }


}
