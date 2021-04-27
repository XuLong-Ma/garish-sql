package org.dregs.garish.sql.data;

import java.util.HashMap;
import java.util.Map;

public class Tuple2<$1, $2> {
    private $1 _1;
    private $2 _2;

    public Tuple2() {
    }

    public Tuple2($1 _1, $2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public $1 get_1() {
        return this._1;
    }

    public void set_1($1 _1) {
        this._1 = _1;
    }

    public $2 get_2() {
        return this._2;
    }

    public void set_2($2 _2) {
        this._2 = _2;
    }

    public $1 _1() {
        return this._1;
    }

    public void _1($1 _1) {
        this._1 = _1;
    }

    public $2 _2() {
        return this._2;
    }

    public void _2($2 _2) {
        this._2 = _2;
    }

    public Map toMap(String _1_, String _2_) {
        return this.toMap(new HashMap(), _1_, _2_);
    }

    public Map toMap(Map stringObjectMap, String _1_, String _2_) {
        stringObjectMap.put(_1_, this._1());
        stringObjectMap.put(_2_, this._2());
        return stringObjectMap;
    }

    public static <$1, $2> Tuple2<$1, $2> initialize($1 _1, $2 _2) {
        return new Tuple2(_1, _2);
    }
}
