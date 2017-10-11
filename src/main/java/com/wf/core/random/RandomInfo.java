//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.random;

import java.util.ArrayList;
import java.util.List;

public class RandomInfo<T> {
    private List<T> values = new ArrayList();

    public RandomInfo() {
    }

    public int count() {
        return this.values.size();
    }

    void add(T data, int count) {
        List<T> list = new ArrayList(count);

        for(int i = 0; i < count; ++i) {
            list.add(data);
        }

        this.values.addAll(list);
    }

    T getValue(int index) {
        return this.values.get(index);
    }
}
