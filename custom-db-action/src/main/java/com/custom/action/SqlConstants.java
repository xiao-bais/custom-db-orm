package com.custom.action;

/**
 * @author Xiao-Bai
 * @date 2022/5/12 21:29
 * @desc:sql操作模板
 */
public class SqlConstants {

    /**
     * 单表sql查询模板
     */
    public final static String SELECT_TEMPLATE = "select {columns} {columns} \nfrom {table} {alias}";

    /**
     * 表关联
     */
    public final static String SELECT_JOIN_TEMPLATE = "select {columns} \nfrom {table}  {alias} \n{joinTables}";

    /**
     * 查询数量
     */
    public final static String SELECT_COUNT_TEMPLATE = "select count(1) from ({selectSql}) xxx";

    /**
     * 删除sql模板
     */
    public final static String DELETE_TEMPLATE = "delete from {table} {alias}";

    /**
     * 修改sql模板
     */
    public final static String UPDATE_TEMPLATE = "update {table} {alias} set {setValues}";

    /**
     * 插入sql模板
     */
    public final static String INSERT_TEMPLATE = "insert into {table}({columns}) values {addValues}";



}
