package com.elastic.search.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 11:31
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
@Data
public class PaymentRequest {

    //支付ID
    private String payId;

    //商品描述
    private String goodsDesc;

    //总金额
    private BigDecimal totalFee;

    //人民币金额
    private BigDecimal rmbFee;

    private TradeInformation tradeInformation;



}
