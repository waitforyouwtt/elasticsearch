package com.elastic.search.tools;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:22
 * @Email: 15290810931@163.com
 */
@Data
public class SearchResult {

    /**
     * 命中总数
     */
    public Long totalHits;

    /**
     * 返回文档集合
     */
    public List<Map<String,Object>> documents;

    /**
     * 查询耗时 单位毫秒
     */
    public Long took;


    /**
     * 聚合结果
     */
    public Map<String,Map<String,Long>> aggregations;



    public SearchResult(Long totalHits,
                        List<Map<String, Object>> documents,
                        Long took,
                        Map<String,Map<String,Long>> aggregations) {
        this.totalHits = totalHits;
        this.documents = documents;
        this.took = took;
        this.aggregations = aggregations;
    }


    public SearchResult() {
        this.totalHits=0L;
        this.documents =new ArrayList<>();
        this.took=0L;
    }
}
