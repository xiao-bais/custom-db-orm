package com.custom.taskmanager.config;

import com.custom.action.autofill.CustomFillHandler;
import com.custom.action.autofill.CustomTableFill;
import com.custom.action.autofill.FillObject;
import com.custom.comm.date.DateTimeUtils;
import com.custom.comm.enums.FillStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/2/24 15:16
 */
@Component
public class MyAutoFillHandler implements CustomFillHandler {


    @Override
    public void handle(CustomTableFill fill) {
        fill.addFill(FillObject.instance("createTime", Integer.class, DateTimeUtils.getThisTime(), FillStrategy.INSERT));
        fill.addFill(FillObject.instance("operatorTime", Integer.class, DateTimeUtils.getThisTime(), FillStrategy.INSERT_UPDATE));
    }

    @Override
    public void handleMany(List<CustomTableFill> fillList) {

    }
}
