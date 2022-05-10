package com.custom.generator.core;

import com.custom.action.dbaction.AbstractSqlExecutor;
import com.custom.comm.date.DateTimeUtils;
import com.custom.generator.config.GlobalConfig;
import com.custom.generator.config.PackageConfig;
import com.custom.generator.config.TableConfig;
import com.custom.generator.model.ColumnStructModel;
import com.custom.generator.model.ServiceStructModel;
import com.custom.generator.model.TableStructModel;
import com.custom.action.sqlparser.JdbcAction;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.DbType;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.configuration.DbCustomStrategy;
import com.custom.configuration.DbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Xiao-Bai
 * @Date 2022/4/19 11:54
 * @Desc：
 **/
public class GenerateCodeExecutor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateCodeExecutor.class);

    private static String DATA_BASE;
    private List<TableStructModel> tableStructModels;
    private final AbstractSqlExecutor sqlExecutor;

    public GenerateCodeExecutor(DbDataSource dbDataSource, DbCustomStrategy dbCustomStrategy) {
        if (Objects.isNull(dbDataSource)) {
            ExThrowsUtil.toCustom("未配置数据源");
        }
        if(Objects.isNull(dbCustomStrategy)) {
            dbCustomStrategy = new DbCustomStrategy();
        }
        DATA_BASE = CustomUtil.getDataBase(dbDataSource.getUrl());
        this.dbCustomStrategy = dbCustomStrategy;
        this.sqlExecutor = new JdbcAction(dbDataSource, dbCustomStrategy);
    }

    public void start() {

        if(JudgeUtilsAx.isEmpty(tables)) {
            ExThrowsUtil.toCustom("未指定表名");
        }

        // 构建表实体结构信息
        buildTableEntityStructs();

        System.out.println("结束。。。");

    }



    /**
     * 初始化Service类
     */
    private void initService(TableStructModel tableInfo) {

        ServiceStructModel serviceInfo = new ServiceStructModel();
        serviceInfo.setServiceName(String.format(globalConfig.getServiceName(), tableInfo.getEntityTruthName()));
        serviceInfo.setServiceImplName(String.format(globalConfig.getServiceImplName(), tableInfo.getEntityTruthName()));
        serviceInfo.setAuthor(globalConfig.getAuthor());
        serviceInfo.setServicePackage(packageConfig.getService());
        serviceInfo.setCreateDate(DateTimeUtils.getThisDay(DateTimeUtils.yyyyMMddHHmm_));

        // 设置导入的包
        List<String> importPackages = Stream.of(
                "import com.custom.action.sqlparser.JdbcDao;",
                "import org.springframework.stereotype.Service;",
                "import org.springframework.beans.factory.annotation.Autowired;"
        ).collect(Collectors.toList());
        StringJoiner serviceImportPackage = new StringJoiner(SymbolConstant.POINT);
        if (JudgeUtilsAx.isNotEmpty(packageConfig.getParentPackage())) {
            serviceImportPackage.add(packageConfig.getParentPackage());
        }
        serviceImportPackage.add(packageConfig.getService()).add(serviceInfo.getServiceName());
        importPackages.add(String.format("import %s;", serviceImportPackage));
        serviceInfo.setImportPackages(importPackages);
        serviceInfo.setOverrideEnable(globalConfig.getOverrideEnable());
        serviceInfo.setSourcePackage(packageConfig.getParentPackage() + SymbolConstant.POINT + packageConfig.getService());
        serviceInfo.setServiceClassPath(globalConfig.getOutputDir() + SymbolConstant.FILE_SEPARATOR + serviceInfo.getSourcePackage().replace(SymbolConstant.POINT, SymbolConstant.FILE_SEPARATOR));
        serviceInfo.setServiceImplClassPath(serviceInfo.getServiceClassPath() + SymbolConstant.FILE_SEPARATOR + "impl");
        tableInfo.setServiceStructModel(serviceInfo);
    }

    /**
     * 构建表实体类
     */
    protected void buildTableEntityStructs() {

        String tableStr = Arrays.stream(tables)
                .filter(CustomUtil::isNotBlank)
                .map(x -> String.format("'%s'", x))
                .collect(Collectors.joining(","));

        handleTruthTables(tableStr);
        if(tableStructModels.isEmpty()) {
            ExThrowsUtil.toCustom("表名皆不存在");
        }

        String selectTableSql = String.format(CustomUtil.loadFiles("/sql/queryTableColumnStruct.sql"), tableStr, DATA_BASE);
        List<ColumnStructModel> columnStructModels = new ArrayList<>();
        try {
            columnStructModels = sqlExecutor.executeQueryNotPrintSql(ColumnStructModel.class, selectTableSql);
            if(columnStructModels.isEmpty()) {
                return;
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

        // 以表名分组后，得到以表名为key 字段集合为value的map
        Map<String, List<ColumnStructModel>> tableColumnMap = columnStructModels.stream().collect(Collectors.groupingBy(ColumnStructModel::getTable));

        for (TableStructModel tableInfo : tableStructModels) {

            // 表与实体基础配置
            entityInitialize(tableInfo);

            // 配置生成路径
            setTableEntityPath(tableInfo);

            // 获取表对应的字段集合
            List<ColumnStructModel> columnStructModelList = tableColumnMap.get(tableInfo.getTable());

            // 构建实体属性字段信息
            buildEntityFieldInfo(tableInfo, columnStructModelList);

            // 配置实体导入包信息
            setEntityImportPackages(tableInfo);

            // 初始化Service
            initService(tableInfo);
        }
    }

    /**
     * 表与实体基础配置
     */
    private void entityInitialize(TableStructModel tableInfo) {
        tableInfo.setLombok(globalConfig.getEntityLombok());
        tableInfo.setSwagger(globalConfig.getSwagger());
        tableInfo.setOverrideEnable(globalConfig.getOverrideEnable());
        tableInfo.setEntityPackage(packageConfig.getEntity());
        tableInfo.setAuthor(globalConfig.getAuthor());
        tableInfo.setCreateDate(DateTimeUtils.getThisDay(DateTimeUtils.yyyyMMddHHmm_));
        String tableName = tableInfo.getTable();
        // 若配置了忽略前缀，则去除指定的前缀
        if(JudgeUtilsAx.isNotEmpty(tableConfig.getTablePrefix())) {
            tableName = tableName.replaceFirst(tableConfig.getTablePrefix(), SymbolConstant.EMPTY);
        }
        // 下划线转驼峰
        if(dbCustomStrategy.isUnderlineToCamel()) {
            tableName = CustomUtil.underlineToCamel(tableName);
            String tableStartStr = tableName.substring(0, 1);
            tableName = tableName.replaceFirst(tableStartStr, tableStartStr.toUpperCase(Locale.ROOT));
        }
        tableInfo.setEntityTruthName(tableName);
        // 若配置了后缀，则拼接后缀
        String entityName = "";
        if (JudgeUtilsAx.isNotEmpty(tableConfig.getEntitySuffix())) {
            entityName = tableName + tableConfig.getEntitySuffix();
        }
        tableInfo.setEntityName(entityName);
    }

    /**
     * 设置表实体的生产路径
     */
    private void setTableEntityPath(TableStructModel tableInfo) {
        String entityClassPath = "";
        if(JudgeUtilsAx.isNotEmpty(globalConfig.getOutputDir())) {
            entityClassPath = globalConfig.getOutputDir();
        }
        if(JudgeUtilsAx.isNotEmpty(packageConfig.getParentPackage())) {
            String packageName = packageConfig.getParentPackage();
            String packagePath = "";
            if(JudgeUtilsAx.isNotEmpty(packageConfig.getEntity())) {
                packagePath = packageName.replace(SymbolConstant.POINT, SymbolConstant.FILE_SEPARATOR) + SymbolConstant.FILE_SEPARATOR + packageConfig.getEntity();
                tableInfo.setSourcePackage(packageName + SymbolConstant.POINT + tableInfo.getEntityPackage());
            }
            entityClassPath = entityClassPath + SymbolConstant.FILE_SEPARATOR + packagePath;
        }
        tableInfo.setEntityClassPath(entityClassPath);
    }

    /**
     * 设置实体的包信息导入
     */
    private void setEntityImportPackages(TableStructModel tableInfo) {

        List<String> importOtherPackages = new ArrayList<>();

        // 字段中的所有类型
        List<? extends Class<?>> fieldTypes = tableInfo.getColumnStructModels().stream().map(ColumnStructModel::getFieldType).collect(Collectors.toList());
        List<String> importJavaPackages = fieldTypes.stream().filter(x -> !x.getName().contains("java.lang")).distinct().map(fieldType -> SymbolConstant.IMPORT + fieldType.getName() + ";").collect(Collectors.toList());

        // 添加@Db*注解导入包信息
        String dbAnnotationPackage = SymbolConstant.IMPORT + "com.custom.comm.annotations.";
        importOtherPackages.add(dbAnnotationPackage + "DbField;");
        importOtherPackages.add( dbAnnotationPackage + "DbKey;");
        importOtherPackages.add(dbAnnotationPackage + "DbTable;");

        // 导入lombok
        if(tableInfo.getLombok()) {
            importOtherPackages.add(SymbolConstant.IMPORT + "lombok.Data;");
        }
        if(tableInfo.getSwagger()) {
            importOtherPackages.add(SymbolConstant.IMPORT + "io.swagger.annotations.ApiModelProperty;");
        }
        // 导入主键自增标识
        if(tableInfo.getColumnStructModels().stream().anyMatch(x -> x.getKeyExtra().equalsIgnoreCase("auto_increment"))) {
            importOtherPackages.add(SymbolConstant.IMPORT + "com.custom.comm.enums.KeyStrategy;");
        }

        tableInfo.setImportJavaPackages(importJavaPackages);
        tableInfo.setImportOtherPackages(importOtherPackages);
    }


    /**
     * 构建实体字段的基础信息，以及整理字段与表之间的关系
     */
    private void buildEntityFieldInfo(TableStructModel tableInfo, List<ColumnStructModel> columnStructModels) {

        for (ColumnStructModel columnModel : columnStructModels) {
            DbType dbType = DbType.getDbType(columnModel.getColumnType());
            if(Objects.isNull(dbType)) {
                logger.info("表{}中字段{}暂无可匹配的类型，暂用java.lang.String替代", tableInfo.getTable(), columnModel.getColumn());
                dbType = DbType.DbVarchar;
            }
            columnModel.setDbType(dbType);
            columnModel.setFieldType(dbType.getFieldType());
            columnModel.setFieldTypeName(dbType.getFieldType().getSimpleName());
            String column = columnModel.getColumn();
            if (dbCustomStrategy.isUnderlineToCamel()) {
                column = CustomUtil.underlineToCamel(column);
            }
            columnModel.setFieldName(column);

            // getter/setter
            String columnStart = column.substring(0, 1).toUpperCase(Locale.ROOT);
            columnModel.setGetterMethodName(SymbolConstant.GETTER + columnStart + column.substring(1));
            columnModel.setSetterMethodName(SymbolConstant.SETTER + columnStart + column.substring(1));

            // @Db字段注解信息
            columnModel.setDbFieldAnnotation(dbFieldAnnotation(columnModel));
            // 属性字段
            columnModel.setOutputFieldInfo(String.format("%s %s %s;", SymbolConstant.PRIVATE, columnModel.getFieldType().getSimpleName(), columnModel.getFieldName()));
        }

        tableInfo.setColumnStructModels(columnStructModels);
    }

    /**
     * 处理@Db字段注解
     */
    private String dbFieldAnnotation(ColumnStructModel columnModel) {
        if (!columnModel.getPrimaryKey()) {
            return tableConfig.getEntityDbFieldAnnotationValueEnable() ?
                    String.format("@DbField(value = \"%s\")", columnModel.getColumn()) : "@DbField";
        }
        // @DbKey
        if (!tableConfig.getEntityDbFieldAnnotationValueEnable()) {
            return "@DbKey";
        }
        // 主键增值策略
        if (Objects.nonNull(globalConfig.getKeyStrategy())) {
            return String.format("@DbKey(value = \"%s\", strategy = KeyStrategy.%s)", columnModel.getColumn(), globalConfig.getKeyStrategy());
        }
        return String.format("@DbKey(value = \"%s\")", columnModel.getColumn());
    }



    /**
     * 获取真实表名
     */
    private void handleTruthTables(String tableStr) {
        try {
            String selectTableSql  = String.format(CustomUtil.loadFiles("/sql/queryTableStruct.sql"), tableStr, DATA_BASE);
            tableStructModels = sqlExecutor.executeQueryNotPrintSql(TableStructModel.class, selectTableSql);
        }catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }





    /**
     * 数据源
     */
    private DbDataSource dbDataSource;

    /**
     * 策略配置
     */
    private DbCustomStrategy dbCustomStrategy;

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig;

    /**
     * 包的配置
     */
    private PackageConfig packageConfig;

    /**
     * 表与实体的配置
     */
    private TableConfig tableConfig;

    /**
     * 需要生成的表
     */
    private String[] tables;

    public DbDataSource getDbDataSource() {
        return dbDataSource;
    }

    public void setDbDataSource(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }

    public DbCustomStrategy getDbCustomStrategy() {
        return dbCustomStrategy;
    }

    public void setDbCustomStrategy(DbCustomStrategy dbCustomStrategy) {
        this.dbCustomStrategy = dbCustomStrategy;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public PackageConfig getPackageConfig() {
        return packageConfig;
    }

    public void setPackageConfig(PackageConfig packageConfig) {
        this.packageConfig = packageConfig;
    }

    public TableConfig getTableConfig() {
        return tableConfig;
    }

    public void setTableConfig(TableConfig tableConfig) {
        this.tableConfig = tableConfig;
    }

    public String[] getTables() {
        return tables;
    }

    public void setTables(String[] tables) {
        this.tables = tables;
    }

    public List<TableStructModel> getTableStructModels() {
        return tableStructModels;
    }
}
