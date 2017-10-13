package com.wf.core.sql.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * map工具
 * @author Fe 2016年9月24日
 */
public class MapUtils {

	/**
	 * 转换为map
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> toMap(Object...objects) {
		if (objects.length % 2 != 0)
			throw new IllegalArgumentException("传入参数个数错误");
		Map<Object, Object> map = new HashMap<>();
		Object key = null;
		for (int i = 0; i < objects.length; i++) {
			if (i % 2 == 0)
				key = objects[i];
			else
				map.put(key, objects[i]);
		}
		return (Map<K, V>) map;
	}
}
