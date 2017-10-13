package com.wf.core.persistence;

public class IsolationLevelThreadLocal {
    private final static ThreadLocal<Integer> isolationLevelThreadLocal = new ThreadLocal<>();

    public static void setIsolationLevel(Integer isolationLevel) {
        isolationLevelThreadLocal.set(isolationLevel);
    }

    public static Integer getIsolationLevel() {
        return isolationLevelThreadLocal.get();
    }

    public static void cleanIsolationLevel() {
        isolationLevelThreadLocal.remove();
    }
}
