package com.home.customtest.config;

import com.custom.enums.FillStrategy;
import com.custom.fieldfill.AutoFillColumnHandler;
import com.custom.fieldfill.TableFillObject;
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
    public List<TableFillObject> fillStrategy() {

        List<TableFillObject> tableFillObjects = new ArrayList<>();

        TableFillObject tableFillObject = new TableFillObject();
        tableFillObject.setStrategy(FillStrategy.INSERT_UPDATE);
        tableFillObject.getTableFillMapper().put("createTime", (int) (System.currentTimeMillis() / 1000));
        tableFillObject.getTableFillMapper().put("updateTime", (int) (System.currentTimeMillis() / 1000));
        tableFillObject.setEntityClass(Aklis.class);

        tableFillObjects.add(tableFillObject);
        return tableFillObjects;
    }

}
