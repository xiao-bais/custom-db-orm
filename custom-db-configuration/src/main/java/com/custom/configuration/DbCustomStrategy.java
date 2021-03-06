package com.custom.configuration;

import com.custom.comm.SymbolConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/22
 * @Description
 */
@Component
@ConfigurationProperties(prefix = "custom.db.strategy")
public class DbCustomStrategy {

    /**
     * 下划线转驼峰
     */
    private boolean underlineToCamel = false;

    /**
     * 打印预编译的sql（默认只开启查询的sql）
     */
    private boolean sqlOutPrinting = false;

    /**
     * true- 打印的是可执行的sql
     * false- 打印的是预编译的sql
     */
    private boolean sqlOutPrintExecute = false;

    /**
    * 逻辑删除字段
    */
    private String dbFieldDeleteLogic = SymbolConstant.EMPTY;

    /**
    * 默认已删除字段值
    */
    private Object deleteLogicValue = SymbolConstant.EMPTY;

    /**
    * 默认未删除字段值
    */
    private Object notDeleteLogicValue = SymbolConstant.EMPTY;

    /**
    * 需要扫描的dao层包路径，加入spring容器中
    */
    private String[] mapperPackageScans;

    /**
    * 开启dao层的扫描包（mapperScanEnable = true后，才会开始扫描 `packageScans` 中指定的路径）
    */
    private boolean mapperScanEnable = false;

    /**
     * 扫描的实体类包
     */
    private String[] entityPackageScans;


    public boolean isMapperScanEnable() {
        return mapperScanEnable;
    }

    public void setMapperScanEnable(boolean mapperScanEnable) {
        this.mapperScanEnable = mapperScanEnable;
    }

    public String[] getMapperPackageScans() {
        return mapperPackageScans;
    }

    public void setMapperPackageScans(String[] mapperPackageScans) {
        this.mapperPackageScans = mapperPackageScans;
    }

    public String getDbFieldDeleteLogic() {
        return dbFieldDeleteLogic;
    }

    public void setDbFieldDeleteLogic(String dbFieldDeleteLogic) {
        this.dbFieldDeleteLogic = dbFieldDeleteLogic;
    }

    public Object getDeleteLogicValue() {
        return deleteLogicValue;
    }

    public void setDeleteLogicValue(Object deleteLogicValue) {
        this.deleteLogicValue = deleteLogicValue;
    }

    public Object getNotDeleteLogicValue() {
        return notDeleteLogicValue;
    }

    public void setNotDeleteLogicValue(Object notDeleteLogicValue) {
        this.notDeleteLogicValue = notDeleteLogicValue;
    }

    public boolean isSqlOutPrinting() {
        return sqlOutPrinting;
    }

    public void setSqlOutPrinting(boolean sqlOutPrinting) {
        this.sqlOutPrinting = sqlOutPrinting;
    }

    public boolean isUnderlineToCamel() {
        return underlineToCamel;
    }

    public void setUnderlineToCamel(boolean underlineToCamel) {
        this.underlineToCamel = underlineToCamel;
    }

    public boolean isSqlOutPrintExecute() {
        return sqlOutPrintExecute;
    }

    public void setSqlOutPrintExecute(boolean sqlOutPrintExecute) {
        this.sqlOutPrintExecute = sqlOutPrintExecute;
    }

    public String[] getEntityPackageScans() {
        return entityPackageScans;
    }

    public void setEntityPackageScans(String[] entityPackageScans) {
        this.entityPackageScans = entityPackageScans;
    }
}
