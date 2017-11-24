/**
 * http://www.lbanma.com
 */
package com.wf.core.service;

import com.wf.core.cache.CacheHander;
import com.wf.core.persistence.CrudDao;
import com.wf.core.persistence.DataEntity;
import com.wf.core.persistence.MyBatisDao;
import com.wf.core.persistence.Page;
import com.wf.core.utils.exception.BusinessCommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Service基类
 * @author www.lbanma.com
 * @version 2014-05-16
 */
public abstract class CrudService<D extends CrudDao<T>, T extends DataEntity> extends BaseService implements InitializingBean {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private String tableName;
	@Autowired
	protected D dao;
	@Autowired
	protected CacheHander cacheHander;
	
	/**
	 * 清除缓存，由各实现服务自己实现
	 */
	protected void clearCache(T entity) {}
	
	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(Long id) {
		return dao.get(id);
	}
	
	public List<T> findList(T entity) {
		return dao.findList(new Page<T>(entity));
	}
	
	public List<T> findList(T entity, int maxResult) {
		return dao.findList(new Page<T>(entity, 0, maxResult));
	}
	
	public List<T> findList(Page<T> page) {
		return dao.findList(page);
	}
	
	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @return
	 */
	public Page<T> findPage(T entity) {
		return findPage(new Page<>(entity));
	}
	/**
	 * 查询分页数据
	 * @param page 分页对象
	 * @return
	 */
	public Page<T> findPage(Page<T> page) {
		long count = dao.count(page);
		page.setCount(count);
		if (count == 0) 
			page.setData(new ArrayList<T>());
		else 
			page.setData(dao.findList(page));
		return page;
	}

	/**
	 * 保存数据（插入或更新）
	 * @param entity
	 */
	public void save(T entity) {
		if (entity.isNewRecord()) {
			this.preInsert(entity);
			dao.insert(entity);
		}else{
			this.preUpdate(entity);
			dao.update(entity);
		}
		clearCache(entity);
	}
	
	/**
	 * 更新前处理
	 * @param entity
	 */
	protected void preUpdate(T entity) {}
	
	/**
	 * 获取下一个id
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long nextId() {
		return createNextId(tableName);
	}
	/**
	 * 指定表，获取下一个id
	 * @param tableName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long nextId(String tableName){
		return createNextId(createTableName(tableName));
	}
	
	/**
	 * 插入前处理
	 * @param entity
	 */
	protected void preInsert(T entity) {
		entity.setId(nextId());
		if (entity.getCreateTime() == null)
			entity.setCreateTime(new Date());
	}

	/**
	 * 删除数据
	 * @param entity
	 */
	public void delete(T entity) {
		if (entity != null && entity.getId() != null) {
			dao.delete(entity.getId());
			clearCache(entity);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//注解处理
		Transactional transactional = getClass().getAnnotation(Transactional.class);
		if (transactional != null)
			throw new BusinessCommonException(getClass().getName() + "在类名上声明了事务注解，但是框架不支持。");
		
		//tableName处理
		if (dao == null)
			return;
		Class<?>[] ints = dao.getClass().getInterfaces();
		MyBatisDao ann = null;
		for (Class<?> c : ints) {
			ann = c.getAnnotation(MyBatisDao.class);
			if (ann != null)
				break;
		}
		if (ann == null)
			throw new NullPointerException(dao.getClass().getName() + "未声明@MyBatisDao注解");
		tableName = createTableName(ann.tableName());
	}
	
	public void batchSave(List<T> entitys) {
		for (int i = 0; i < entitys.size();)
			doBatchSave(entitys, i, (i += 500) >= entitys.size() ? entitys.size() : i);
	}
	
	protected void doBatchSave(List<T> entitys, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++)
			save(entitys.get(i));
	}
	
	protected List<T> toList(List<T> entitys, int startIndex, int endIndex) {
		List<T> newList = new ArrayList<>(endIndex - startIndex);
		for (int i = startIndex; i < endIndex; i++) {
			T entity = entitys.get(i);
			if (entity.isNewRecord())
				this.preInsert(entity);
			else
				this.preUpdate(entity);
			newList.add(entity);
		}
		return newList;
	}
}
