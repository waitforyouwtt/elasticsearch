package com.elastic.search.service.impl;

import com.elastic.search.constant.AliPayConstant;
import com.elastic.search.entity.GoodsInfo;
import com.elastic.search.entity.PayResponse;
import com.elastic.search.entity.PaymentRequest;
import com.elastic.search.service.AlipayService;
import com.elastic.search.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 12:24
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {

    @Value( "${partner}" )
    private String partner;
    @Value( "${private_yh_key}" )
    private String privateYhkey;
    @Value( "${refer_url}" )
    private String referUrl;
    @Value( "${seller_id}" )
    private String sellerId;
    @Value( "${secondary_merchant_id}" )
    private String secondaryMerchantId;
    @Value( "${secondary_merchant_name}" )
    private String secondaryMerchantName;


    RestTemplate restTemplate = new RestTemplate();

    @Override
    public int pay(PaymentRequest paymentRequest) {
        Map<String,String> map = new TreeMap<>(  );
        map.put( "service",AliPayConstant.ALIPAY_PAY_SERVICE );
        map.put( "partner","partner" );
        map.put( "_input_charset",AliPayConstant.INPUT_CHARSET );
        map.put( "sign_type", AliPayConstant.SIGN_TYPE );
        //异步通知接口以后再填写
        map.put( "notify_url","");
        map.put( "refer_url",referUrl );
        map.put( "appenv","" );
        map.put( "out_trade_no",paymentRequest.getPayId());
        map.put( "subject",paymentRequest.getGoodsDesc() );
        map.put( "payment_type",AliPayConstant.PAYMENT_TYPE );
        map.put( "seller_id", sellerId);
        map.put( "total_fee",paymentRequest.getTotalFee().toString() );
        map.put( "currency",AliPayConstant.CURRENCY );
        map.put( "rmb_fee",paymentRequest.getRmbFee().toString() );
        map.put( "body",body(paymentRequest));
        map.put( "forex_biz",AliPayConstant.FOREX_BIZ );
        map.put( "it_b_pay","" );
        map.put("secondary_merchant_id",secondaryMerchantId);
        map.put("secondary_merchant_name",secondaryMerchantName);
        map.put("secondary_merchant_industry","4");
        map.put( "product_code",AliPayConstant.PRODUCT_CODE );
        map.put( "trade_information",tradeInformation(paymentRequest) );
        map.put( "sign", RSAUtil.sign( map.toString(),privateYhkey ) );
        String url = AliPayConstant.GET_EWAY +"?"+ AliPayConstant.ALIPAY_PAY_SERVICE ;
        PayResponse forObject = restTemplate.getForObject( url, PayResponse.class );
        log.info( "请求支付宝跨境购返回的结果：{}",forObject );
        return 0;
    }


    private String tradeInformation(PaymentRequest paymentRequest){
        SortedMap<String,String> map = new TreeMap<>(  );
        map.put( "business_type",AliPayConstant.BUSINESS_TYPE );
        map.put( "total_quantity", totalQuantity(paymentRequest.getTradeInformation().getGoodsInfo()));
        map.put( "goods_info",goodsInfo(paymentRequest.getTradeInformation().getGoodsInfo()) );
        return map.toString();
    }

    private String totalQuantity(List<GoodsInfo> goodsInfoList){
        return String.valueOf( goodsInfoList.stream().collect( Collectors.summarizingInt( GoodsInfo::getProductSkuQuantity ) ).getSum() );
    }
    private String goodsInfo(List<GoodsInfo> goodsInfoList){
        StringBuffer sb = new StringBuffer(  );
        for (int i=0;i< goodsInfoList.size();i++) {
            GoodsInfo goodsInfo = goodsInfoList.get( i );
            sb.append( goodsInfo.getProductSkuName() );
            sb.append( "^" );
            sb.append( goodsInfo.getProductSkuQuantity());
            if (i< goodsInfoList.size() -1){
                sb.append( "|" );
            }
        }
        return sb.toString();
    }

    private String body(PaymentRequest paymentRequest){
       return  paymentRequest.getTradeInformation().getGoodsInfo().stream().map(GoodsInfo::getProductSkuDesc).collect(Collectors.joining("|"));
    }
}
