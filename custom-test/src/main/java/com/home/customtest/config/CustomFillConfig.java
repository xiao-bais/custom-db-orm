package com.home.customtest.config;

import com.custom.enums.FillStrategy;
import com.custom.fill.AutoFillColumnHandler;
import com.custom.fill.TableFillObject;
import com.home.customtest.entity.Aklis;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/21 15:46
 * @Desc：
 **/
@Component
public class CustomFillConfig implements AutoFillColumnHandler {
    @Override
    public List<TableAutoUpdateObject> fillStrategy() {

        List<TableAutoUpdateObject> tableFillObjects = new ArrayList<>();

        TableFillObject tableFillObject = new TableFillObject();
        tableFillObject.setStrategy(FillStrategy.INSERT);
        tableFillObject.setFieldName("createTime");
        tableFillObject.setFieldVal((int) (System.currentTimeMillis() / 1000));

        TableAutoUpdateObject tableAutoUpdateObject = new TableAutoUpdateObject();
        tableAutoUpdateObject.setEntityClass(Aklis.class);
        tableAutoUpdateObject.getTableFillObjects().add(tableFillObject);
        tableFillObjects.add(tableAutoUpdateObject);
        return tableFillObjects;
    }
}
