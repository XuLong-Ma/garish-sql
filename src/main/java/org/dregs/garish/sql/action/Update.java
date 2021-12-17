package org.dregs.garish.sql.action;


import org.dregs.garish.sql.Quote;
import org.dregs.garish.sql.annotation.Entity;
import org.dregs.garish.sql.data.Tuple;
import org.dregs.garish.sql.data.Tuple2;
import org.dregs.garish.sql.data.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class Update<T> extends Action
{

    private final static String UPDATE_TEMPLATE = "UPDATE `%s` SET %s WHERE %s";

    private Class<T> tClass;

    private String tableName;

    private List<Tuple2<String, Object>> setKVs;

    private List<Tuple2<String, Object>> whereKVs;

    private Decide decide = result -> result > 0;

    public static <T> Update<T> createUpdate(Class<T> tClass)
    {
        return new Update<T>(tClass);
    }

    public static <T> Update<T> createUpdate(Class<T> tClass, Decide decide)
    {
        return new Update<T>(tClass, decide);
    }

    public void setDecide(Decide decide)
    {
        this.decide = decide;
    }

    public Update<T> resetDecide(Decide decide)
    {
        this.setDecide(decide);
        return this;
    }

    public Decide getDecide()
    {
        return this.decide;
    }


    private Update(Class<T> tClass)
    {
        this.tClass = tClass;
        this.tableName = tClass.getAnnotation(Entity.class).name();
        this.setKVs = new ArrayList<>();
        this.whereKVs = new ArrayList<>();
    }

    private Update(Class<T> tClass, Decide decide)
    {
        this.tClass = tClass;
        this.tableName = tClass.getAnnotation(Entity.class).name();
        this.setKVs = new ArrayList<>();
        this.whereKVs = new ArrayList<>();
        this.decide = decide;
    }

    public Update<T> assertSet(Quote<Object, T> key, Object value)
    {
        return set(fieldName(key), value);
    }

    public Update<T> assertEq(Quote<Object, T> key, Object value)
    {
        return eq(fieldName(key), value);
    }

    public Update<T> assertSet(String key, Object value)
    {
        String cell = findCell(key, tClass);
        return setCell(cell, value);
    }

    public Update<T> assertEq(String key, Object value)
    {
        String cell = findCell(key, tClass);
        return eqCell(cell, value);
    }

    public Update<T> assertSetCell(String key, Object value)
    {
        if (null == key)
            throw new RuntimeException("key not exist");
        if (null == value)
            throw new RuntimeException(String.format("[%s] cannot be null", key));
        setKVs.add(Tuple.initialize(key, value));
        return this;
    }

    public Update<T> assertEqCell(String key, Object value)
    {
        if (null == key)
            throw new RuntimeException("key not exist");
        if (null == value)
            throw new RuntimeException(String.format("[%s] cannot be null", key));
        whereKVs.add(Tuple.initialize(key, value));
        return this;
    }

    public Update<T> set(Quote<Object, T> key, Object value)
    {
        return set(fieldName(key), value);
    }

    public Update<T> eq(Quote<Object, T> key, Object value)
    {
        return eq(fieldName(key), value);
    }

    public Update<T> set(String key, Object value)
    {
        String cell = findCell(key, tClass);
        if (null == cell) return this;
        return setCell(cell, value);
    }

    public Update<T> eq(String key, Object value)
    {
        String cell = findCell(key, tClass);
        if (null == cell) return this;
        return eqCell(cell, value);
    }

    public Update<T> setCell(String key, Object value)
    {
        if (null != key && null != value) setKVs.add(Tuple.initialize(key, value));
        return this;
    }

    public Update<T> eqCell(String key, Object value)
    {
        if (null != key && null != value) whereKVs.add(Tuple.initialize(key, value));
        return this;
    }

    public Tuple2<String, Object[]> fusionSO()
    {
        List<Object> objects = new ArrayList<>();
        StringBuilder setBuilder = new StringBuilder();
        for (int $i = 0; $i < setKVs.size(); $i++)
        {
            if (0 != $i)
                setBuilder.append(" , ");
            Tuple2<String, Object> t = setKVs.get($i);
            setBuilder.append("`").append(t._1()).append("` = ?");
            objects.add(t._2());
        }
        StringBuilder whereBuilder = new StringBuilder();
        for (int $i = 0; $i < whereKVs.size(); $i++)
        {
            if (0 != $i)
                whereBuilder.append(" AND ");
            Tuple2<String, Object> t = whereKVs.get($i);
            whereBuilder.append("`").append(t._1()).append("` = ?");
            objects.add(t._2());
        }
        return Tuple.initialize(
                String.format(UPDATE_TEMPLATE, tableName, setBuilder.toString(), whereBuilder.toString()),
                objects.toArray()
        );
    }

    public Tuple3<String, Object[], Decide> fusionSOD()
    {
        Tuple2<String, Object[]> tuple2 = fusionSO();
        return Tuple.initialize(
                tuple2._1(),
                tuple2._2(),
                decide
        );
    }

    public int update(Function<Update<T>, Integer> function)
    {
        return function.apply(this);
    }

    public boolean updateResult(Function<Update<T>, Integer> function)
    {
        return this.decide.use(update(function));
    }

    public <R> R exec(Function<Update<T>, R> function)
    {
        return function.apply(this);
    }

    public void exec(Consumer<Update<T>> consumer)
    {
        consumer.accept(this);
    }

    @FunctionalInterface
    public interface Decide
    {

        boolean use(int result);

    }


}
