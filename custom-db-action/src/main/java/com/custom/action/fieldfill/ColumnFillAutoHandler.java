package com.custom.action.fieldfill;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/21 14:18
 * @Desc：
 **/
public interface ColumnFillAutoHandler {

    /**
     * 自动填充
     */
    List<TableFillObject> fillStrategy();

}
