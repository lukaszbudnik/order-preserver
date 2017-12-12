package com.github.lukaszbudnik.orderpreserver;

import java.util.List;
import java.util.function.Function;

public final class OrderSnapshot<T, I> {
    private final Function<T, I> mapFunction;
    private final List<I> snapshot;

    public OrderSnapshot(List<I> snapshot, Function<T, I> mapFunction) {
        this.mapFunction = mapFunction;
        this.snapshot = snapshot;
    }

    public Function<T, I> getMapFunction() {
        return mapFunction;
    }

    public List<I> getSnapshot() {
        return snapshot;
    }
}
