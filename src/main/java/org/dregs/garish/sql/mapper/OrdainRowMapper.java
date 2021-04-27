package org.dregs.garish.sql.mapper;

import org.dregs.garish.sql.MapperHub;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrdainRowMapper<T> implements RowMapper<T> {

    private Class<T> tClass;

    private Map<String,Class> map;

    public OrdainRowMapper(Class<T> tClass) {
        this.tClass = tClass;
        this.map = new HashMap<String,Class>();
        for(Field field : tClass.getDeclaredFields()){
            Class<?> type = field.getType();
            if(type == Date.class)
                type = Timestamp.class;
            this.map.put(field.getName(),type);
        }
    }

    @Override
    public T mapRow(ResultSet resultSet, int i) throws SQLException {
        T t = BeanUtils.instantiateClass(tClass);
        ResultSetMetaData metaData = resultSet.getMetaData();
        for(int $i=1;$i<=metaData.getColumnCount();$i++){
            String columnName = JdbcUtils.lookupColumnName(metaData, $i);
            String name = MapperHub.getField(tClass,columnName);
            Assert.notNull(name,tClass.getSimpleName()+" no mapping");
            Object value = JdbcUtils.getResultSetValue(resultSet, $i, map.get(name));
            Method method = findMethod(setMethodNameChange(name));
            Assert.notNull(method,name+" set method is null");
            ReflectionUtils.invokeMethod(method,t,value);
        }
        return t;
    }

    public Method findMethod(String name){
        return MapperHub.getMethod(tClass,name);
    }
    public String methodNameChange(String name){
        String begin = name.substring(0, 1).toUpperCase();
        String end = name.substring(1);
        return begin+end;
    }

    private static final String GET = "get";
    private static final String SET = "set";

    public String getMethodNameChange(String name){
        String begin = name.substring(0, 1).toUpperCase();
        String end = name.substring(1);
        return new StringBuilder(GET).append(begin+end).toString();
    }
    public String setMethodNameChange(String name){
        String begin = name.substring(0, 1).toUpperCase();
        String end = name.substring(1);
        return new StringBuilder(SET).append(begin+end).toString();
    }
}
