package com.elastic.search.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 17:32
 * @Email: 15290810931@163.com
 */
@Data
public class Person implements Serializable {
    //自增id
    private long id;
    //用户名
    private String username;
    //用户年龄
    private int age;
    //生日
    private Date birthday;
    //存款
    private BigDecimal deposit;
    //住址
    private String address;
    //用户状态
    private int status;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;
}
