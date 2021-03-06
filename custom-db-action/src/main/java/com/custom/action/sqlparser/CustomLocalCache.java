package com.custom.action.sqlparser;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 13:59
 * @Desc：自定义本地缓存
 **/
public class CustomLocalCache extends LinkedHashMap<String, Object> {

    // 读写锁
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();


    @Override
    public Object get(Object key) {
        readLock.lock();
        try {
            return super.get(key);
        }finally {
            readLock.unlock();
        }
    }

    @Override
    public Object put(String key, Object value) {
        writeLock.lock();
        try {
            return super.put(key, value);
        }finally {
            writeLock.unlock();
        }
    }

    public CustomLocalCache(int size) {
        super(size + 1, 1.0F, true);
    }

    public CustomLocalCache() {
        super(10, 1.0F, true);
    }


}
