package com.elastic.search.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 12:55
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
@Data
public class TradeInformation {

    //业务类型
    private String businessType;

    //商品信息
    private List<GoodsInfo> goodsInfo;

    //商品总数量
    private String totalQuantity;

    private String otherBusinessType;

}
