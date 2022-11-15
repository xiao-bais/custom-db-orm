package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.SelectFunc;
import com.custom.action.interfaces.TableExecutor;
import com.custom.action.sqlparser.*;
import com.custom.comm.utils.CustomUtil;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {


        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();


        TableParseModel<ChildStudent> tableModel = TableInfoCache.getTableModel(ChildStudent.class);
        List<DbFieldParserModel<ChildStudent>> fieldParserModels = tableModel.getDbFieldParseModels();
        for (DbFieldParserModel<ChildStudent> fieldParserModel : fieldParserModels) {
            boolean dbField = fieldParserModel.isDbField();
            String fieldName = fieldParserModel.getFieldName();
            String column = fieldParserModel.getColumn();
            System.out.println("fieldName = " + fieldName + ", column = " + column + ", isDbField = " + dbField);
        }


    }




}
