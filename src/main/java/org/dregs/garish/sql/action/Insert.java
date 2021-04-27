package org.dregs.garish.sql.action;

import org.dregs.garish.sql.annotation.*;
import org.dregs.garish.sql.data.Tuple2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.dregs.garish.sql.data.BuildMap.asNode;


public class Insert<T> extends Action {

    private Class<T> tClass;

    private String template = "INSERT INTO `%s` (%s) VALUES(%s)";

    private String tableName;

    private Field[] kTMs;

    private Insert(Class<T> tClass) {
        this.tClass = tClass;
        kTMs = tClass.getDeclaredFields();
        Entity entity = tClass.getAnnotation(Entity.class);
        tableName = entity.name();
    }


    public Tuple2<String,Object[]> builder(T t) {
       try{
           List<String> keys = new ArrayList<>();
           List<Object> values = new ArrayList<>();
           List<String> placeholder = new ArrayList<>();
           for(Field field : kTMs){
               if(
                       null != field.getAnnotation(Id.class) && null != field.getAnnotation(GeneratedValue.class)
               )continue;
               if(null != field.getAnnotation(Ignore.class)) continue;
               Cell cell = field.getAnnotation(Cell.class);
               if(null == cell || null == cell.name())
                   continue;
               keys.add("`"+cell.name()+"`");
               if(!field.isAccessible()) field.setAccessible(true);
               placeholder.add("?");
               values.add(get(field,t));
           }
           return asNode(
                   String.format(
                           template,
                           tableName,
                           String.join(",", keys),
                           String.join(",", placeholder)
                   ),values.toArray()
           );
       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }

    public static <T> Insert<T> createInsert(Class<T> tClass){
        return new Insert<T>(tClass);
    }

    private static String format(String s){
        StringBuilder result = new StringBuilder();
        for(int $i = 0 ; $i < s.length() ; $i++){
            if(Character.isUpperCase(s.charAt($i))){
                result.append("_");
                result.append(Character.toLowerCase(s.charAt($i)));
            }else{
                result.append(s.charAt($i));
            }
        }
        return result.toString();
    }

    private Object get(Field field,T t) throws IllegalAccessException {
        if(!field.isAccessible()) field.setAccessible(true);
        return field.get(t);
    }
}
