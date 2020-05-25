package com.elastic.search.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 17:32
 * @Email: 15290810931@163.com
 */
@Data
public class Person implements Serializable {

    private long id;

    private String username;

    private int age;

    private int status;

    private String birthday;

    private String remark;

    private String updateDate;

}
