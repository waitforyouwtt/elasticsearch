package com.elastic.search.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/18 12:57
 * @Version: 1.0
 * @Email: 15290810931@163.com
 */
@Data
@AllArgsConstructor
public class GoodsInfo {

    //商品sku name
    private String productSkuName;

    //商品sku 数量
    private Integer productSkuQuantity;

    //商品描述
    private String productSkuDesc;

}
