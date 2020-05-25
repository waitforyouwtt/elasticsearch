package com.elastic.search.controller;

import com.elastic.search.tools.FType;
import com.elastic.search.tools.FilterType;
import lombok.Data;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:34
 * @Email: 15290810931@163.com
 */
@Data
public class FilterBean {

    /**
     * 开始结束时间中间使用#分隔
     */
    @FilterType(value = FType.DATE)
    private String testYear;

    /**
     * 语言 数组逗号分隔
     */
    @FilterType(value = FType.ARRAY)
    private String testArray;


    /**
     * 字符串过滤类型  ignoreValue=0的不过滤
     */
    @FilterType(value = FType.STRING, ignoreValue = "0")
    private String testString;
}
