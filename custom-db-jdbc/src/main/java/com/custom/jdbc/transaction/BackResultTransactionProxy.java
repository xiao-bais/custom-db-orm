package com.custom.jdbc.transaction;

import com.custom.comm.utils.Constants;
import com.custom.configuration.DbConnection;
import com.custom.configuration.DbDataSource;
import com.custom.jdbc.CustomConfigHelper;
import com.custom.jdbc.GlobalDataHandler;
import com.custom.jdbc.back.BackResult;
import jdk.nashorn.internal.ir.BaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Savepoint;

/**
 * @author Xiao-Bai
 * @date 2022/10/9 20:34
 * @desc
 */
@SuppressWarnings("unchecked")
public class BackResultTransactionProxy<T> implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(BackResultTransactionProxy.class);

    public BackResultTransactionProxy(BackResult.Back<T> back) {
        CustomConfigHelper configHelper = (CustomConfigHelper) GlobalDataHandler.readGlobalObject(Constants.DATA_CONFIG);
        if (configHelper != null) {
            DbDataSource dbDataSource = configHelper.getDbDataSource();
            this.connection = (Connection) DbConnection.currMap.get(DbConnection.getConnKey(dbDataSource));
        }
        this.back = back;
    }

    private Connection connection;
    private final BackResult.Back<T> back;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        try {
            connection.setAutoCommit(false);
            back.execCall((BackResult<T>) args[0]);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            connection.rollback();
            throw e;
        }
        return null;
    }

    public BackResult.Back<T> getProxyBack() {
        ClassLoader classLoader = BackResult.Back.class.getClassLoader();
        Class<?>[] interfaces = new Class[]{BackResult.Back.class};
        return (BackResult.Back<T>) Proxy.newProxyInstance(classLoader, interfaces, this);
    }
}
