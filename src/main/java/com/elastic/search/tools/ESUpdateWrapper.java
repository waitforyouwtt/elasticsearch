package com.elastic.search.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:21
 * @Email: 15290810931@163.com
 */
@Slf4j
public class ESUpdateWrapper<T> implements Serializable {

    private Map<String, Object> fieldMap = new HashMap<>();

    private String docId;

    private String index;

    public ESUpdateWrapper<T> add(ESFunction<T, ?> lambda, Object object) {
        String fieldName = SerializedLambdaUtils.convertToFieldName(lambda);
        fieldMap.put(fieldName, object);
        return this;
    }

    public ESUpdateWrapper<T> docId(String id) {
        this.docId = id;
        return this;
    }

    public ESUpdateWrapper<T> index(String index) {
        this.index = index;
        return this;
    }

    public String getIndex() {
        return index;
    }

    public String getDocId() {
        return docId;
    }

    public Map<String, Object> getFieldMap() {
        return fieldMap;
    }
}