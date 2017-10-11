//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jdd.lkcz.fm.core.sensitive;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.jdd.lkcz.fm.core.sensitive.SensitiveWordHander;
import org.springframework.stereotype.Component;

public class SensitiveWordHanderImpl implements SensitiveWordHander {
    private WordsTransfer transfer;

    public SensitiveWordHanderImpl() {
    }

    public void init(WordsTransfer transfer) {
        this.transfer = transfer;
    }

    public Map<String, String> build() {
        Collection<String> collection = this.transfer.getWords();
        HashMap<String, String> sensitiveWordMap = new HashMap(collection.size());
        Iterator iterator = this.transfer.getWords().iterator();

        while(iterator.hasNext()) {
            String key = (String)iterator.next();
            Map nowMap = sensitiveWordMap;

            for(int i = 0; i < key.length(); ++i) {
                char keyChar = key.charAt(i);
                Object wordMap = ((Map)nowMap).get(keyChar);
                if (wordMap != null) {
                    nowMap = (Map)wordMap;
                } else {
                    Map<String, String> newWorMap = new HashMap();
                    newWorMap.put("isEnd", "0");
                    ((Map)nowMap).put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    ((Map)nowMap).put("isEnd", "1");
                }
            }
        }

        return sensitiveWordMap;
    }

    public boolean isContains(String txt, int matchType) {
        boolean flag = false;

        for(int i = 0; i < txt.length(); ++i) {
            int matchFlag = this.checkSensitiveWord(txt, i, matchType);
            if (matchFlag > 0) {
                flag = true;
            }
        }

        return flag;
    }

    public String replace(String original, int matchType) {
        String resultTxt = original;
        Set<String> set = this.getSensitiveWord(original, matchType);

        String word;
        String replaceString;
        for(Iterator iterator = set.iterator(); iterator.hasNext(); resultTxt = resultTxt.replaceAll(word, replaceString)) {
            word = (String)iterator.next();
            replaceString = this.getReplaceChars("*", word.length());
        }

        return resultTxt;
    }

    private String getReplaceChars(String replaceChar, int length) {
        String resultReplace = replaceChar;

        for(int i = 1; i < length; ++i) {
            resultReplace = resultReplace + replaceChar;
        }

        return resultReplace;
    }

    private Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet();

        for(int i = 0; i < txt.length(); ++i) {
            int length = this.checkSensitiveWord(txt, i, matchType);
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }

    public int checkSensitiveWord(String txt, int beginIndex, int matchType) {
        boolean flag = false;
        int matchFlag = 0;
        Map nowMap = this.transfer.getSensitive();

        for(int i = beginIndex; i < txt.length(); ++i) {
            char word = txt.charAt(i);
            nowMap = (Map)nowMap.get(word);
            if (nowMap == null) {
                break;
            }

            ++matchFlag;
            if ("1".equals(nowMap.get("isEnd"))) {
                flag = true;
                if (1 == matchType) {
                    break;
                }
            }
        }

        if (matchFlag < 2 || !flag) {
            matchFlag = 0;
        }

        return matchFlag;
    }
}
