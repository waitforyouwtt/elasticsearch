package com.elastic.search.tools;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:25
 * @Email: 15290810931@163.com
 */
public enum FType {

    /**
     * 字符串，默认类型。建议无特殊情况都用此类型
     */
    STRING,
    /**
     * 日期类型 #号分隔 格式要求：2016#2019
     */
    DATE,
    /**
     * 多值类型，逗号分隔  格式要求：a,b,c
     */
    ARRAY
}
