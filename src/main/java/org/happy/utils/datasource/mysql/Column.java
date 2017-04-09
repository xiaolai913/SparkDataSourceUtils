package org.happy.utils.datasource.mysql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表列注解
 *
 * @author happy
 * @version 17/4/08 下午7:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Column {

    public String name() default "";

    public int length() default 0;

    public boolean nullable() default true;

    public String defaultValue() default "";

    public String type() default "";

    public boolean unique() default false;

    public boolean mask() default false; //会用特定字符替换

    public boolean htmlEscape() default false; //替换html

    public String comment() default ""; //替换html
}
