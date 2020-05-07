package com.elastic.search.old;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/17 22:01
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
@Slf4j
public class ElasticSearchOld {
    public static final String host = "127.0.0.1";
    public static final int port = 9200;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Settings settings = Settings.builder().put( "cluster.name","my-application" ).build();

        //获取client
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        //继续添加其他地址

        //创建文档
        createDocment(client);
        //修改文档:method 1
        updateMethodOne(client);
        //修改文档：method 2
        updateMethodTwo(client);
        //查询文档：
        query(client);
        //删除文档
        deleteDoc(client);

        //on shutdown
        client.close();
    }

    /**
     * 创建文档
     */
    private static void createDocment(TransportClient client) throws IOException {
        //创建文档
        XContentBuilder mapping = jsonBuilder().startObject()
                .startObject( "settings" ).field( "number_of_shard",1 ) //设置分片数量
                .field("number_of_replicas",0  ) //设置副本数量
                .endObject().endObject()
                .startObject(  )
                .startObject( "typeName" ) //type 名称
                .startObject( "properties" )
                .startObject( "type" ).field( "type","string" ).field( "store","yes" ).endObject()
                .startObject( "eventCount" ).field( "type","long" ).field( "store","yes" ).endObject()
                .startObject( "eventDate" ).field( "type","date" ).field( "format","dateOptionalTime" ).field( "store","yes" ).endObject()
                .startObject( "message" ).field( "type","string" ).field( "index","not_analyzed" ).field( "store","yes" ).endObject()
                .endObject()
                .endObject()
                .endObject();

        CreateIndexRequestBuilder cirb = client.admin().indices()
                .prepareCreate( "storeIndexName" )  //index 名称
                .setSource( mapping );
        CreateIndexResponse response = cirb.execute().actionGet();
        if (response.isAcknowledged()){
            log.info("index created");
        }else{
            log.info( "index create failed" );
        }
    }

    private static void updateMethodOne(TransportClient client) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest updateRequest =new UpdateRequest(  );
        updateRequest.index("indexName");
        updateRequest.type("typeName");
        updateRequest.id("id");
        updateRequest.doc(jsonBuilder().startObject(  )
                .field( "type","filed" ).endObject());
        client.update( updateRequest ).get();
    }

    private static void updateMethodTwo(TransportClient client) throws IOException, ExecutionException, InterruptedException {
        //修改文档
        IndexRequest indexRequest = new IndexRequest( "indexName","typeName","3" )
                .source( jsonBuilder().startObject(  )
                        .field( "type","syslog" )
                        .field( "eventCount",2)
                        .field( "eventDate",new Date() )
                        .field( "message","凤凰小哥哥最帅" )
                        .endObject()
                );
        UpdateRequest updateRequest1 = new UpdateRequest( "indexName","typeName","3" )
                .doc( jsonBuilder().startObject().field( "type","file" ).endObject() )
                .upsert( indexRequest );
        client.update( updateRequest1 ).get();
    }

    private static void query(TransportClient client){
        //查询文档
        GetResponse response1 = client.prepareGet("secilog","log","1").get();
        String source = response1.getSource().toString();
        long version = response1.getVersion();
        String indexName = response1.getIndex();
        String type = response1.getType();
        String id = response1.getId();
    }

    private static void deleteDoc(TransportClient client){
        DeleteResponse deleteResponse = client.prepareDelete("decilog","log","4").get();
        boolean isFound = deleteResponse.isFragment();
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest( "secilog" );
        client.admin().indices().delete( deleteIndexRequest );
    }
}
