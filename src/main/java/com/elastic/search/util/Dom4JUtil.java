package com.elastic.search.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.util.StringTokenizer;

/**
 * @author yanzhiheng on 2018/9/21.
 */
public class Dom4JUtil {
    private static final Logger logger = LoggerFactory.getLogger(Dom4JUtil.class);

    /**
     * 解析XML并且防止XEE攻击, 详情参考https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=23_5
     * @param text
     * @return
     * @throws DocumentException
     */
    public static Document parseTextWithoutXEE(String text) throws DocumentException {
        Document result = null;
        SAXReader reader = new SAXReader();
        String FEATURE = null;
        try {
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            reader.setFeature(FEATURE, true);
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            reader.setFeature(FEATURE, false);
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            reader.setFeature(FEATURE, false);
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            reader.setFeature(FEATURE, false);
        } catch (SAXException e) {
            logger.warn("A DOCTYPE was passed into the XML document");
        }

        String encoding = getEncoding(text);
        InputSource source = new InputSource(new StringReader(text));
        source.setEncoding(encoding);
        result = reader.read(source);
        if (result.getXMLEncoding() == null) {
            result.setXMLEncoding(encoding);
        }
        return result;
    }
    private static String getEncoding(String text) {
        String result = null;
        String xml = text.trim();
        if (xml.startsWith("<?xml")) {
            int end = xml.indexOf("?>");
            String sub = xml.substring(0, end);
            StringTokenizer tokens = new StringTokenizer(sub, " =\"\'");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if ("encoding".equals(token)) {
                    if (tokens.hasMoreTokens()) {
                        result = tokens.nextToken();
                    }
                    break;
                }
            }
        }
        return result;
    }
}
