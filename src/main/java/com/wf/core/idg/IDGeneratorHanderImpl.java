package com.wf.core.idg;


import com.wf.core.idg.postgres.PostgresIDGenerator;

/**
 * id工具类
 * @author Fe 2016年4月15日
 */
public class IDGeneratorHanderImpl implements IDGeneratorHander {
	private PostgresIDGenerator idGenerator;
	
	public PostgresIDGenerator getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(PostgresIDGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Override
	public long nextId(Object entity) throws IDGeneratorException {
		return (Long) idGenerator.nextId(entity);
	}

	@Override
	public int nextIntId(Object entity) throws IDGeneratorException {
		return (Integer) idGenerator.nextId(entity);
	}
}
