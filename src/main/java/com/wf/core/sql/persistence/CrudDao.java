/**
 * http://www.lbanma.com
 */
package com.wf.core.sql.persistence;

import java.util.List;

/**
 * DAO支持类实现
 * @author www.lbanma.com
 * @version 2014-05-16
 * @param <T>
 */
public interface CrudDao<T> extends BaseDao {

	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(Long id);
	
	/**
	 * 查询数据列表
	 * @param page
	 * @return
	 */
	public List<T> findList(Page<T> page);
	
	/**
	 * 查询数据条数
	 * @param entity
	 * @return
	 */
	public long count(Page<T> page);
	
	/**
	 * 插入数据
	 * @param entity
	 * @return
	 */
	public int insert(T entity);
	
	/**
	 * 更新数据
	 * @param entity
	 * @return
	 */
	public int update(T entity);
	
	/**
	 * 删除数据（一般为逻辑删除，更新del_flag字段为1）
	 * @param id
	 * @see public int delete(T entity)
	 * @return
	 */
	public int delete(Long id);
}