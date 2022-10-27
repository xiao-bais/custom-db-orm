package com.home.customtest.entity;

import com.custom.comm.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Xiao-Bai
 * @date 2022/3/29 22:52
 * @desc:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@DbJoinTable("left join location lo on lo.id = a.area_id")
public class ChildStudent extends Student {



    @DbIgnore
    private Integer sumAge;
    @DbIgnore
    private Integer ifNullAge;
    @DbIgnore
    private Integer countAge;
    @DbIgnore
    private Integer minAge;
    @DbIgnore
    private Integer maxAge;
    @DbIgnore
    private Integer avgAge;

}
