package com.wf.core.service;


import com.wf.core.dao.AbstractCommonDao;
import com.wf.core.persistence.Page;

import java.util.List;
import java.util.Map;

/**
 * 通用service
 * @author Fe 2016年9月24日
 */
public abstract class AbstractCommonService {
	
	protected abstract AbstractCommonDao getCommonDao();
	
	/**
	 * 分页查询
	 * @param listStr
	 * @param countStr
	 * @param params
	 * @return
	 */
	public Object findForPage(String listStr, String countStr, Object params) {
		Page<Object> page = new Page<>(params, 0, 0);
		page.setCount((Long) getCommonDao().findForObject(countStr, params));
		page.setData(getCommonDao().findForList(listStr, params));
		return page;
	}
	
	/**
	 * 查找对象
	 * @param str
	 * @param obj
	 * @return
	 */
	public <E> E findForObject(String str, Object obj) {
		return getCommonDao().findForObject(str, obj);
	}

	/**
	 * 查找对象
	 * @param str
	 * @param obj
	 * @return
	 */
	public <E> List<E> findForList(String str, Object obj) {
		return getCommonDao().findForList(str, obj);
	}
	
	/**
	 * 查找对象封装成Map
	 * @param str
	 * @param obj
	 * @return
	 */
	public <K, V> Map<K, V> findForMap(String str, Object obj, String key) {
		return getCommonDao().findForMap(str, obj, key);
	}
	
	/**
	 * 删除
	 * @param str
	 * @param obj
	 * @return
	 */
	public int delete(String str, Object obj) {
		return getCommonDao().delete(str, obj);
	}
	
	/**
	 * 更新
	 * @param str
	 * @param obj
	 * @return
	 */
	public int update(String str, Object obj) {
		return getCommonDao().update(str, obj);
	}

}
