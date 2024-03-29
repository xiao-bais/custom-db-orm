package com.home.customtest.entity;

import com.custom.comm.annotations.*;
import com.custom.comm.enums.KeyStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author  Xiao-Bai
 * @since  2021/11/27 15:37
 * @Desc：
 **/
@DbTable(value = "employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DbJoinTables({
        @DbJoinTable("left join dept dept on dept.id = a.dept_id"),
})
public class Employee {

    @DbKey(strategy = KeyStrategy.UUID)
    private String id;

    @DbField
    private String empName;

    @DbField
    private boolean sex;

    @DbField
    private Integer age;

    @DbField
    private String address;

    @DbField
    private String birthday;

    @DbField
    private Integer deptId;

    @DbField
    private Integer areaId;

    @DbField
    private String explain;

    @DbField
    private int state;

    @DbJoinField(value = "dept.name")
    private String deptName;




}
