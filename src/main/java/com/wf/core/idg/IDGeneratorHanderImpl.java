package com.wf.core.idg;


import com.wf.core.idg.api.IdGeneratorApi;

/**
 * id工具类
 *
 * @author Fe 2016年4月15日
 */
public class IDGeneratorHanderImpl implements IDGeneratorHander {

    private IdGeneratorApi idGenerator;

    public IdGeneratorApi getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGeneratorApi idGenerator) {
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

    @Override
    public Number entityCorrectId(Object entity, Exception e) {
        return (Long) idGenerator.entityCorrectId(entity, e);
    }
}
