package com.custom.action.fieldfill;

import java.util.List;

/**
 * @author   Xiao-Bai
 * @since  2022/3/21 14:18
 * ：
 **/
public interface ColumnFillAutoHandler {

    /**
     * 自动填充
     */
    List<TableFillObject> fillStrategy();

}
