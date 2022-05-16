package com.home.customtest.temp;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Data;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/10 9:55
 * @Desc：
 **/
@Data
@DbTable(table = "city")
public class City {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    @DbField("province_id")
    private Integer provinceId;
}
