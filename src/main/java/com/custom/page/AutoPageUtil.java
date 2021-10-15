package com.custom.page;

import com.custom.dbconfig.SymbolConst;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/10/15
 * @Description
 */
public class AutoPageUtil<T> {

    /**
     * 当前页
     */
    private int pageIndex;
    /**
     * 每页显示量
     */
    private int pageSize;
    /**
     * 总记录数
     */
    private long total;
    /**
     * 当前页的数据
     */
    private List<T> data;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 第一页
     */
    private int firstPage;
    /**
     * 最后一页
     */
    private int lastPage;
    /**
     * 上一页
     */
    private int prePage;
    /**
     * 下一页
     */
    private int nextPage;
    /**
     * 是否是第一页
     */
    private boolean isFirstPage;
    /**
     * 是否是最后一页
     */
    private boolean isLastPage;
    /**
     * 是否有上一页
     */
    private boolean hasPreviousPage;
    /**
     * 是否有下一页
     */
    private boolean hasNextPage;

    public AutoPageUtil() {
        data = new ArrayList<>();
    }

    public AutoPageUtil(List<T> dataRows) {
       this(1, 10, dataRows);
    }

    public AutoPageUtil(int pageIndex, int pageSize, List<T> dataRows) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        int size = dataRows.size();
        this.total = size;
        int subIndex = (pageIndex - SymbolConst.DEFAULT_ONE) * pageSize;
        this.data = dataRows.subList(Math.min(subIndex, size), (int) Math.min(total, pageIndex * pageSize));
        this.pages = size % pageSize > SymbolConst.DEFAULT_ZERO ? size / pageSize + SymbolConst.DEFAULT_ONE : size / pageSize;
        this.firstPage = SymbolConst.DEFAULT_ONE;
        this.lastPage = this.pages;
        this.prePage = Math.max(pageIndex - SymbolConst.DEFAULT_ONE, SymbolConst.DEFAULT_ONE);
        this.nextPage = Math.min(pageIndex + SymbolConst.DEFAULT_ONE, this.pages);
        this.isFirstPage = pageIndex == this.firstPage;
        this.isLastPage = pageIndex == this.pages;
        this.hasPreviousPage = pageIndex - SymbolConst.DEFAULT_ONE > SymbolConst.DEFAULT_ZERO;
        this.hasNextPage = pageIndex + SymbolConst.DEFAULT_ONE < this.pages;

    }






    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    @Override
    public String toString() {
        return "AutoPageUtil{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", data=" + data +
                ", pages=" + pages +
                ", firstPage=" + firstPage +
                ", lastPage=" + lastPage +
                ", prePage=" + prePage +
                ", nextPage=" + nextPage +
                ", isFirstPage=" + isFirstPage +
                ", isLastPage=" + isLastPage +
                ", hasPreviousPage=" + hasPreviousPage +
                ", hasNextPage=" + hasNextPage +
                '}';
    }
}
