package com.elastic.search.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Map;


/**
 * @Author: luoxian
 * @Date: 2020/5/21 17:18
 * @Email: 15290810931@163.com
 */
public class ElasticsearchUtils {

    /**
     * 将实体转化为 XContentBuilder 以便存储es
     *
     * @param xContentBuilder
     * @param object
     * @return
     */
    public static XContentBuilder objectToXContentBuilder(XContentBuilder xContentBuilder, Object object) {
        try {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(object));
            xContentBuilder.startObject();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                xContentBuilder.field(entry.getKey(), entry.getValue());
            }
            xContentBuilder.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xContentBuilder;
    }

}
