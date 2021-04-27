package org.dregs.garish.sql.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface Ignore {

    String[] names() default {};

}
