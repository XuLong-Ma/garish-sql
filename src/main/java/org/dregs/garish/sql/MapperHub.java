package org.dregs.garish.sql;

import org.dregs.garish.sql.annotation.Cell;
import org.dregs.garish.sql.annotation.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapperHub {


    private final static Map<String,String> CLASS_MAPPER_TABLE = new HashMap<>();

    private final static Map<String,Map<String,String>> COL_TO_FIELD = new HashMap<>();

    private final static Map<String,Map<String,String>> FIELD_TO_COL = new HashMap<>();

    private final static Map<String,Map<String, Method>> METHOD_CACHE = new HashMap<>();

    private final static Map<String,String> CELLS_CACHE = new HashMap<>();




    public static Map<String,String> queryMapper(){
        Map<String, String> map = new HashMap<>();
        for(Map.Entry<String,String> entry : CLASS_MAPPER_TABLE.entrySet())
            map.put(entry.getKey(),entry.getValue());
        return map;
    }



    public static String getField(Class tClass,String col){
        String tableName = getTableName(tClass);
        Map<String, String> map = COL_TO_FIELD.get(tableName);
        if(null == map)return null;
        return map.get(col);
    }
    public static String getCell(Class tClass,String field){
        String tableName = getTableName(tClass);
        Map<String, String> map = FIELD_TO_COL.get(tableName);
        if(null == map)return null;
        return map.get(field);
    }
    public static Method getMethod(Class tClass,String name){
        String tableName = getTableName(tClass);
        Map<String, Method> map = METHOD_CACHE.get(tableName);
        if(null == map)return null;
        return map.get(name);
    }

    public static String cells(Class tClass){
        String tableName = getTableName(tClass);
        String result = CELLS_CACHE.get(tableName);
        if(null != result) return result;
        Map<String, String> map = COL_TO_FIELD.get(tableName);
        List<String> cols = map.keySet().stream().map(s -> (String.format("`%s`", s))).collect(Collectors.toList());
        result = String.join(",", cols);
        return result;
    }

    private static String getTableName(Class tClass){
        if(null == tClass) return null;
        Entity entity = (Entity) tClass.getAnnotation(Entity.class);
        if(null == entity) return null;
        return entity.name();
    }





    public static void put(Class tClass){
        Entity entity = (Entity) tClass.getAnnotation(Entity.class);
        if(null == entity)
            return;
        String tableName = entity.name();
        Field[] fields = tClass.getDeclaredFields();
        Map<String, String> colKey = new HashMap<>();
        Map<String, String> fieldKey = new HashMap<>();
        Map<String, Method> methodDict = new HashMap<>();
        for(Field field : fields){
            Cell cell = field.getAnnotation(Cell.class);
            if(null == cell)continue;
            String name = field.getName();
            String value = null;
            if(null != cell){
                value = cell.name();
            }
            if(null == value){
                StringBuilder result = new StringBuilder();
                for(int $i = 0 ; $i < name.length() ; $i++){
                    if(Character.isUpperCase(name.charAt($i))){
                        result.append("_");
                        result.append(Character.toLowerCase(name.charAt($i)));
                    }else{
                        result.append(name.charAt($i));
                    }
                }
                value = result.toString();
            }
            colKey.put(value,name);
            fieldKey.put(name,value);
        }
        for(Method method : tClass.getDeclaredMethods()){
            methodDict.put(method.getName(),method);
        }
        CLASS_MAPPER_TABLE.put(tClass.getSimpleName(),tableName);
        METHOD_CACHE.put(tableName,methodDict);
        COL_TO_FIELD.put(tableName,colKey);
        FIELD_TO_COL.put(tableName,fieldKey);
    }

}
