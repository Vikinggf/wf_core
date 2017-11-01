package com.wf.core.persistence;

public class IsolationLevelThreadLocal {
    private final static ThreadLocal<Integer> ISOLATION_LEVEL_THREADLOCAL = new ThreadLocal<>();

    public static void setIsolationLevel(Integer isolationLevel) {
        ISOLATION_LEVEL_THREADLOCAL.set(isolationLevel);
    }

    public static Integer getIsolationLevel() {
        return ISOLATION_LEVEL_THREADLOCAL.get();
    }

    public static void cleanIsolationLevel() {
        ISOLATION_LEVEL_THREADLOCAL.remove();
    }
}
