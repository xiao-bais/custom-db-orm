package com.custom.sqlparser;

import com.custom.annotations.*;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.ExecuteMethod;
import com.custom.handler.CheckExecute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 14:10
 * @Desc：构建实体表的基础模板，以及提供一系列的sql语句或字段
 **/
public class TableSqlBuilder<T> {

    private static Logger logger = LoggerFactory.getLogger(TableSqlBuilder.class);

    private Class<T> cls;

    private T t;

    private List<T> list;

    private String table;

    private String alias;

    private Field[] fields;
    /**
    * @Desc：对于@DbRelated注解的解析
    */
    private DbKeyParserModel<T> keyParserModel = null;
    /**
    * @desc:对于@DbField注解的解析
    */
    private List<DbFieldParserModel<T>> fieldParserModels = new ArrayList<>();
    /**
    * @Desc：对于@DbRelated注解的解析
    */
    private List<DbRelationParserModel<T>> relatedParserModels = new ArrayList<>();
    /**
    * @Desc:对于@DbJoinTables注解的解析
    */
    private Map<String, String> joinTableParserModelMap = new HashMap<>();
    /**
     * @Desc:对于@DbJoinTables注解的解析
     */
    private List<String> joinTableParserModels = new ArrayList<>();
    /**
     * @Desc:查询的sql语句
     */
    private StringBuilder selectSql = new StringBuilder();
    /**
    * @Desc:插入的sql语句
    */
    private StringJoiner insertSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
    /**
    * @Desc:插入的`?`
    */
    private StringJoiner insetSymbol = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
    /**
    * @Desc:对象的所有值
    */
    private List<Object> objValues = new ArrayList<>();



    /**
    * 获取查询sql
    */
    public String getSelectSql() {
        try {
            if (CustomUtil.isDbRelationTag(this.cls) || this.cls.isAnnotationPresent(DbJoinTables.class)) {
                getSelectRelationSql();
            }else {
                getSelectBaseTableSql();
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConst.EMPTY;
        }
        return selectSql.toString();
    }

    /**
    * 获取对象所有字段的值(多个对象)
    */
    public List<Object> getManyObjValues() {
        if(objValues.isEmpty()) {
            for (T t : list) {
                if (keyParserModel != null) {
                    if (t == null) throw new NullPointerException();
                    objValues.add(keyParserModel.getValue(t));
                }
                if (!fieldParserModels.isEmpty()) {
                    fieldParserModels.forEach(x -> objValues.add(x.getValue(t)));
                }
            }
        }
        return objValues;
    }

    /**
     * 获取对象所有字段的值(单个对象)
     */
    public List<Object> getOneObjValues() {
        if(objValues.isEmpty()) {
            if (keyParserModel != null) {
                objValues.add(keyParserModel.generateKey());
            }
            if (!fieldParserModels.isEmpty()) {
                fieldParserModels.forEach(x -> objValues.add(x.getValue(t)));
            }
        }
        return objValues;
    }

    /**
    * 获取添加sql
    */
    public String getInsertSql() {
        try {
            if(keyParserModel != null) {
                insertSql.add(String.format("`%s`",keyParserModel.getDbKey()));
            }
            if(!fieldParserModels.isEmpty()) {
                fieldParserModels.forEach(x -> insertSql.add(String.format("`%s`", x.getColumn())));
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return SymbolConst.EMPTY;
        }
        return String.format("insert into %s(%s) values %s ", this.table, insertSql.toString(), getInsertSymbol());
    }

    /**
     * 获取添加的？
     */
    private String getInsertSymbol() {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StringJoiner brackets = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1, SymbolConst.BRACKETS_LEFT, SymbolConst.BRACKETS_RIGHT);
            if(keyParserModel != null) {
                brackets.add(SymbolConst.QUEST);
            }
            if(!fieldParserModels.isEmpty()) {
                fieldParserModels.forEach(x -> brackets.add(SymbolConst.QUEST));
            }
            insetSymbol.add(brackets.toString());
        }
        return insetSymbol.toString();
    }


    /**
    * 创建表结构
    */
    public String geCreateTableSql() {
        StringBuilder createTableSql = new StringBuilder();
        StringJoiner fieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        if(this.keyParserModel != null) {
            fieldSql.add(keyParserModel.buildTableSql() + "\n");
        }

        if(!this.fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(dbFieldParserModel -> dbFieldParserModel.buildTableSql() + "\n").forEach(fieldSql::add);
        }

        createTableSql.append(String.format("create table `%s` (\n%s)", this.table, fieldSql.toString()));
        return createTableSql.toString();
    }

    /**
    * 删除表结构
    */
    public String getDropTableSql() {
        return String.format("drop table if exists `%s`", this.table);
    }

    /**
     * 生成表查询sql语句
     */
    private void getSelectBaseTableSql() {
        StringJoiner baseFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (keyParserModel != null) {
            baseFieldSql.add(keyParserModel.getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第三步 拼接主表
        selectSql.append(String.format("select %s\n from `%s` %s \n", baseFieldSql.toString(), this.table, this.alias));
    }


    /**
     * 关联的sql分为两部分
     * 一是 @DbJoinTables注解，二是@DbRelated注解
     * 默认按注解放置顺序载入，优先加载DbJoinTables注解(顺带优先@DbMap的查询字段)
     */
    private void getSelectRelationSql() {

        StringJoiner baseFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);

        // 第一步 拼接主键
        if (keyParserModel != null) {
            baseFieldSql.add(keyParserModel.getSelectFieldSql());
        }

        // 第二步 拼接此表的其他字段
        if (!fieldParserModels.isEmpty()) {
            fieldParserModels.stream().map(DbFieldParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第三步 拼接以joinTables的方式关联的查询字段
        if (!joinTableParserModelMap.isEmpty()) {
            joinTableParserModelMap.forEach((k, v) -> baseFieldSql.add(String.format("%s `%s`", k, v)));
        }

        // 第三步 拼接以related方式关联的查询字段
        if (!relatedParserModels.isEmpty()) {
            relatedParserModels.stream().map(DbRelationParserModel::getSelectFieldSql).forEach(baseFieldSql::add);
        }

        // 第四步 拼接主表
        selectSql.append(String.format("select %s\n from `%s` %s\n", baseFieldSql.toString(), this.table, this.alias));

        // 第五步 拼接以joinTables方式的关联条件
        if (!joinTableParserModels.isEmpty()) {
            joinTableParserModels.stream().map(model -> String.format("\n %s", model)).forEach(selectSql::append);
        }

        // 第六步 拼接以related方式的关联条件
        if (!relatedParserModels.isEmpty()) {
            selectSql.append(getRelatedTableSql(relatedParserModels)).append(" \n");
        }
    }

    /**
     * 拼接related的表关联
     */
    private String getRelatedTableSql(List<DbRelationParserModel<T>> relatedParserModels) {
        StringBuilder joinTableSql = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        for (DbRelationParserModel<T> model : relatedParserModels) {
            String condition = String.format("%s@%s@%s", model.getJoinTable(), model.getJoinAlias(), model.getCondition());
            if (!conditions.contains(condition)) {
                joinTableSql.append("\n").append(String.format("%s `%s` %s on %s", model.getJoinStyle(), model.getJoinTable(), model.getJoinAlias(), model.getCondition()));
                conditions.add(condition);
            }
        }
        return joinTableSql.toString();
    }


    public TableSqlBuilder(Class<T> cls, ExecuteMethod method) {
        this.cls = cls;
        DbTable annotation = cls.getAnnotation(DbTable.class);
        this.alias = annotation.alias();
        this.table = annotation.table();
        this.fields = CustomUtil.getFields(this.cls);
        initTableBuild(method);
    }


    /**
    * 初始化
    */
    void initTableBuild(ExecuteMethod method){
        switch (method) {
            case NONE:
                break;
            case SELECT:
                buildSelectModels();
                break;
            case UPDATE:
            case INSERT:
                buildUpdateModels();
                break;
            case DELETE:
                buildDeleteModels();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + method);
        }
    }

    /**
    * 默认构造方法为查询
    */
    public TableSqlBuilder(Class<T> cls) {
        this(cls, ExecuteMethod.SELECT);
    }

    public TableSqlBuilder(T t) {
        this.t = t;
        this.list = new ArrayList<>();
        this.list.add(t);
        DbTable annotation = t.getClass().getAnnotation(DbTable.class);
        this.fields = CustomUtil.getFields(t.getClass());
        this.alias = annotation.alias();
        this.table = annotation.table();
        buildUpdateModels();
    }

    public TableSqlBuilder(List<T> tList) {
        this.list = tList;
        this.t = tList.get(0);
        DbTable annotation = t.getClass().getAnnotation(DbTable.class);
        this.fields = CustomUtil.getFields(t.getClass());
        this.alias = annotation.alias();
        this.table = annotation.table();
        buildUpdateModels();
    }

    /**
    * 构造查询模板
    */
    private void buildSelectModels() {
        DbJoinTables joinTables = this.cls.getAnnotation(DbJoinTables.class);
        if (joinTables != null) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(joinTableParserModels::add);
        }
        Field[] fields = CustomUtil.getFields(this.cls);
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbRelated.class)) {
                DbRelationParserModel<T> relatedParserModel = new DbRelationParserModel<>(this.cls, field, this.table, this.alias);
                relatedParserModels.add(relatedParserModel);

            } else if (field.isAnnotationPresent(DbKey.class) && keyParserModel == null) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias);
                fieldParserModels.add(fieldParserModel);

            } else if (field.isAnnotationPresent(DbMap.class)) {
                DbMap dbMap = field.getAnnotation(DbMap.class);
                joinTableParserModelMap.put(CustomUtil.getJoinFieldStr(dbMap.value().trim()), field.getName());
            }
        }
    }

    /**
    * 构造增改模板
    */
    private void buildUpdateModels() {
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class) && keyParserModel == null) {
                keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias);

            } else if (field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field, this.table, this.alias);
                fieldParserModels.add(fieldParserModel);
            }
        }
    }

    /**
    * 构造删除模板
    */
    private void buildDeleteModels() {
        Optional<Field> fieldOptional = Arrays.stream(fields).filter(x -> x.isAnnotationPresent(DbKey.class)).findFirst();
        fieldOptional.ifPresent(field -> keyParserModel = new DbKeyParserModel<>(field, this.table, this.alias));
    }

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    public DbKeyParserModel<T> getKeyParserModel() {
        return keyParserModel;
    }
}
