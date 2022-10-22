package com.custom.action.activerecord;

import com.custom.action.dbaction.JdbcActiveWrapper;
import com.custom.action.sqlparser.DefaultTableAction;
import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.jdbc.configuration.DbCustomStrategy;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.jdbc.configuration.DbDataSource;
import com.custom.jdbc.transaction.DbConnGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/7/15 0015 14:47
 * ActiveRecord装配模式的父类，继承该类即可获得crud功能（对象需要直接继承该类，否则子类的子类调用会报错）
 * 多数据源下，可通过重写{@link #order()}方法来指定不同数据源
 * T 实体对象类型
 * P 主键类型
 */
@SuppressWarnings("unchecked")
public class ActiveModel<T extends ActiveModel<T, P>, P extends Serializable> implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ActiveModel.class);

    /**
     * 根据主键删除多条记录
     */
    public boolean delete(List<P> keys) {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        return ConvertUtil.conBool(activeWrapper.deleteBatchKeys(keys));
    }


    /**
     * 根据条件删除记录
     */
    public boolean delete(ConditionWrapper<T> wrapper) {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        return ConvertUtil.conBool(activeWrapper.deleteByCondition(wrapper));
    }

    /**
     * 根据主键删除一条记录
     */
    public boolean delete(P key) {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        return ConvertUtil.conBool(activeWrapper.deleteByKey(key));
    }

    /**
     * 删除此记录
     */
    public boolean delete() {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        P primaryKeyValue = activeWrapper.primaryKeyValue((T) this);
        if (primaryKeyValue == null) {
            ExThrowsUtil.toCustom("Value of primary key not specified");
        }
        return ConvertUtil.conBool(activeWrapper.deleteByKey(primaryKeyValue));
    }

    /**
     * 根据主键修改
     */
    public boolean update() {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        return ConvertUtil.conBool(activeWrapper.updateByKey((T) this));
    }

    /**
     * 根据主键是否为空自行插入或修改一条记录
     */
    public boolean save() {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        return ConvertUtil.conBool(activeWrapper.save((T) this));
    }

    /**
     * 插入一条记录
     */
    public boolean insert() {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        if (activeWrapper.primaryKeyValue((T) this) != null) {
            return false;
        }
        return ConvertUtil.conBool(activeWrapper.insert((T) this));
    }

    /**
     * 插入多条记录
     */
    public boolean insert(List<T> tList) {
        JdbcActiveWrapper<T, P> activeWrapper = activeWrapper();
        return ConvertUtil.conBool(activeWrapper.insert(tList));
    }


    private JdbcActiveWrapper<T, P> activeWrapper() {
        CustomConfigHelper configHelper = DbConnGlobal.getConfigHelper(order());
        if (configHelper == null) {
            throw new CustomCheckException("No data source configured");
        }
        if (configHelper.getDbDataSource() == null) {
            throw new CustomCheckException("No matching data source found");
        }
        if (configHelper.getDbCustomStrategy() == null) {
            configHelper.setDbCustomStrategy(new DbCustomStrategy());
        }
        return new DefaultTableAction<>(configHelper.getDbDataSource(),configHelper.getDbCustomStrategy(), entityClass());
    }


    @SuppressWarnings("unchecked")
    public Class<T> entityClass() {
        try {
            ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
            return (Class<T>) actualTypeArguments[0];
        }catch (ClassCastException e) {
            logger.error("Valid generics are missing, and entity objects only support direct inheritance from 'ActiveModel'");
            throw e;
        }

    }
    
    /**
     * @see DbDataSource#getOrder()
     * 多个数据源的情况下，可由此指定数据源
     * @return {@link DbDataSource#getOrder()}
     */
    public int order() {
        return Constants.DEFAULT_ONE;
    }
}
