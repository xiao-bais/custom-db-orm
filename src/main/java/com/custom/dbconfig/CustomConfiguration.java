package com.custom.dbconfig;

import com.custom.comm.CustomUtil;
import com.custom.exceptions.ExceptionConst;
import com.custom.handler.proxy.SqlReaderExecuteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/23 17:47
 * @Desc：
 **/
@Configuration
public class CustomConfiguration {

    private DbDataSource dbDataSource;

    public CustomConfiguration(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }

    @Bean
    @ConditionalOnBean(DbDataSource.class)
    public SqlReaderExecuteProxy sqlReaderExecuteProxy() {
        if(ExceptionConst.currMap.get(CustomUtil.getConnKey(dbDataSource)) != null) {
            return new SqlReaderExecuteProxy(dbDataSource);
        }
        return null;
    }




}
