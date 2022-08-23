package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbOneToMany;
import com.custom.comm.annotations.DbTable;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author Xiao-Bai
 * @date 2021/12/3 20:08
 * @desc:
 */
@Data
@DbTable(table = "dept")
public class Dept {

    @DbKey
    private int id;

    @DbField
    private String name;

    @DbOneToMany(joinField = "deptId")
    private Set<Employee> employeeList;

}
