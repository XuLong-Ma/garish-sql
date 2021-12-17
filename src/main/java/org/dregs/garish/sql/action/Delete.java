package org.dregs.garish.sql.action;

import org.dregs.garish.sql.Factor;
import org.dregs.garish.sql.Quote;
import org.dregs.garish.sql.annotation.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Delete<T> extends Action
{

    private final Function<Quote, String> methodAsField = m -> fieldName(m);
    private final Function<Quote, Function<Class<T>, String>> methodAsCell = m -> c -> findCell(methodAsField.apply(m), c);
    private final Function<String, Function<Class<T>, String>> fieldAsCell = f -> c -> findCell(f, c);

    private Class<T> tClass;

    private List<String> sqlList;

    private List<Object> objects;

    private String tableName;

    private boolean hasToTerm = true;

    private final String sqlTemplate = "DELETE FROM `%s` %s";

    private final String factorTemplate = "`%s` %s %s";

    private boolean isFinish = false;

    private Delete(Class<T> tClass)
    {
        this.tClass = tClass;
        this.tableName = tClass.getAnnotation(Entity.class).name();
        this.sqlList = new ArrayList<>();
        this.objects = new ArrayList<>();
    }

    public static <T> Delete<T> indexDelete(Class<T> tClass, Quote<Object, T> k, Object v)
    {
        return new Delete<T>(tClass).eqIndex(k, v);
    }

    public static <T> Delete<T> createDelete(Class<T> tClass)
    {
        return new Delete<T>(tClass);
    }

    public static <T> Delete<T> createDelete(Class<T> tClass, boolean hasToTerm)
    {
        Delete<T> delete = new Delete<>(tClass);
        delete.hasToTerm = hasToTerm;
        return delete;
    }

    private Delete<T> comeBack()
    {
        return this;
    }

    private String joinSQL()
    {
        StringBuilder factor = new StringBuilder();
        if (0 != this.sqlList.size()) factor.append("WHERE " + String.join(" AND ", this.sqlList));
        String sql = String.format(sqlTemplate, tableName, factor.toString());
        return sql;
    }

    public Factor builder()
    {
        inspection();
        return Factor.of(joinSQL(), this.objects.toArray(), null);
    }


    private static String loopJoin(int number, String content, String delimiter)
    {
        List<String> strings = new ArrayList<>();
        for (int $i = 0; $i < number; $i++)
        {
            strings.add(content);
        }
        return String.join(delimiter, strings);
    }

    private boolean isPass(Object key, Object o)
    {
        return (isFinish || null == key || null == o);
    }


    public Delete<T> put(String column, String nexus, String placeholder, Object o)
    {
        if (null == column || null == o || null == nexus || null == placeholder)
            return comeBack();
        String sql = String.format(factorTemplate, column, nexus, placeholder);
        sqlList.add(sql);
        if (o instanceof Collection)
        {
            objects.addAll((Collection<?>) o);
        } else
        {
            objects.add(o);
        }
        return comeBack();
    }

    public Delete<T> eqIndexCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        this.isFinish = true;
        return put(k, "=", "?", o);
    }

    public Delete<T> eqCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "=", "?", o);
    }

    public Delete<T> gtCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, ">", "?", o);
    }

    public Delete<T> ltCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "<", "?", o);
    }

    public Delete<T> geCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, ">=", "?", o);
    }

    public Delete<T> leCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "<=", "?", o);
    }

    public Delete<T> neCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "<>", "?", o);
    }

    public Delete<T> inCell(String k, Collection o)
    {
        if (isPass(k, o) || 0 == o.size()) return comeBack();
        return put(k, "IN", "(" + loopJoin(o.size(), "?", ",") + ")", o);
    }

    public Delete<T> eqIndex(String k, Object o)
    {
        return eqIndexCell(findCell(k, this.tClass), o);
    }

    public Delete<T> eq(String k, Object o)
    {
        return eqCell(findCell(k, this.tClass), o);
    }

    public Delete<T> gt(String k, Object o)
    {
        return gtCell(findCell(k, this.tClass), o);
    }

    public Delete<T> lt(String k, Object o)
    {
        return ltCell(findCell(k, this.tClass), o);
    }

    public Delete<T> ge(String k, Object o)
    {
        return geCell(findCell(k, this.tClass), o);
    }

    public Delete<T> le(String k, Object o)
    {
        return leCell(findCell(k, this.tClass), o);
    }

    public Delete<T> ne(String k, Object o)
    {
        return neCell(findCell(k, this.tClass), o);
    }

    public Delete<T> in(String k, Collection o)
    {
        return inCell(findCell(k, this.tClass), o);
    }

    public Delete<T> eqIndex(Quote<Object, T> k, Object o)
    {
        return eqIndex(fieldName(k), o);
    }

    public Delete<T> eq(Quote<Object, T> k, Object o)
    {
        return eq(fieldName(k), o);
    }

    public Delete<T> gt(Quote<Object, T> k, Object o)
    {
        return gt(fieldName(k), o);
    }

    public Delete<T> lt(Quote<Object, T> k, Object o)
    {
        return lt(fieldName(k), o);
    }

    public Delete<T> ge(Quote<Object, T> k, Object o)
    {
        return ge(fieldName(k), o);
    }

    public Delete<T> le(Quote<Object, T> k, Object o)
    {
        return le(fieldName(k), o);
    }

    public Delete<T> ne(Quote<Object, T> k, Object o)
    {
        return ne(fieldName(k), o);
    }

    public Delete<T> in(Quote<Object, T> k, Collection o)
    {
        return in(fieldName(k), o);
    }


    public void inspection()
    {
        if (hasToTerm && 0 == sqlList.size())
            throw new RuntimeException("filter conditions must exist or open not limit mode");
    }

    public <R> R exec(Function<Delete<T>, R> function)
    {
        return function.apply(this);
    }

    public void exec(Consumer<Delete<T>> consumer)
    {
        consumer.accept(this);
    }

    public int delete(Function<Delete<T>, Integer> function){
        return function.apply(this);
    }


}
