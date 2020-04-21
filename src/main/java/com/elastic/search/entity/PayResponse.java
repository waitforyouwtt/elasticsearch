package com.elastic.search.entity;

import lombok.Data;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 14:00
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
@Data
public class PayResponse {

    private String resultStatus;

    private String result;

    private String memo;
}
