package com.elastic.search.util;

import com.google.gson.JsonObject;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/23 21:09
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
public class ElasticSearchConnection {

    public static final String host = "127.0.0.1";
    public static final int port = 9100;

    public static void main(String[] args) throws UnknownHostException {

        Settings settings = Settings.builder().put( "cluster.name","my-application" ).build();

        TransportClient client = new PreBuiltTransportClient( settings )
                .addTransportAddress( new TransportAddress( InetAddress.getByName( host ),port ) );

        //创建文档
        createIndex(client);

        //获取文档
        //getDoc(client);

        //更新文档
        //updateDoc(client);

        //删除文档
        //deleteDoc(client);
    }

    /**
     * 创建文档
     * @param client
     */
    public static void createIndex(TransportClient client){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty( "name","java编程思想" );
        jsonObject.addProperty( "publishDate","2018-10-24" );
        jsonObject.addProperty( "price","100" );
        IndexResponse response = client.prepareIndex("book","java","1")
                .setSource( jsonObject.toString(), XContentType.JSON ).get();
        System.out.println("索引名称："+response.getIndex());
        System.out.println("文档类型："+response.getType());
        System.out.println("文档ID："+response.getId());
        System.out.println("档期实例状态："+response.status());
    }
}
