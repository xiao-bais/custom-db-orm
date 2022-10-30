package com.custom.jdbc.configuration;

import com.custom.comm.utils.Constants;
import com.custom.comm.enums.Rollback;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/22
 * @Description 策略配置
 */
@Component
@ConfigurationProperties(prefix = "custom.db.strategy")
public class DbCustomStrategy {

    /**
     * 下划线转驼峰
     */
    private boolean underlineToCamel = false;

    /**
     * 打印预编译的sql（默认只开启参数为 '?' 的sql）
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
    private String dbFieldDeleteLogic = Constants.EMPTY;

    /**
    * 默认已删除字段值
    */
    private Object deleteLogicValue = Constants.EMPTY;

    /**
    * 默认未删除字段值
    */
    private Object notDeleteLogicValue = Constants.EMPTY;

    /**
    * 需要扫描的dao层包路径，加入spring容器中
    */
    private String[] mapperPackageScans;

    /**
     * 扫描的实体类包
     */
    private String[] entityPackageScans;

    /**
     * 开启同步实体与表结构（当syncEntityEnable = true后，则开始扫描 `entityPackageScans` 中指定的路径）
     * 当syncEntityEnable = true时，在容器启动后，自动同步更新表结构
     */
    private boolean syncEntityEnable = false;


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

    public boolean isSyncEntityEnable() {
        return syncEntityEnable;
    }

    public void setSyncEntityEnable(boolean syncEntityEnable) {
        this.syncEntityEnable = syncEntityEnable;
    }

}
