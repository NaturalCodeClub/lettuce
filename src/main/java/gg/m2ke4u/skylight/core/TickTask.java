package gg.m2ke4u.skylight.core;

public interface TickTask<T>{

    void call(T input);

    boolean finished();

    boolean terminate();

    void forceTerminate();

    void awaitFinish(long nanosTimeOut);

    void awaitFinish();
}
