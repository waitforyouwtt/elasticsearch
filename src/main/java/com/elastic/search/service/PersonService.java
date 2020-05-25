package com.elastic.search.service;


import java.io.IOException;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 12:23
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
public interface PersonService {

    /**
     * 创建索引库
     * @return
     */
    boolean createIndex();

    /**
     * 创建添加文档
     * @return
     */
    void addDoc() throws IOException;

}
