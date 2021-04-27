package org.dregs.garish.sql.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoRowMapper {

    boolean value() default true;

}
