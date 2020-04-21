package com.elastic.search;

import com.elastic.search.entity.GoodsInfo;
import com.elastic.search.entity.PaymentRequest;
import com.elastic.search.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class ElasticsearchApplicationTests {


    @Test
    void contextLoads() {
        List<GoodsInfo> goodsInfoList = Arrays.asList(
             /*   new GoodsInfo( "娃哈哈",1 ,"娃哈哈"),
                new GoodsInfo(  "小白兔",2,"小白兔"),*/
                new GoodsInfo( "乖小孩",5 ,"乖小孩"));

      /*  IntSummaryStatistics collect = goodsInfoList.stream().collect( Collectors.summarizingInt( GoodsInfo::getProductSkuQuantity ) );
        long sum = collect.getSum();*/

        /*StringBuffer sb = new StringBuffer(  );
        for (int i=0;i< goodsInfoList.size();i++) {
            GoodsInfo goodsInfo = goodsInfoList.get( i );
            sb.append( goodsInfo.getProductSkuName() );
            sb.append( "^" );
            sb.append( goodsInfo.getProductSkuQuantity());
            if (i< goodsInfoList.size() -1){
                sb.append( "|" );
            }
        }*/
        String body = body(goodsInfoList);
        log.info("得到的结果：{}",body);

    }
    private String body(List<GoodsInfo> goodsInfos){
        return  goodsInfos.stream().map(GoodsInfo::getProductSkuDesc).collect(Collectors.joining("|"));
    }

    @Test
    public void rsaTest(){
//        Map<String, Object> map = new HashMap<String, Object>();
//        map = RsaTool.init();
//        System.out.println("公钥："+RsaTool.getPublicKey(map));
//        System.out.println("私钥："+RsaTool.getPrivateKey(map));
            //由前四行代码获得公、私密钥
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLNbmKl9/gLn7Bef/xtUkshC1WyrLZLRpXCcFYR1gQi0isWsZBTicC4efBOkkNG3r+1ue0gvtuU/tjREFGf4Y7HaKHGb5tNCOlMNeNjM5YLRwLFqrUSsQyD4rj4eua1ltearr24R0HilnTvnQm6Z/UY0s21vdOUFQBPY0GNAa+0wIDAQAB";
            String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIs1uYqX3+AufsF5//G1SSyELVbKstktGlcJwVhHWBCLSKxaxkFOJwLh58E6SQ0bev7W57SC+25T+2NEQUZ/hjsdoocZvm00I6Uw142MzlgtHAsWqtRKxDIPiuPh65rWW15quvbhHQeKWdO+dCbpn9RjSzbW905QVAE9jQY0Br7TAgMBAAECgYBcYhbzpr5no/Nyqmf0G/6nkEAWbQYrogbs5AhvcUk8EXL1DnirNhYlj42hafC4xhflrvCtlo8NNKaLxewbwN1uuzG8A2jd+ROEXlx5HDh2ZluhtHzL/SmNcJXo684xAl2pCNVBjDcW48PcIBijke/sTVHTDsDCukLKDPUOM/mKIQJBAL96k4+jBscazsJiuZ6C3RFDVtRRDpf1dMgLgxcx63bAXkA2Arau0J49IAYmSVJoDXqDoJKWdXJVh9vHSkhN/48CQQC6Hk1/G0Y0nOylf6NOp0oMgc0A+etnwxHKqwtctPKjEYcJx2fzALzTtCoySLYXX7gLnPIQXpQBTUysG5skBKp9AkEAiSQm6fqu0Q4fRlRlc+VwpnufhgPkOuw/z0OHiaZkajJPjxfgC63bl2paNG1ZmJ8UAEqkSDlhNxmRa9UqG+1ZewJASaQxz6gwCCNLM1SkfjuM/hPh1JAOh9jUUleJQF5MXx9RSho/VBQnorB3vbutaOQzw0yPLtDtSPKX8sVdhkveVQJAIDsJP5X8Tey6zXTUISor7PF0TSiKdE4k0IwKoy9y8HmQ+AU8+xyr/iOt5lvaGxKlBK8N/7yCw5H4qHnJaHT+Bg==";

            String str = "你好啊，凤凰小哥哥";
            // 公钥加密，私钥解密
            String enStr1 = RSAUtil.encryptByPublicKey(str, publicKey);
            System.out.println("公钥加密后：" + enStr1);
            String deStr1 = RSAUtil.decryptByPrivateKey(enStr1, privateKey);
            System.out.println("私钥解密后：" + deStr1);
            // 私钥加密，公钥解密
            String enStr2 = RSAUtil.encryptByPrivateKey(str, privateKey);
            System.out.println("私钥加密后：" + enStr2);
            String deStr2 = RSAUtil.decryptByPublicKey(enStr2, publicKey);
            System.out.println("公钥解密后：" + deStr2);
            // 产生签名
            String sign = RSAUtil.sign(enStr2, privateKey);
            System.out.println("签名:" + sign);
            // 验证签名
            boolean status = RSAUtil.verify(enStr2, publicKey, sign);
            System.out.println("状态:" + status);
    }

}
