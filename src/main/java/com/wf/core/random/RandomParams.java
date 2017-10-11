//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.random;

import java.util.ArrayList;
import java.util.List;

public class RandomParams<T> {
    private List<RandomParams.RandomParam<T>> list = new ArrayList();

    public RandomParams() {
    }

    public static <T> RandomParams<T> createInstance() {
        return new RandomParams();
    }

    List<RandomParams.RandomParam<T>> list() {
        return this.list;
    }

    public RandomParams<T> add(T data, int count) {
        RandomParams.RandomParam<T> param = new RandomParams.RandomParam();
        param.data = data;
        param.count = count;
        this.list.add(param);
        return this;
    }

    static class RandomParam<T> {
        int count;
        T data;

        RandomParam() {
        }
    }
}
