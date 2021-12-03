package com.home.customtest.entity;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import lombok.Data;

/**
 * @author Xiao-Bai
 * @date 2021/12/3 20:12
 * @desc:
 */
@Data
@DbTable(table = "location")
public class Location {

    @DbKey
    private int id;

    @DbField
    private String code;

    @DbField
    private String area;

    @DbField("parent_id")
    private int parentId;

}
