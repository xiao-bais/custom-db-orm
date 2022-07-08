package com.home.customtest.entity;

import com.custom.comm.annotations.*;
import com.custom.comm.enums.KeyStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/27 15:37
 * @Desc：
 **/
@DbTable(table = "employee", enabledDefaultValue = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@DbJoinTables({
        @DbJoinTable("left join dept dept on dept.id = a.dept_id"),
})
public class Employee {

    @DbKey(strategy = KeyStrategy.UUID)
    private String id;

    @DbField(defaultValue = "员工")
    private String empName;

    @DbField
    private boolean sex;

    @DbField(defaultValue = "20")
    private Integer age;

    @DbField
    private String address;

    @DbField
    private Date birthday;

    @DbField
    private Integer deptId;

    @DbField
    private Integer areaId;

    @DbField(defaultValue = "他很懒，什么都没写")
    private String explain;

    @DbField
    private int state;

    @DbMapper(value = "dept.name")
    private String deptName;




}
