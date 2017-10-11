package com.jdd.lkcz.fm.core.cache;

/**
 * cache的类型
 * @author Fe 2016年3月3日
 */
public enum CacheType {
    CONTROLLER("c"), SERVICE("s"), DAO("d"), FRAMEWORK("f");
    private final String name;
    private CacheType(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
}
