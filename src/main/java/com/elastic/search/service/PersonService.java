package com.elastic.search.service;


import com.elastic.search.entity.Person;

import java.io.IOException;
import java.util.List;

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
    void addDoc(Person person) throws IOException;

    /**
     * 修改文档
     */
    void updateDoc(Person person) throws IOException;

    /**
     * 批量新增
     * @param personList
     */
    void batchInsert(List<Person> personList);

    /**
     * 根据id 查询ES
     * @param id
     * @return
     * @throws IOException
     */
    Person queryESById(String id) throws IOException;

    /**
     * 查询全部数据且过滤字段
     * @return
     */
    List<Person> queryFetchSource() throws IOException;

    /**
     * 分页查询
     * @return
     */
    List<Person> queryByPage() throws IOException;

    List<Person> termQuery(Person person) throws IOException;

}
