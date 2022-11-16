package com.home.customtest.entity;

import com.custom.comm.annotations.*;
import lombok.*;

/**
 * @author Xiao-Bai
 * @date 2022/3/29 22:52
 * @desc:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@DbJoinTable("left join location lo on lo.id = a.area_id")
@DbTable(table = "student", mergeSuperJoin = false)
public class ChildStudent extends Student {



    @DbNotField
    private Integer sumAge;
    @DbNotField
    private Integer ifNullAge;
    @DbNotField
    private Integer countAge;
    @DbNotField
    private Integer minAge;
    @DbNotField
    private Integer maxAge;
    @DbNotField
    private Integer avgAge;

    public ChildStudent(){}

    public ChildStudent(int age) {
        this.avgAge = age;
    }


}
