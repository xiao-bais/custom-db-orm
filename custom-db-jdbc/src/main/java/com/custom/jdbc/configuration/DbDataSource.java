package com.custom.jdbc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/31 22:35
 * @Description 数据库连接配置中心
 */
@Component
@ConfigurationProperties(prefix = "custom.db.datasource")
public class DbDataSource {

    /**
    * 连接驱动
    */
    private String driver;
    /**
    * 连接地址
    */
    private String url;
    /**
    * 连接用户名
    */
    private String username;
    /**
    * 连接用户密码
    */
    private String password;
    /**
    * 指定数据库
    */
    private String database;

    /**
     * 初始化配置
     */
    private int initialSize = 0;
    private int minIdle = 0;
    private int maxActive = 500;

    /**
     * 连接等待超时时间
     */
    private int maxWait = 60000;

    /**
     * 间隔多久进行一次检查,检查需要关闭的空闲连接
     */
    private int runsMillis = 60000;

    private String validationQuery = "select 1";

    private boolean testWhileIdle = true;

    private boolean testOnBorrow = false;

    private boolean testOnReturn = false;

    /**
     * 指定这是第几个数据源
     */
    private int order = 1;

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getRunsMillis() {
        return runsMillis;
    }

    public void setRunsMillis(int runsMillis) {
        this.runsMillis = runsMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public DbDataSource(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }



    public DbDataSource() {
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
