package com.potlid.common.define;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by styb on 2018/3/31.
 * 迁移标记
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface Transfer{
    String before() default "";
    String after() default "";
    String beforeDataSource() ;
    String afterDataSource() ;
    TransferMode transferMode() default TransferMode.INSERT;
    int order() default 0;
}