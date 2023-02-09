package com.custom.action.activerecord;

import com.custom.action.core.TableInfoCache;
import com.custom.action.interfaces.TableExecutor;
import com.custom.action.condition.ConditionWrapper;
import com.custom.comm.utils.ConvertUtil;
import com.custom.comm.utils.Constants;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.ReflectUtil;
import com.custom.jdbc.configuration.DbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 ActiveRecord装配模式的父类，继承该类即可获得crud功能（对象需要直接继承该类，否则子类的子类调用会报错）
 * 多数据源下，可通过重写{@link #order()}方法来指定不同数据源
 * @param <T> 实体对象类型
 * @param <P> 主键类型
 * @author   Xiao-Bai
 * @since  2022/7/15 0015 14:47
 *
 */
@SuppressWarnings("unchecked")
public class ActiveModel<T extends ActiveModel<T, P>, P extends Serializable> implements Serializable {


    /**
     * 根据主键删除多条记录
     */
    public boolean delete(List<P> keys) throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        return ConvertUtil.conBool(tableExecutor.deleteBatchKeys(keys));
    }


    /**
     * 根据条件删除记录
     */
    public boolean delete(ConditionWrapper<T> wrapper) throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        return ConvertUtil.conBool(tableExecutor.deleteSelective(wrapper));
    }

    /**
     * 根据主键删除一条记录
     */
    public boolean delete(P key) throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        return ConvertUtil.conBool(tableExecutor.deleteByKey(key));
    }

    /**
     * 删除此记录
     */
    public boolean delete() throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        P primaryKeyValue = tableExecutor.primaryKeyValue((T) this);
        if (primaryKeyValue == null) {
            throw new CustomCheckException("Value of primary key not specified");
        }
        return ConvertUtil.conBool(tableExecutor.deleteByKey(primaryKeyValue));
    }

    /**
     * 根据主键修改
     */
    public boolean update() throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        return ConvertUtil.conBool(tableExecutor.updateByKey((T) this));
    }

    /**
     * 根据主键是否为空自行插入或修改一条记录
     */
    public boolean save() throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        return ConvertUtil.conBool(tableExecutor.save((T) this));
    }

    /**
     * 插入一条记录
     */
    public boolean insert() throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        if (tableExecutor.primaryKeyValue((T) this) != null) {
            return false;
        }
        return ConvertUtil.conBool(tableExecutor.insert((T) this));
    }


    /**
     * 获取表操作对象
     */
    private TableExecutor<T, P> thisExecutor() {
        return TableInfoCache.getTableExecutor(order(), target());
    }

    /**
     * 插入多条记录
     */
    public boolean insert(List<T> tList) throws Exception {
        TableExecutor<T, P> tableExecutor = thisExecutor();
        return ConvertUtil.conBool(tableExecutor.insert(tList));
    }


    @SuppressWarnings("unchecked")
    private Class<T> target() {
       return ReflectUtil.getThisGenericType(ActiveModel.class);
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
