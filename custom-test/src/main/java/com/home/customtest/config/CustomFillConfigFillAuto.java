package com.home.customtest.config;

import com.custom.action.fieldfill.ColumnFillAutoHandler;
import com.custom.action.fieldfill.TableFillObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Xiao-Bai
 * @since  2022/3/21 15:46
 * @Desc：
 **/
//@Component
public class CustomFillConfigFillAuto implements ColumnFillAutoHandler {
    @Override
    public List<TableFillObject> fillStrategy() {

        List<TableFillObject> tableFillObjects = new ArrayList<>();

//        TableFillObject<> tableFillObject = new TableFillObject();
//        tableFillObject.setStrategy(FillStrategy.INSERT_UPDATE);
//        tableFillObject.getTableFillMapper().put("createTime", (int) (System.currentTimeMillis() / 1000));
//        tableFillObject.getTableFillMapper().put("updateTime", (int) (System.currentTimeMillis() / 1000));
//        tableFillObject.setEntityClass(Student.class);
//
//        tableFillObjects.add(tableFillObject);
        return tableFillObjects;
    }

}
