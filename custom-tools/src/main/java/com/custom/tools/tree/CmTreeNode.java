package com.custom.tools.tree;

import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Xiao-Bai
 * @since 2023/2/19 13:51
 */
public class CmTreeNode<T> {


    /**
     * 所有元素
     */
    private final List<T> elements;

    /**
     * 最顶级树节点，只有一个
     */
    private Supplier<T> topNode;

    /**
     * 顶级父节点列表
     */
    private Predicate<T> topListFind;

    /**
     * 子节点集的设置
     */
    private ChildrenSet<T> childrenSet;

    /**
     * 子节点集的查找条件
     */
    private ChildrenSeek<T> childrenSeek;


    private CmTreeNode(List<T> elements) {
        this.elements = elements;
    }

    public static <T> CmTreeNode<T> of(List<T> elements) {
        return new CmTreeNode<>(elements);
    }

    /**
     * 可选，若不需要顶级节点，不填即可
     */
    public CmTreeNode<T> top(Supplier<T> topNode) {
        this.topNode = topNode;
        return this;
    }


    public CmTreeNode<T> topListCond(Predicate<T> topListFind) {
        this.topListFind = topListFind;
        return this;
    }

    /**
     * 查找子集的条件
     */
    public CmTreeNode<T> childCond(ChildrenSeek<T> seek) {
        this.childrenSeek = seek;
        return this;
    }

    /**
     * 设置子集时使用
     */
    public CmTreeNode<T> childrenSet(ChildrenSet<T> childrenSet) {
        this.childrenSet = childrenSet;
        return this;
    }


    /**
     * 开始构建，并返回金字塔最顶级的那个对象
     */
    public T buildTop() {

        T topObj = thisTopObj();
        if (check()) return topObj;

        List<T> topList = elements.stream().filter(topListFind).collect(Collectors.toList());
        for (T t : topList) {
            findChildren(t);
        }
        childrenSet.accept(topObj, topList);
        return topObj;
    }


    /**
     * 开始构建，并返回最顶级的父节点列表
     * <br/>与{@link #buildTop()}的不同是，该方法会返回父节点列表，而前者会将父节点数据的列表置于新创建的最顶级对象属性中
     */
    public List<T> buildTrees() {
        if (check()) return new ArrayList<>();
        List<T> topList = elements.stream().filter(topListFind).collect(Collectors.toList());
        for (T t : topList) {
            findChildren(t);
        }
        return topList;
    }



    /**
     * 检查递归需要的条件是否齐全
     */
    private boolean check() {
        if (elements == null || elements.isEmpty()) {
            return true;
        }

        if (topListFind == null) {
            throw new CustomCheckException("topListFind cannot be null");
        }

        if (childrenSeek == null) {
            throw new CustomCheckException("seek cannot be null");
        }

        if (childrenSet == null) {
            throw new CustomCheckException("childrenSet cannot be null");
        }
        return false;
    }


    private T thisTopObj() {
        T topObj = topNode.get();
        AssertUtil.notNull(topObj, "top node cannot be null");
        return topObj;
    }


    /**
     * 向下递归查找子集
     */
    private void findChildren(T t) {
        List<T> childList = elements.stream()
                .filter(e -> this.childrenSeek.seek(t, e))
                .collect(Collectors.toList());

        if (!childList.isEmpty()) {
            for (T child : childList) {
                findChildren(child);
            }
            childrenSet.accept(t, childList);
        }
    }




}
