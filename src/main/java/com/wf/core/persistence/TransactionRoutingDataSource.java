package com.wf.core.persistence;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author Fe 2017年1月19日
 */
public class TransactionRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        Integer isolationLevel = IsolationLevelThreadLocal.getIsolationLevel();
        if (isolationLevel == null)
            return null;
        return isolationLevel.toString();
    }

}
