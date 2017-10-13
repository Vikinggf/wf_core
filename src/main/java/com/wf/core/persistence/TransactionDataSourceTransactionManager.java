package com.wf.core.persistence;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

@SuppressWarnings("serial")
public class TransactionDataSourceTransactionManager extends DataSourceTransactionManager {

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        if (definition != null)
            IsolationLevelThreadLocal.setIsolationLevel(definition.getIsolationLevel());
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        IsolationLevelThreadLocal.cleanIsolationLevel();
    }
}
