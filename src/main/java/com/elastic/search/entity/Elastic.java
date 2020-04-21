package com.elastic.search.entity;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/17 22:01
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
public class Elastic {

    public static void main(String[] args) {
      /*  XContentBuilder mapping = XContentFactory.jsonBuilder().
                startObject().
                startObject( "settings" ).field( "number_of_shard",1 ) //设置分片数量
                .field("number_of_replicas",0  ) //设置副本数量
                .endObject().endObject()
                .startObject(  ).startObject( type ) //type 名称
                .startObject( "properties" )
                .startObject( "type" ).field( "type","string" ).field( "store","yes" ).endObject()
                .startObject( "eventCount" ).field( "type","long" ).field( "store","yes" ).endObject()
                .startObject( "eventDate" ).field( "type","date" ).field( "format","dateOptionalTime" ).field( "store","yes" ).endObject()
                .startObject( "message" ).field( "type","string" ).field( "index","not_analyzed" ).field( "store","yes" ).endObject()
                .endObject()
                .endObject()
                .endObject();

        CreateIndexRequestBuilder cirb ;*/













    }
}
