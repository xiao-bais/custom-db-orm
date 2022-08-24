package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbOneToMany;
import com.custom.comm.annotations.DbTable;
import lombok.Data;

import java.util.*;

/**
 * @author Xiao-Bai
 * @date 2021/12/3 20:08
 * @desc:
 */
@Data
@DbTable(table = "dept")
public class Dept {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    private boolean adminFlag;

    @DbOneToMany(joinField = "deptId", joinTarget = Employee.class)
    private List<Map<String, Object>> employeeList;

}
