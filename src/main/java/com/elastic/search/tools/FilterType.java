package com.elastic.search.tools;

import java.lang.annotation.*;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:26
 * @Email: 15290810931@163.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FilterType {

    /**
     * 过滤类型
     * @see FType
     * @return
     */
    FType value() default FType.STRING;

    /**
     * 忽略过滤 如果设置此值，为此值则跳过
     * @return
     */
    String ignoreValue() default "";
}
