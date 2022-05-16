package com.home.customtest.temp;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbJoinTable;
import com.custom.comm.annotations.DbJoinTables;
import com.custom.comm.annotations.DbTable;
import com.home.customtest.entity.Student;
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
@DbJoinTables(
        @DbJoinTable("left join location lo on lo.id = a.area_id")
)
@DbTable(table = "student")
public class ChildStudent extends Student {


    @DbField
    private Boolean sex;

    @DbField
    private String phone;

    private Integer sumAge;
    private Integer ifNullAge;
    private Integer countAge;
    private Integer minAge;
    private Integer maxAge;
    private Integer avgAge;

}
