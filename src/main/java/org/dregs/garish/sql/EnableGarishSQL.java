package org.dregs.garish.sql;

import org.dregs.garish.sql.config.InjectBeans;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(InjectBeans.class)
public @interface EnableGarishSQL {
}
