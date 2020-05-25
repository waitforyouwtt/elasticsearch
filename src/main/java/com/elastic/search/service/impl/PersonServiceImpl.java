package com.elastic.search.service.impl;

import com.elastic.search.entity.Person;
import com.elastic.search.service.PersonService;
import com.elastic.search.util.ElasticsearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean createIndex() {
        //创建索引对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("person");
        //设置参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
        //指定映射
        createIndexRequest.mapping("doc"," {\n" +
                " \t\"properties\": {\n" +
                "            \"id\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "            \"username\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "            \"age\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "            \"birthday\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "            \"deposit\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "            \"address\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "            \"status\":{\n" +
                "             \"type\":\"keyword\"\n" +
                "           },\n" +
                "           \"createTime\": {\n" +
                "              \"type\": \"text\",\n" +
                "              \"analyzer\":\"english\"\n" +
                "           },\n" +
                "           \"updateTime\":{\n" +
                "             \"type\":\"text\",\n" +
                "             \"index\":false\n" +
                "           }\n" +
                " \t}\n" +
                "}", XContentType.JSON);

        //操作索引的客户端
        IndicesClient indices = restHighLevelClient.indices();
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
    public void addDoc(Person person) throws IOException {
        //文档内容
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("id", person.getId());
        jsonMap.put("username", person.getUsername());
        jsonMap.put("age", person.getAge());
        jsonMap.put("birthday", fyFormatDate(person.getBirthday(),"yyyy-MM-dd"));
        jsonMap.put("deposit",  person.getDeposit());
        jsonMap.put("address",  person.getAddress());
        jsonMap.put("status",   person.getStatus());
        jsonMap.put("createTime",person.getCreateTime());
        jsonMap.put("updateTime",person.getUpdateTime());

        //创建索引创建对象
        IndexRequest indexRequest = new IndexRequest("user_info","doc");
        //文档内容
        indexRequest.source(jsonMap);
        //通过client进行http的请求
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest,RequestOptions.DEFAULT);
        DocWriteResponse.Result result = indexResponse.getResult();
        log.info("添加文档的结果：{}",result);
    }

    public static String fyFormatDate(Date date,String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        String sDate = f.format(date);
        return sDate;
    }

    /**
     * 修改文档
     */
    @Override
    public void updateDoc(Person person) throws IOException {
        IndexRequest indexRequest = new IndexRequest("user_info").type("doc").id(String.valueOf(person.getId())).source(BeanMap.create(person));

        // 创建更新请求，指定index，type,id,如果indexRequest 有值 （存在该数据）则用doc指定的内容更新indexRequest中指定的source，
        // 如果不存在该数据，则插入一条indexRequest指定的source数据
        UpdateRequest updateRequest = new UpdateRequest("user_info", "doc", String.valueOf(person.getId()))
                .doc(BeanMap.create(person)).upsert(indexRequest);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(updateRequest);
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        log.info("修改数据的结果：{}",bulk);
    }

    /**
     * 批量新增
     *
     * @param personList
     */
    @Override
    public void batchInsert(List<Person> personList) {
        // 创建批量操作请求
        BulkRequest bulkRequest = new BulkRequest();

        for (Person person : personList) {
            IndexRequest indexRequest = new IndexRequest("index").type("type").id(String.valueOf(person.getId()))
                    .source(BeanMap.create(person));

            // 创建更新请求，指定index，type,id,如果indexRequest 有值 （存在该数据）则用doc指定的内容更新indexRequest中指定的source，
            // 如果不存在该数据，则插入一条indexRequest指定的source数据
            UpdateRequest updateRequest = new UpdateRequest("index", "type", String.valueOf(person.getId()))
                    .doc(BeanMap.create(person)).upsert(indexRequest);

            // 将更新请求加入批量操作请求
            bulkRequest.add(updateRequest);
        }​
        try {
            // 执行批量操作
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
