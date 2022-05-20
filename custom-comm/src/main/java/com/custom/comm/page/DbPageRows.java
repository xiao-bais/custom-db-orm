package com.custom.comm.page;

import com.custom.comm.SymbolConstant;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/9 0009 15:09
 * @Version 1.0
 * @Description DbPageUtils
 */
public class DbPageRows<T> {

    /**
     * 当前页
     */
    private int pageIndex = 1;
    /**
     * 每页显示量
     */
    private int pageSize = 10;
    /**
     * 总记录数
     */
    private long total;
    /**
     * 当前页的数据
     */
    private List<T> data;

    public DbPageRows(int pageIndex, int pageSize, long total) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.total = total;
    }

    public DbPageRows(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public DbPageRows(){

    }

    public DbPageRows(int pageIndex, int pageSize, List<T> list){
        this.data = list;
        this.total = list.size();
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        int subIndex = (pageIndex - SymbolConstant.DEFAULT_ONE) * pageSize;
        this.data = list.subList(Math.min(subIndex, list.size()), (int) Math.min(total, pageIndex * pageSize));
    }

    public int getPageIndex() {
        return Math.max(pageIndex, 0);
    }

    public DbPageRows<T> setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public DbPageRows<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public DbPageRows<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public List<T> getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public DbPageRows<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "DbPageRows{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}
