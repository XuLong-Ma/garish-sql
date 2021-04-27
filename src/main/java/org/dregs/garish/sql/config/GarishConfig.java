package org.dregs.garish.sql.config;

import org.dregs.garish.sql.utils.Logs;
import org.dregs.garish.sql.MapperHub;

import java.util.List;
import java.util.function.Consumer;

public class GarishConfig {

    public static void setErrorLogMethod(Consumer<Exception> consumer){
        Logs.setConsumer(consumer);
    }
    public static void setMapperClasses(List<Class> classes){
        if(null == classes || 0 == classes.size())return;
        for(Class c : classes){
            setMapperClasses(c);
        }
    }
    public static void setMapperClasses(Class...classes){
        if(null == classes || 0 == classes.length)return;
        for(Class c : classes){
            setMapperClasses(c);
        }
    }
    private static void setMapperClasses(Class c){
        if(null == c)return;
        MapperHub.put(c);
    }

}
