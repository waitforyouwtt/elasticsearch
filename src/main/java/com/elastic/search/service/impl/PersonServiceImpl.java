package com.elastic.search.service.impl;

import com.elastic.search.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: luoxian
 * @Date: 2020/5/25 15:08
 * @Email: 15290810931@163.com
 */
@Slf4j
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    RestHighLevelClient client;

    @Override
    public boolean createIndex() {
        //创建索引对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("studymodel");
        //设置参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
        //指定映射
        createIndexRequest.mapping("doc"," {\n" +
                " \t\"properties\": {\n" +
                "            \"studymodel\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "            \"name\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "           \"description\": {\n" +
                "              \"type\": \"text\",\n" +
                "              \"analyzer\":\"english\"\n" +
                "           },\n" +
                "           \"pic\":{\n" +
                "             \"type\":\"text\",\n" +
                "             \"index\":false\n" +
                "           }\n" +
                " \t}\n" +
                "}", XContentType.JSON);

        //操作索引的客户端
        IndicesClient indices = client.indices();
        //执行创建索引库
        CreateIndexResponse createIndexResponse = null;
        try {
            createIndexResponse = indices.create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info("es 创建index索引失败：{}",e);
        }
        //得到响应
        boolean acknowledged = createIndexResponse.isAcknowledged();
       return acknowledged;
    }

    /**
     * 创建添加文档
     *
     * @return
     */
    @Override
    public void addDoc() throws IOException {
        //文档内容
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "rocketMQ 实战");
        jsonMap.put("description", "rocketMQ 实战 阿里人写的书");
        jsonMap.put("studymodel", "201002");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 56f);

        //创建索引创建对象
        IndexRequest indexRequest = new IndexRequest("elasticsearch_test2","doc");
        //文档内容
        indexRequest.source(jsonMap);
        //通过client进行http的请求
        IndexResponse indexResponse = client.index(indexRequest,RequestOptions.DEFAULT);
        DocWriteResponse.Result result = indexResponse.getResult();
        log.info("添加文档的结果：{}",result);
    }
}
