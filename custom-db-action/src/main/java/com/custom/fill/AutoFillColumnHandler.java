package com.custom.fill;

import com.custom.enums.FillStrategy;
import com.custom.sqlparser.TableInfoCache;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/21 14:18
 * @Desc：
 **/
public interface AutoFillColumnHandler {

    /**
     * 自动填充
     */
    List<TableAutoUpdateObject> fillStrategy();

}
