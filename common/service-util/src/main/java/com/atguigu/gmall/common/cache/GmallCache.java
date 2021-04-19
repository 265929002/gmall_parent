package com.atguigu.gmall.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})//注解只能声明在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    /**
     * 定义缓存key的前缀
     * @return
     */
    String prefix() default "cache";
}
