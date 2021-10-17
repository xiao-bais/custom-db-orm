package com.custom.redis;

import com.custom.annotations.DbTable;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/17
 * @Description
 */
@DbTable
public class ActiveConn {

    private String key;

    private Object name;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }
}
