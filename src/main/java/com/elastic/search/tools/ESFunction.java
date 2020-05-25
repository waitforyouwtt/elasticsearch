package com.elastic.search.tools;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:20
 * @Email: 15290810931@163.com
 */
@FunctionalInterface
public interface ESFunction<T,R> extends Function<T, R>,Serializable {
}