package com.elastic.search.controller;

import com.elastic.search.tools.ESFilterWrapper;
import com.elastic.search.tools.ESUpdateWrapper;
import com.elastic.search.tools.ElasticSearchHelper;
import com.elastic.search.tools.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:32
 * @Email: 15290810931@163.com
 */
@RestController
public class DemoController {

    @Autowired
    private ElasticSearchHelper searchHelper;

    /**
     * 检索示例
     * @param queryBean
     * @param filterBean
     * @return
     */
    public SearchResult keyword(QueryBean queryBean, FilterBean filterBean){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        List<QueryBuilder> filterList = boolQueryBuilder.filter();
        filterList.addAll(new ESFilterWrapper().filter(filterBean));

        List<QueryBuilder> boolmustList = boolQueryBuilder.must();
        boolmustList.add(QueryBuilders.matchQuery("title",queryBean.getKeyword()));

        searchSourceBuilder.query(boolQueryBuilder);

        return requestResult(queryBean,searchSourceBuilder);
    }


    /**
     * 更新示例
     * @param updateWrapper
     * @throws IOException
     */
    public void lambdaUpdateDocument(ESUpdateWrapper<?> updateWrapper) throws IOException {
        if (StringUtils.isBlank(updateWrapper.getIndex())){
            updateWrapper.index("indexName");
        }
        searchHelper.lambdaUpdateDocument(updateWrapper);
    }


    private SearchResult requestResult(QueryBean queryBean,SearchSourceBuilder searchSourceBuilder) {
        SearchRequest firstSearchRequest = new SearchRequest("indexName");
        searchSourceBuilder.trackTotalHits(true); //设置返回总数
        searchSourceBuilder.trackScores(true);
        searchSourceBuilder.size(queryBean.getSize()).from((queryBean.getPage()-1)*queryBean.getSize());
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        /*返回字段*/
        searchSourceBuilder.fetchSource(new String[]{"title"}, Strings.EMPTY_ARRAY);
        /*排序*/
        searchSourceBuilder.sort(new FieldSortBuilder(queryBean.getSortField()).order(queryBean.getSortOrder()));
        /*学科聚合*/
        searchSourceBuilder.aggregation(AggregationBuilders.terms("suject_count").field("suject_code").size(100000).order(BucketOrder.key(true)));
        firstSearchRequest.source(searchSourceBuilder);
        SearchResult searchResult = searchHelper.searchDocument(firstSearchRequest);
        return searchResult;
    }

}
