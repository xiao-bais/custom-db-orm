package com.custom.test;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/20
 * @Description
 */
@DbTable
public class Classes {

    @DbKey
    private int clsId;

    @DbField
    private String clsName;

    public int getClsId() {
        return clsId;
    }

    public void setClsId(int clsId) {
        this.clsId = clsId;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }
}
