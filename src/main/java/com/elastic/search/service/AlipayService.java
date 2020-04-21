package com.elastic.search.service;

import com.elastic.search.entity.PaymentRequest;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 12:23
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
public interface AlipayService {

    public int pay(PaymentRequest paymentRequest);
}
