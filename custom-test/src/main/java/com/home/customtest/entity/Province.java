package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Data;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 9:53
 * @Desc：
 **/
@Data
@DbTable(table = "province")
public class Province {

    @DbKey
    private Integer id;

    @DbField
    private String name;


}
