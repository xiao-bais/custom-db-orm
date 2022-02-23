package com.home.customtest.entity;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import lombok.Data;

/**
 * @author Xiao-Bai
 * @date 2021/12/3 20:08
 * @desc:
 */
@Data
@DbTable(table = "dept1")
public class Dept {

    @DbKey
    private int id;

    @DbField
    private String name;

}
