package com.custom.tools.objects;

import com.custom.tools.function.BuildParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author  Xiao-Bai
 * @since  2023/1/28 10:53
 */
public class ObjBuilder<T> {


    private final Supplier<T> thisPrepare;
    private final List<Consumer<T>> setConsumers = new ArrayList<>();
    public ObjBuilder(Supplier<T> thisPrepare) {
        this.thisPrepare = thisPrepare;

    }

    public static <T> ObjBuilder<T> of(Supplier<T> thisPrepare) {
        return new ObjBuilder<>(thisPrepare);
    }

    public <P> ObjBuilder<T> with(BuildParam<T, P> param, P val) {
      return with(true, param, val);
    }

    public <P> ObjBuilder<T> with(boolean condition, BuildParam<T, P> param, P val) {
        if (condition) {
            Consumer<T> consumer =  x -> param.accept(x, val);
            setConsumers.add(consumer);
        }
        return this;
    }

    public T build() {
        T targetObj = thisPrepare.get();
        setConsumers.forEach(val -> val.accept(targetObj));
        setConsumers.clear();
        return targetObj;
    }


}
