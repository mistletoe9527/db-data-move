package com.potlid.common.define;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by styb on 2018/3/31.
 * 迁移目标的表名或者字段名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Condition {
    String operator() default "=";
    String before() default "";
    String after() default "";
    boolean insert() default true;
    boolean select() default true;
    boolean unique() default false;
}