package com.custom.action.activerecord;

import com.custom.action.sqlparser.JdbcSingleAction;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.configuration.DbCustomStrategy;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.GlobalDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/15 0015 14:47
 * @Desc ActiveRecord装配模式的父类，继承该类即可获得crud功能
 * T 实体对象类型
 * P 主键类型
 */
public class ActiveModel<T, P> {

    private static final Logger logger = LoggerFactory.getLogger(ActiveModel.class);

    private final Class<T> entityClass;
    private final CustomConfigHelper configHelper = GlobalDataHandler.getConfigHelper();

    private JdbcSingleAction<T, P> jdbcSingleAction() {
        if (configHelper == null) {
            throw new CustomCheckException("未配置数据源");
        }
        if (configHelper.getDbDataSource() == null) {
            throw new CustomCheckException("未找到匹配的数据源");
        }
        if (configHelper.getDbCustomStrategy() == null) {
            configHelper.setDbCustomStrategy(new DbCustomStrategy());
        }
        return new JdbcSingleAction<>(configHelper.getDbDataSource(),configHelper.getDbCustomStrategy(), entityClass);
    }


    @SuppressWarnings("unchecked")
    public ActiveModel() {
        try {
            ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
            this.entityClass = (Class<T>) actualTypeArguments[0];
            System.out.println("actualTypeArguments.toString() = " + Arrays.toString(actualTypeArguments));
        }catch (ClassCastException e) {
            logger.error("缺少有效泛型，并且实体对象仅支持直接继承于ActiveModel");
            throw e;
        }

    }
}
