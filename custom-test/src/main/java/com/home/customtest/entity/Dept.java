package com.home.customtest.entity;

import com.custom.comm.annotations.DbField;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbTable;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author  Xiao-Bai
 * @since  2021/12/3 20:08
 * @desc:
 */
@Data
@DbTable(value = "dept")
public class Dept {

    @DbKey
    private Integer id;

    @DbField
    private String name;

    private boolean adminFlag;

    private List<Map<String, Object>> employeeList;

}
