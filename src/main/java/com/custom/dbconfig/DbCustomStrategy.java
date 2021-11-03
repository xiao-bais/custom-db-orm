package com.custom.dbconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/22
 * @Description
 */
@Configuration
@ConfigurationProperties(prefix = "custom.db.strategy")
public class DbCustomStrategy {

    /**
     * 下划线转驼峰
     */
    private boolean underlineToCamel = false;

    /**
     * 打印sql
     */
    private boolean sqlOutPrinting = false;


    /**
    * 逻辑删除字段
    */
    private String dbFieldDeleteLogic = SymbolConst.EMPTY;

    /**
    * 删除字段值
    */
    private String deleteLogicValue = SymbolConst.EMPTY;

    /**
    * 不删除字段值
    */
    private String notDeleteLogicValue = SymbolConst.EMPTY;






    public String getDbFieldDeleteLogic() {
        return dbFieldDeleteLogic;
    }

    public void setDbFieldDeleteLogic(String dbFieldDeleteLogic) {
        this.dbFieldDeleteLogic = dbFieldDeleteLogic;
    }

    public String getDeleteLogicValue() {
        return deleteLogicValue;
    }

    public void setDeleteLogicValue(String deleteLogicValue) {
        this.deleteLogicValue = deleteLogicValue;
    }

    public String getNotDeleteLogicValue() {
        return notDeleteLogicValue;
    }

    public void setNotDeleteLogicValue(String notDeleteLogicValue) {
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

}
