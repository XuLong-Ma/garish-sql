package org.dregs.garish.sql.data;

import java.util.HashMap;
import java.util.Map;

public class Tuple3<$1, $2, $3> {
    private $1 _1;
    private $2 _2;
    private $3 _3;

    public Tuple3() {
    }

    public Tuple3($1 _1, $2 _2, $3 _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
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

    public $3 get_3() {
        return this._3;
    }

    public void set_3($3 _3) {
        this._3 = _3;
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

    public $3 _3() {
        return this._3;
    }

    public void _3($3 _3) {
        this._3 = _3;
    }

    public Map toMap(String _1_, String _2_, String _3_) {
        return this.toMap(new HashMap(), _1_, _2_, _3_);
    }

    public Map toMap(Map stringObjectMap, String _1_, String _2_, String _3_) {
        stringObjectMap.put(_1_, this._1());
        stringObjectMap.put(_2_, this._2());
        stringObjectMap.put(_3_, this._3());
        return stringObjectMap;
    }

    public static <$1, $2, $3> Tuple3<$1, $2, $3> initialize($1 _1, $2 _2, $3 _3) {
        return new Tuple3(_1, _2, _3);
    }
}
