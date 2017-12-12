package com.github.lukaszbudnik.orderpreserver;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class OrderPreserver {

    private OrderPreserver() {
    }

    public static <T, I> OrderSnapshot<T, I> createOrderSnapshot(List<T> list, Function<T, I> mapFunction) {
        return new OrderSnapshot<T, I>(list.stream().map(mapFunction).collect(Collectors.toList()), mapFunction);
    }

    public static <T, I> List<T> restoreOrder(List<T> list, OrderSnapshot<T, I> orderSnapshot) {
        return list.stream().sorted(createComparator(orderSnapshot)).collect(Collectors.toList());
    }

    public static <T, I> Comparator<T> createComparator(OrderSnapshot<T, I> orderSnapshot) {
        Function<T, I> mapFunction = orderSnapshot.getMapFunction();
        List<I> snapshot = orderSnapshot.getSnapshot();
        return (o1, o2) -> {
            I if1 = mapFunction.apply(o1);
            I if2 = mapFunction.apply(o2);

            int i1 = snapshot.indexOf(if1);
            int i2 = snapshot.indexOf(if2);

            if (i1 < i2) {
                return -1;
            } else if (i1 > i2) {
                return 1;
            } else {
                return 0;
            }
        };
    }

}
