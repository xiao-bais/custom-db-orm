package com.custom.action.interfaces;

import java.util.List;

/**
 * 查询结果后，做一些数据的补充
 * @author   Xiao-Bai
 * @since  2023/1/2 15:38
 */
public interface SqlQueryAfter {

    <T> void handle(Class<T> target, List<T> result) throws Exception;

}
