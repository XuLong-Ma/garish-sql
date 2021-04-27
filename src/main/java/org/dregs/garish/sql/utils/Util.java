package org.dregs.garish.sql.utils;

import java.util.Iterator;
import java.util.function.Function;

public class Util {

    public static <T> String strJoin(String delimiter,Iterable<T> ts, Function<T,String> function){
        if(null == ts || null == function)return null;
        Iterator<T> iterator = ts.iterator();
        StringBuilder builder = new StringBuilder();
        int $i = 0;
        while (iterator.hasNext()){
            if(0 != $i)
                builder.append(delimiter);
            T element = iterator.next();
            String value = function.apply(element);
            builder.append(value);
            $i++;
        }
        return builder.toString();
    }
    public static <T> String strJoin(String delimiter,T[] ts, Function<T,String> function){
        if(null == ts)return null;
        if(null == function)function = v->null;
        StringBuilder builder = new StringBuilder();
        for (int $i = 0; $i < ts.length; $i++) {
            if(0 != $i)
                builder.append(delimiter);
            String value = function.apply(ts[$i]);
            builder.append(value);
        }
        return builder.toString();
    }


}
