package org.dregs.garish.sql;

import java.io.Serializable;

@FunctionalInterface
public interface Quote<OUT,OBJ> extends Serializable {

    OUT invoke(OBJ obj);

}
