package org.dregs.garish.sql.config;

import org.dregs.garish.sql.MapperHub;
import org.dregs.garish.sql.utils.TransactionFunction;
import org.dregs.garish.sql.utils.TransactionHub;
import org.dregs.garish.sql.dao.impl.IndexDaoImpl;
import org.dregs.garish.sql.dao.trait.IndexDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class InjectBeans {

    @Bean("garish-sql::MapperHub")
    public MapperHub setMapperHub(){
        return new MapperHub();
    }
    @Bean("garish-sql::TransactionHub")
    public TransactionHub setTransactionHub(DataSourceTransactionManager dataSourceTransactionManager){
        return new TransactionHub(dataSourceTransactionManager);
    }
    @Bean("garish-sql::TransactionFunction")
    public TransactionFunction setTransactionFunction(TransactionHub transactionHub){
        return new TransactionFunction(transactionHub);
    }
    @Bean("garish-sql::IndexDao")
    public IndexDao setIndexDao(
            JdbcTemplate jdbcTemplate,
            @Qualifier("garish-sql::TransactionFunction") TransactionFunction transactionFunction){
        return new IndexDaoImpl(jdbcTemplate,transactionFunction);
    }

}
