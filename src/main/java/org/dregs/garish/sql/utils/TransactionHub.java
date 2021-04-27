package org.dregs.garish.sql.utils;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionHub {

    private DataSourceTransactionManager dataSourceTransactionManager;

    public TransactionHub(DataSourceTransactionManager dataSourceTransactionManager) {
        this.dataSourceTransactionManager = dataSourceTransactionManager;
    }

    public TransactionStatus beginTransaction() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(0);
        return dataSourceTransactionManager.getTransaction(def);
    }

    public void commit(TransactionStatus status) {
        if(null != status) dataSourceTransactionManager.commit(status);
    }

    public void rollback(TransactionStatus status) {
        if(null != status) dataSourceTransactionManager.rollback(status);
    }
    public void commitOrRollBack(TransactionStatus status, boolean flag) {
        if(flag)
            commit(status);
        else
            rollback(status);
    }

    public boolean batchResult(int[] ints,int size){
        if(null == ints)
            return false;
        if(size != ints.length)
            return false;
        for(int $i=0;$i<ints.length;$i++){
            if(0 == ints[$i])
                return false;
        }
        return true;
    }


}
