package org.the3deer.util.function;

@FunctionalInterface
public interface QuadFunction<A1,A2,A3,A4, R> {

    R apply(A1 arg1, A2 arg2, A3 arg3, A4 arg4);
}
