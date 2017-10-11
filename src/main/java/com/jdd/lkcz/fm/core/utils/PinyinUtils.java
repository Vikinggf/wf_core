package com.jdd.lkcz.fm.core.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PinyinUtils {
    /**
     * 获取汉字串拼音的每字的第一个字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String cn2FirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (_t != null) {
                        pybf.append(_t[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }

    /**
     * 取第一个中文字符得到拼音首字母
     *
     * @param chinese
     * @return
     */
    public static String getFirstLetter(String chinese) {
        if (chinese == null) {
            return "";
        }
        return cn2FirstSpell(chinese.substring(0, 1)).toUpperCase();
    }

    /**
     * 对数据列表进行分组排序
     *
     * @param dataList
     * @return TreeMap
     */
    public static <T extends PinyinByGroup> Map<String, List<T>> groupByLetter(List<T> dataList) {
        Map<String, List<T>> resultMap = new TreeMap<>();
        List<T> list = null;
        for (T e : dataList) {
            list = resultMap.get(e.firstLetter());
            if (list != null) {  // 元素存在
                list.add(e);
            } else {
                list = new ArrayList<>();
                list.add(e);
                resultMap.put(e.firstLetter(), list);
            }
        }
        return resultMap;
    }
}
