package com.custom.action.core.methods.insert;

import com.custom.action.core.methods.MethodKind;
import com.custom.comm.utils.AssertUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Xiao-Bai
 * @since 2023/3/13 13:46
 */
@SuppressWarnings("unchecked")
public class InsertBatch extends InsertOne {

    @Override
    public MethodKind getKind() {
        return MethodKind.INSERT_BATCH;
    }

    @Override
    public <T> Class<T> getMappedType(Object[] params) {
        AssertUtil.notEmpty(params, "insert data cannot be empty");
        AssertUtil.notEmpty(params[0], "insert data cannot be empty");
        List<T> list = new ArrayList<>((Collection<T>) params[0]);
        return (Class<T>) list.get(0).getClass();
    }
}
