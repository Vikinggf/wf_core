/**
 * http://www.lbanma.com
 */
package com.wf.core.service;

import com.wf.core.idg.IDGeneratorHander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service基类
 * @author www.lbanma.com
 * @version 2014-05-16
 */
public abstract class BaseService {
	
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	protected IDGeneratorHander idGeneratorHander;
	
	/**
	 * 获取下一个id。需要使用 {@link #createTableName(String)}来创建tableName
	 * @deprecated 请使用 {@link CrudService#nextId()}
	 * @return
	 */
	public long createNextId(String tableName) {
		return idGeneratorHander.nextId(tableName);
	}
	
	/**
	 * 生成用于创建id的表名
	 * @param tableName
	 * @return
	 */
	public String createTableName(String tableName) {
		return "llm_" + tableName;
	}
}
