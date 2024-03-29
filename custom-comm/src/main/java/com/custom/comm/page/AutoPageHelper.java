package com.custom.comm.page;

import com.custom.comm.utils.Constants;

import java.util.List;

/**
 * 分页辅助对象
 * @author  Xiao-Bai
 * @since 2021/10/15
 */
public class AutoPageHelper<T> extends DbPageRows<T> {

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

    public AutoPageHelper() {
        super();
    }

    public AutoPageHelper(List<T> dataRows) {
       this(1, 10, dataRows);
    }

    public AutoPageHelper(int pageIndex, int pageSize, List<T> dataRows) {
        super(pageIndex, pageSize, dataRows);
        int pages = super.getPages();
        this.firstPage = Constants.DEFAULT_ONE;
        this.lastPage = pages;
        this.prePage = Math.max(pageIndex - Constants.DEFAULT_ONE, Constants.DEFAULT_ONE);
        this.nextPage = Math.min(pageIndex + Constants.DEFAULT_ONE, pages);
        this.isFirstPage = pageIndex == this.firstPage;
        this.isLastPage = pageIndex == pages;
        this.hasPreviousPage = pageIndex - Constants.DEFAULT_ONE > Constants.DEFAULT_ZERO;
        this.hasNextPage = pageIndex + Constants.DEFAULT_ONE < pages;
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
                "pages=" + super.getPages() +
                ", total=" + super.getTotal() +
                ", data=" + super.getData() +
                ", pageIndex=" + super.getPageIndex() +
                ", pageSize=" + super.getPageSize() +
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
