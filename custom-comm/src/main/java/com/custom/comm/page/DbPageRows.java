package com.custom.comm.page;

import com.custom.comm.utils.Constants;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页对象
 * @author  Xiao-Bai
 * @since 2021/1/9 0009 15:09
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
     * 总页数
     */
    private int pages;
    /**
     * 当前页的数据
     */
    private List<T> data;

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
        this.pages = (int) (this.total / this.pageSize == 0 ? this.total / this.pageSize : this.total / this.pageSize + 1);
        int subIndex = (pageIndex - Constants.DEFAULT_ONE) * pageSize;
        this.data = list.subList(Math.min(subIndex, list.size()), (int) Math.min(total, (long) pageIndex * pageSize));
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
        this.pages = (int) (this.total / this.pageSize == 0 ? this.total / this.pageSize : this.total / this.pageSize + 1);
        return this;
    }

    public int getPages() {
        return pages;
    }

    public List<T> getData() {
        return data;
    }

    public DbPageRows<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <R> DbPageRows<R> convert(Function<? super T, ? extends R> mapping) {
        List<R> collect = data.stream().map(mapping).collect(Collectors.toList());
        return ((DbPageRows<R>) this).setData(collect);
    }

    @Override
    public String toString() {
        return "DbPageRows{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", pages=" + pages +
                ", data=" + data +
                '}';
    }
}
