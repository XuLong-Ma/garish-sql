package org.dregs.garish.sql.action;

import org.dregs.garish.sql.utils.Logs;
import org.dregs.garish.sql.MapperHub;
import org.dregs.garish.sql.Quote;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Action {

    private static Map<String,String> lambdaToFieldCache = new HashMap<>();


    protected String proxyMethodName(Serializable serializable){
        Class<? extends Serializable> tClass = serializable.getClass();
        String name = tClass.getName();
        String result = lambdaToFieldCache.get(name);
        if(null != result) return result;
        try {
            Method writeReplace = tClass.getDeclaredMethod("writeReplace");
            if(!writeReplace.isAccessible())writeReplace.setAccessible(true);
            Object invoke1 = writeReplace.invoke(serializable);
            SerializedLambda invoke = (SerializedLambda)invoke1;
            String implMethodName = invoke.getImplMethodName();
            String re = implMethodName.replaceFirst("get", "").replaceFirst("set", "");
            String result_ = re.substring(0,1).toLowerCase()+re.substring(1);
            lambdaToFieldCache.put(name,result_);
            return result_;
        } catch (Exception e) {
            Logs.error(e);
            return null;
        }
    }


    public String fieldName(Quote s){
        if(null == s) return null;
        return proxyMethodName(s);
    }
    public <T> String findCell(String field,Class<T> tClass){
        if(null == field)return null;
        return MapperHub.getCell(tClass,field);
    }
    public <T> String findCell(String field,Class<T> tClass,String defaultValue){
        String cell = MapperHub.getCell(tClass, field);
        return (null == cell) ? defaultValue : cell ;
    }

}
