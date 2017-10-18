package com.wf.core.idg;


/**
 * id工具类
 * @author Fe 2016年4月15日
 */
public interface IDGeneratorHander {

	/**
	 * 获取下一个ID
	 * @param entity
	 * @return
	 * @throws IDGeneratorException
	 */
	public long nextId(Object entity) throws IDGeneratorException;

	/**
	 * 获取下一个 int类型的ID
	 * @param entity
	 * @return
	 * @throws IDGeneratorException
	 */
	public int nextIntId(Object entity) throws IDGeneratorException;

	/**
	 * 新增时异常，修正id
	 * @param entity
	 * @param e
	 * @return
	 */
	public Number entityCorrectId(Object entity, Exception e);
}
