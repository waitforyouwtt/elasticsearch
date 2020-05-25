package com.elastic.search.controller;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:33
 * @Email: 15290810931@163.com
 */
@Data
public class QueryBean {
    private String keyword;

    private String sortField;

    private String sort = "asc";

    private Integer size=20;

    private Integer page=1;


    public SortOrder getSortOrder(){
        if (StringUtils.lowerCase(this.sort).equals("desc")){
            return SortOrder.DESC;
        }
        return SortOrder.ASC;
    }
}
