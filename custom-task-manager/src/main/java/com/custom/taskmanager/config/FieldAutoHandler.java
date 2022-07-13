package com.custom.taskmanager.config;

import com.custom.action.fieldfill.AutoFillColumnHandler;
import com.custom.action.fieldfill.TableFillObject;
import com.custom.comm.date.DateTimeUtils;
import com.custom.comm.enums.FillStrategy;
import com.custom.taskmanager.entity.TaskRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/7/13 0013 13:35
 * @Desc
 */
@Component
public class FieldAutoHandler implements AutoFillColumnHandler {
    @Override
    public List<TableFillObject> fillStrategy() {
        List<TableFillObject> list = new ArrayList<>();

        TableFillObject fillObject = new TableFillObject();
        fillObject.setEntityClass(TaskRecord.class);
        fillObject.setStrategy(FillStrategy.INSERT);
        fillObject.addField("createTime", DateTimeUtils.getThisTime())
                .addField("operatorTime", DateTimeUtils.getThisTime())
                .setNotFoundFieldThrowException(true);

        return list;
    }
}
