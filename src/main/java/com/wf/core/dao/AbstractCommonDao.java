package com.wf.core.dao;

import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;
import java.util.Map;

/**
 * DAO支持类实现
 * @author Ares
 * @version 2016-01-15
 * @param <T>
 */
public class AbstractCommonDao implements CommonDao {
	private SqlSessionTemplate sqlSessionTemplate;

	@Override
	public <E> E findForObject(String str, Object obj) {
		return sqlSessionTemplate.selectOne(str, obj);
	}

	@Override
	public <E> List<E> findForList(String str, Object obj) {
		return sqlSessionTemplate.selectList(str, obj);
	}

	@Override
	public <K, V> Map<K, V> findForMap(String str, Object obj, String key) {
		return sqlSessionTemplate.selectMap(str, obj, key);
	}

	@Override
	public int delete(String statement, Object parameter) {
		return sqlSessionTemplate.delete(statement, parameter);
	}

	@Override
	public int update(String statement, Object parameter) {
		return sqlSessionTemplate.update(statement, parameter);
	}

	public SqlSessionTemplate getSqlSessionTemplate() {
		return sqlSessionTemplate;
	}

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
}