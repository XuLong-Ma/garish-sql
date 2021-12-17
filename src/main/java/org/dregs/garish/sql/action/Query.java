package org.dregs.garish.sql.action;


import org.dregs.garish.sql.Factor;
import org.dregs.garish.sql.MapperHub;
import org.dregs.garish.sql.Quote;
import org.dregs.garish.sql.annotation.Entity;
import org.dregs.garish.sql.utils.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Query<T> extends Action
{


    //private final Function2<String,Class<T>,String> fieldAsCell = (f,c) -> findCell(f,c);
    private final Function<Quote, String> methodAsField = m -> fieldName(m);
    private final Function<Quote, Function<Class<T>, String>> methodAsCell = m -> c -> findCell(methodAsField.apply(m), c);
    private final Function<String, Function<Class<T>, String>> fieldAsCell = f -> c -> findCell(f, c);

    private Class<T> tClass;

    private List<String> sqlList;

    private List<Object> objects;

    private StringBuilder attach;

    private String tableName;

    private boolean hasToTerm = true;

    private final String sqlTemplate = "SELECT %s FROM `%s` %s";

    private final String factorTemplate = "`%s` %s %s";

    private boolean isFinish = false;

    private Query(Class<T> tClass)
    {
        this.tClass = tClass;
        this.tableName = tClass.getAnnotation(Entity.class).name();
        this.sqlList = new ArrayList<>();
        this.objects = new ArrayList<>();
        attach = new StringBuilder();
    }

    public static <T> Query<T> indexQuery(Class<T> tClass, Quote<Object, T> k, Object v)
    {
        return new Query<T>(tClass).eqIndex(k, v);
    }

    public static <T> Query<T> createQuery(Class<T> tClass)
    {
        return new Query<T>(tClass);
    }

    public static <T> Query<T> createQuery(Class<T> tClass, boolean hasToTerm)
    {
        Query<T> tQuery = new Query<>(tClass);
        tQuery.hasToTerm = hasToTerm;
        return tQuery;
    }

    private Query<T> comeBack()
    {
        return this;
    }


    private String joinSQL(String columns)
    {
        StringBuilder factor = new StringBuilder("");
        if (0 != this.sqlList.size())
            factor.append("WHERE ").append(String.join(" AND ", this.sqlList));
        factor.append(this.attach.toString());
        String sql = String.format(sqlTemplate, columns, tableName, factor);
        return sql;
    }

    public Factor<T> builder(String columns)
    {
        return Factor.of(joinSQL(columns), this.objects.toArray(), this.tClass);
    }

    public Factor<T> count()
    {
        String sql = joinSQL("COUNT(1)");
        return Factor.of(tClass, sql, this.objects.toArray());
    }

    public Factor<T> builder()
    {
        inspection();
        return builder(MapperHub.cells(tClass));
    }

    public <H> Factor<T> builder(List<Quote> cell)
    {
        return builder(Util.strJoin(",", cell, v -> MapperHub.getCell(tClass, proxyMethodName(v))));
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


    public Query<T> put(String column, String nexus, String placeholder, Object o)
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

    public Query<T> eqIndexCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        this.isFinish = true;
        return put(k, "=", "?", o);
    }

    public Query<T> eqCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "=", "?", o);
    }

    public Query<T> gtCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, ">", "?", o);
    }

    public Query<T> ltCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "<", "?", o);
    }

    public Query<T> geCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, ">=", "?", o);
    }

    public Query<T> leCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "<=", "?", o);
    }

    public Query<T> neCell(String k, Object o)
    {
        if (isPass(k, o)) return comeBack();
        return put(k, "<>", "?", o);
    }

    public Query<T> inCell(String k, Collection o)
    {
        if (isPass(k, o) || 0 == o.size()) return comeBack();
        return put(k, "IN", "(" + loopJoin(o.size(), "?", ",") + ")", o);
    }

    public Query<T> eqIndex(String k, Object o)
    {
        return eqIndexCell(findCell(k, this.tClass), o);
    }

    public Query<T> eq(String k, Object o)
    {
        return eqCell(findCell(k, this.tClass), o);
    }

    public Query<T> gt(String k, Object o)
    {
        return gtCell(findCell(k, this.tClass), o);
    }

    public Query<T> lt(String k, Object o)
    {
        return ltCell(findCell(k, this.tClass), o);
    }

    public Query<T> ge(String k, Object o)
    {
        return geCell(findCell(k, this.tClass), o);
    }

    public Query<T> le(String k, Object o)
    {
        return leCell(findCell(k, this.tClass), o);
    }

    public Query<T> ne(String k, Object o)
    {
        return neCell(findCell(k, this.tClass), o);
    }

    public Query<T> in(String k, Collection o)
    {
        return inCell(findCell(k, this.tClass), o);
    }

    public Query<T> eqIndex(Quote<Object, T> k, Object o)
    {
        return eqIndex(fieldName(k), o);
    }

    public Query<T> eq(Quote<Object, T> k, Object o)
    {
        return eq(fieldName(k), o);
    }

    public Query<T> gt(Quote<Object, T> k, Object o)
    {
        return gt(fieldName(k), o);
    }

    public Query<T> lt(Quote<Object, T> k, Object o)
    {
        return lt(fieldName(k), o);
    }

    public Query<T> ge(Quote<Object, T> k, Object o)
    {
        return ge(fieldName(k), o);
    }

    public Query<T> le(Quote<Object, T> k, Object o)
    {
        return le(fieldName(k), o);
    }

    public Query<T> ne(Quote<Object, T> k, Object o)
    {
        return ne(fieldName(k), o);
    }

    public Query<T> in(Quote<Object, T> k, Collection o)
    {
        return in(fieldName(k), o);
    }

    public Query<T> limit(int offset, int amount)
    {
        attach.append(String.format(" LIMIT %s,%s", offset, amount));
        return comeBack();
    }

    public Query<T> ascCell(String k)
    {
        return order(k, "ASC");
    }

    public Query<T> descCell(String k)
    {
        return order(k, "DESC");
    }

    public Query<T> asc(String k)
    {
        return ascCell(findCell(k, tClass));
    }

    public Query<T> desc(String k)
    {
        return descCell(findCell(k, tClass));
    }

    public Query<T> asc(Quote<Object, T> k)
    {
        return asc(fieldName(k));
    }

    public Query<T> desc(Quote<Object, T> k)
    {
        return desc(fieldName(k));
    }

    public Query<T> order(String cell, String type)
    {
        if (null == cell)
            return comeBack();
        if (!"ASC".equals(type) && !"DESC".equals(type))
            return comeBack();
        attach.append(String.format(" ORDER BY `%s` %s", cell, type));
        return comeBack();
    }

    public void inspection()
    {
        if (hasToTerm && 0 == sqlList.size() && 0 == attach.length())
            throw new RuntimeException("filter conditions must exist or open not limit mode");
    }

    public List<T> findList(Function<Query<T>, List<T>> function)
    {
        return function.apply(this);
    }

    public T findOne(Function<Query<T>, T> function)
    {
        return function.apply(this);
    }

    public int count(Function<Query<T>, Integer> function)
    {
        return function.apply(this);
    }

    public <R> R exec(Function<Query<T>, R> function)
    {
        return function.apply(this);
    }

    public void exec(Consumer<Query<T>> consumer)
    {
        consumer.accept(this);
    }


}
