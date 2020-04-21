package com.elastic.search.controller;

import com.elastic.search.entity.GoodsInfo;
import com.elastic.search.entity.PaymentRequest;
import com.elastic.search.entity.TradeInformation;
import com.elastic.search.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 14:03
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
@RestController
public class PayController {

    @Autowired
    AlipayService payService ;

    @PostMapping("/pay")
    public String pay(){
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPayId( "20200418140606" );
        paymentRequest.setGoodsDesc( "哇哈哈" );
        paymentRequest.setTotalFee( new BigDecimal( 0.01 ) );
        paymentRequest.setRmbFee( new BigDecimal( 0.01 ) );

        TradeInformation information = new TradeInformation();
        List<GoodsInfo> goodsInfoList = Arrays.asList(
                new GoodsInfo( "娃哈哈",1 ,"娃哈哈"),
                new GoodsInfo(  "小白兔",2,"小白兔"),
                new GoodsInfo( "乖小孩",5,"乖小孩" ));
        information.setGoodsInfo(  goodsInfoList);
        paymentRequest.setTradeInformation( information );

        int pay = payService.pay( paymentRequest );

        return "success";
    }
}
