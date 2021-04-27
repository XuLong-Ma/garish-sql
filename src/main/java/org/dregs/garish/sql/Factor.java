package org.dregs.garish.sql;

public class Factor<T> {

    private Class<T> tClass;

    private String sql;

    private Object[] objects;

    public Class<T> getTClass() {
        return this.tClass;
    }

    public String getSql() {
        return this.sql;
    }

    public Object[] getObjects() {
        return this.objects;
    }

    public Factor(Class<T> tClass, String sql, Object[] objects) {
        this.tClass = tClass;
        this.sql = sql;
        this.objects = objects;
    }

    public static <T> Factor<T> of(Class<T> tClass, String sql, Object[] objects){
        return new Factor<T>(tClass,sql,objects);
    }
    public static <T> Factor<T> of(String sql, Object[] objects,Class<T> tClass){
        return new Factor<T>(tClass,sql,objects);
    }
}
