package org.dregs.garish.sql.dao.impl;

import org.dregs.garish.sql.data.Tuple2;
import org.dregs.garish.sql.data.Tuple3;
import org.dregs.garish.sql.Factor;
import org.dregs.garish.sql.Quote;
import org.dregs.garish.sql.action.*;
import org.dregs.garish.sql.mapper.OrdainRowMapper;
import org.dregs.garish.sql.utils.TransactionFunction;
import org.dregs.garish.sql.dao.trait.IndexDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexDaoImpl implements IndexDao {

    private final static Map<Class, RowMapper> MAPPER_CACHE = new HashMap<>();

    private final static Map<String, Method> KEY_METHOD_CACHE = new HashMap<>();

    private final static Map<String, Method> VALUE_METHOD_CACHE = new HashMap<>();


    private JdbcTemplate jdbcTemplate;

    private TransactionFunction transactionFunction;

    public IndexDaoImpl(JdbcTemplate jdbcTemplate, TransactionFunction transactionFunction) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionFunction = transactionFunction;
    }

    @Override
    public <T> List<T> findList(Factor<T> factor) {
        Class<T> tClass = factor.getTClass();
        RowMapper<T> rowMapper = MAPPER_CACHE.<T>get(tClass);
        if(null == rowMapper){
            rowMapper = new OrdainRowMapper<T>(tClass);
            MAPPER_CACHE.put(tClass,rowMapper);
        }
        return jdbcTemplate.query(
                factor.getSql(),
                factor.getObjects(),
                rowMapper
        );
    }

    @Override
    public <T> T findOne(Factor<T> factor) {
        return onlyOne(findList(factor));
    }

    @Override
    public <T> List<T> findList(Query<T> query) {
        Factor<T> factor = query.builder();
        return findList(factor);
    }

    @Override
    public <T> T findOne(Query<T> query) {
        return onlyOne(findList(query));
    }

    @Override
    public <T> T index(Class<T> c, Quote<Object, T> q, Object o) {
        Query<T> query = Query
                .createQuery(c)
                .eqIndex(q, o);
        return findOne(query);
    }

    @Override
    public <T> List<T> indexList(Class<T> c, Quote<Object, T> q, Object o) {
        Query<T> query = Query
                .createQuery(c)
                .eqIndex(q, o);
        return findList(query);
    }

    @Override
    public int count(Query query) {
        Factor factor = query.count();
        return jdbcTemplate.queryForObject(factor.getSql(),factor.getObjects(),Integer.class);
    }

    @Override
    public int insert(Object o) {
        try{
            if(null == o)
                throw new RuntimeException("insert objects cannot be null");
            Method insertSQL = findKeyMethod(o, "$insertSQL");
            Method insertObjects = findValueMethod(o, "$objects");
            if(null == insertSQL)
                throw new RuntimeException("not found `$insertSQL` method");
            if(null == insertObjects)
                throw new RuntimeException("not found `$objects` method");
            String sql = (String) insertSQL.invoke(o);
            Object[] objects = (Object[]) insertObjects.invoke(o);
            return jdbcTemplate.update(
                    sql,objects
            );
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(Delete delete) {
        Factor factor = delete.builder();
        return jdbcTemplate.update(
                factor.getSql(),
                factor.getObjects()
        );
    }

    @Override
    public int update(Update update) {
        Tuple2<String, Object[]> tuple2 = update.fusionSO();
        return jdbcTemplate.update(tuple2._1(),tuple2._2());
    }

    @Override
    public boolean update(Update... updates) {
        List<Tuple3<String,Object[], Update.Decide>> fusionList = new ArrayList<>();
        if(1 == updates.length){
            Update update = updates[0];
            int result = update(update);
            Update.Decide decide = update.getDecide();
            return decide.use(result);
        }
        for(Update update : updates)fusionList.add(update.fusionSOD());
        return transactionFunction.run(this::fusionUpdate,fusionList);
    }

    @Override
    public boolean update(List<Update> updates) {
        List<Tuple3<String,Object[], Update.Decide>> fusionList = new ArrayList<>();
        if(1 == updates.size()){
            Update update = updates.get(0);
            int result = update(update);
            Update.Decide decide = update.getDecide();
            return decide.use(result);
        }
        for(Update update : updates)fusionList.add(update.fusionSOD());
        return transactionFunction.run(this::fusionUpdate,fusionList);
    }

    @Override
    public boolean save(Save... saves) {
        return false;
    }


    private boolean fusionUpdate(List<Tuple3<String,Object[], Update.Decide>> fusionList){
        for(Tuple3<String,Object[], Update.Decide> tuple : fusionList){
            if(!tuple._3().use(jdbcTemplate.update(tuple._1(),tuple._2())))return false;
        }
        return true;
    }


    public Method findKeyMethod(Object o,String name){
        if(null == o)return null;
        Class c = o.getClass();
        String key = c.getName()+"-"+name;
        Method result = KEY_METHOD_CACHE.get(key);
        if(null != result)return result;
        for(Method method : c.getDeclaredMethods()){
            if(name.equals(method.getName())){
                result = method;break;
            }
        }
        if(null != result){
            KEY_METHOD_CACHE.put(key,result);
        }
        return result;
    }
    public Method findValueMethod(Object o,String name){
        if(null == o)return null;
        Class c = o.getClass();
        String key = c.getName()+"-"+name;
        Method result = VALUE_METHOD_CACHE.get(key);
        if(null != result)return result;
        for(Method method : c.getDeclaredMethods()){
            if(name.equals(method.getName())){
                result = method;break;
            }
        }
        if(null != result){
            VALUE_METHOD_CACHE.put(key,result);
        }
        return result;
    }

}
