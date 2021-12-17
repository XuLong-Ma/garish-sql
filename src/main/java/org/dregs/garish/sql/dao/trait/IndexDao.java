package org.dregs.garish.sql.dao.trait;

import org.dregs.garish.sql.Factor;
import org.dregs.garish.sql.Quote;
import org.dregs.garish.sql.action.*;

import java.util.List;

public interface IndexDao {

    <T> List<T> findList(Factor<T> factor);

    <T> T findOne(Factor<T> factor);

    <T> List<T> findList(Query<T> query);

    <T> T findOne(Query<T> query);

    <T> T index(Class<T> c, Quote<Object, T> q, Object o);

    <T> List<T> indexList(Class<T> c, Quote<Object, T> q, Object o);

    int count(Query query);

    int insert(Object o);

    int delete(Delete delete);

    int update(Update update);

    boolean update(Update... updates);

    boolean update(List<Update> updates);

    boolean save(Save... saves);







    default <T> T onlyOne(List<T> ts){
        if(null == ts || 0 == ts.size())
            return null;
        return ts.get(0);
    }

}
