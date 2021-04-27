package org.dregs.garish.sql.data;


import java.util.HashMap;
import java.util.Map;

public class Tuple<$1>
{
    private $1 _1;

    public $1 get_1()
    {
        return this._1;
    }

    public void set_1($1 _1)
    {
        this._1 = _1;
    }

    public $1 _1()
    {
        return this._1;
    }

    public void _1($1 _1)
    {
        this._1 = _1;
    }

    public Tuple()
    {
    }

    public Tuple($1 _1)
    {
        this._1 = _1;
    }

    public Map toMap(String _1_)
    {
        return this.toMap(new HashMap(), _1_);
    }

    public Map toMap(Map stringObjectMap, String _1_)
    {
        stringObjectMap.put(_1_, this._1());
        return stringObjectMap;
    }

    public static <$1> Tuple<$1> initialize($1 _1)
    {
        return new Tuple();
    }

    public static <$1, $2> Tuple2<$1, $2> initialize($1 _1, $2 _2)
    {
        return new Tuple2(_1, _2);
    }

    public static <$1, $2, $3> Tuple3<$1, $2, $3> initialize($1 _1, $2 _2, $3 _3)
    {
        return new Tuple3(_1, _2, _3);
    }

    public static <$1> Tuple<$1> asTuple($1 _1)
    {
        return new Tuple();
    }

    public static <$1, $2> Tuple2<$1, $2> asTuple($1 _1, $2 _2)
    {
        return new Tuple2(_1, _2);
    }

    public static <$1, $2, $3> Tuple3<$1, $2, $3> asTuple($1 _1, $2 _2, $3 _3)
    {
        return new Tuple3(_1, _2, _3);
    }

}