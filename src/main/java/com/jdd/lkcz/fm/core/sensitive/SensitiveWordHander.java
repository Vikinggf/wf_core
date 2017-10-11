//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jdd.lkcz.fm.core.sensitive;

import java.util.Map;

public interface SensitiveWordHander {
    int MIN_MATCH_TYPE = 1;
    int MAX_MATCH_TYPE = 2;
    String REPLACE_TARGET = "*";

    Map<String, String> build();

    String replace(String var1, int var2);

    boolean isContains(String var1, int var2);

    void init(WordsTransfer var1);
}
