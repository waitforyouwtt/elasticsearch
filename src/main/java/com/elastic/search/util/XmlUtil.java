package com.elastic.search.util;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.util.StringUtils;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author: 凤凰[小哥哥]
 * @Date: 2020/4/20 18:26
 * @Email: 15290810931@163.com
 */
@Slf4j
public class XmlUtil {


    /**
     * map 转 string
     */
    public static String getStringByMapIncludeNull(Map<String, Object> map) {
        StringBuffer content = new StringBuffer();
        for (Map.Entry<String, Object> entity : map.entrySet()) {
            if (StringUtils.isEmpty(entity.getKey())) {
                continue;
            }
            content.append("&").append(entity.getKey()).append("=").append(entity.getValue());
        }
        return content.substring(1);
    }

    /**
     * xml 转 map
     */
    public static SortedMap<String, String> xmlToMap(String content) throws DocumentException {
        SortedMap<String, String> xmlMap = new TreeMap<String, String>();
        Document document = Dom4JUtil.parseTextWithoutXEE(content);
        List<Element> els = document.getRootElement().elements();
        for (Element el : els) {
            xmlMap.put(el.getName().toLowerCase(), el.getTextTrim());
        }
        return xmlMap;
    }

    /**
     * map 转 xml
     */
    public static String getXmlByMap(Map<String, Object> params) {
        StringBuilder sBuild = new StringBuilder(params.size() * 10);
        sBuild.append("<xml>");
        for (Map.Entry<String, Object> param : params.entrySet()) {
            String paramTemplete = "<key><![CDATA[value]]></key>"; // 使用java string池 不会有空间损耗
            if (null != param.getValue()) {
                String key = param.getKey();
                String value = param.getValue().toString();
                sBuild.append(paramTemplete.replaceAll("key", key).replace("value", value));
            }
        }
        sBuild.append("</xml>");
        return sBuild.toString();
    }

    public static Map<String, Object> beanToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if (StringUtils.isEmpty(key) || value ==null) {
                        continue;
                    }
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            log.info("bean to map exception:{}",e);
        }
        return map;
    }

    /**
     * javaBean转XML, default UTF-8
     *
     * @param obj
     * @return
     */
    public static String convertToXml(Object obj) {
        return convertToXml(obj, "UTF-8");
    }

    /**
     * javaBean转XML, custom encode
     *
     * @param obj
     * @param encoding
     * @return
     */
    public static String convertToXml(Object obj, String encoding) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            result = writer.toString();
        } catch (JAXBException e) {
            log.info("bean to xml exception:{}",e);
        }

        return result;
    }


}
