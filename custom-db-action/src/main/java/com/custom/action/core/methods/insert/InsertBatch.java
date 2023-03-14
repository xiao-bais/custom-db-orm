package com.custom.action.core.methods.insert;

import com.custom.action.core.methods.MethodKind;

/**
 * @author Xiao-Bai
 * @since 2023/3/13 13:46
 */
public class InsertBatch extends InsertOne {

    @Override
    public MethodKind getKind() {
        return MethodKind.INSERT_BATCH;
    }
}
