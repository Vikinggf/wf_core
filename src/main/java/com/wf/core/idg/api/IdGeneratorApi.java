package com.wf.core.idg.api;


import com.wf.core.idg.IDGeneratorException;

public interface IdGeneratorApi<T> {
    /**
     * 获取下一个ID
     * @param entity
     * @return
     * @throws IDGeneratorException
     */
    public T nextId(Object entity) throws IDGeneratorException;

    public Number entityCorrectId(Object entity, Exception e);

}
