package com.custom.jdbc.transaction;

import com.custom.comm.utils.Asserts;
import com.custom.comm.utils.Constants;
import com.custom.jdbc.configuration.GlobalDataHandler;
import com.custom.jdbc.back.BackResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * @author Xiao-Bai
 * @date 2022/10/9 20:34
 * @desc
 */
@SuppressWarnings("unchecked")
public class BackResultTransactionProxy<T> implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(BackResultTransactionProxy.class);

    public BackResultTransactionProxy(BackResult.Back<T> back) {
        this.back = back;
    }

    private final BackResult.Back<T> back;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        Connection connection = DbConnGlobal.getCurrentConnection();
        Asserts.notNull(connection, "未能获取到可用的连接");
        try {
            if (connection.getAutoCommit()) {
//                System.out.println("connection1 = " + connection);
//                System.out.println("设置自动提交为false");
                connection.setAutoCommit(false);
            }
            BackResult<T> result = null;
            if (args.length > 0) {
                result = (BackResult<T>) args[0];
            }
            back.execCall(result);
            connection = DbConnGlobal.getCurrentConnection();
//            System.out.println("connection2 = " + connection);
            if (!connection.getAutoCommit()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
//            System.out.println("connection3 = " + connection);
            connection.rollback();
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                }
            }
        }
        return null;
    }

    public BackResult.Back<T> getProxyBack() {
        ClassLoader classLoader = BackResult.Back.class.getClassLoader();
        Class<?>[] interfaces = new Class[]{BackResult.Back.class};
        return (BackResult.Back<T>) Proxy.newProxyInstance(classLoader, interfaces, this);
    }

}
