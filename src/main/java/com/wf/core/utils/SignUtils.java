package com.wf.core.utils;

import com.wf.core.utils.encrypt.MD5Util;
import com.wf.core.utils.type.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 签名
 *
 * @author jijie.chen
 */
public class SignUtils {

    private static Logger logger = LoggerFactory.getLogger(SignUtils.class);

    /**
     * 时间是否超时
     */
    public static boolean checkTimeStamp(String timestamp) {
        Date d = DateUtils.parseDate(timestamp,DateUtils.YYYYMMDDHHMMSS_PATTERN);
        long d2 = System.currentTimeMillis();
        long diffTimeStamp =  d2 - d.getTime();
        if (timestamp == null) {
            return false;
        } else if (timestamp.length() != 14) {
            return false;
        } else if (diffTimeStamp > 1000 * 60 * 10) {
            return false;
        }
        return true;
    }

    /**
     * 加签
     */
    public static String genSignData(Map<String, Object> data, String key) {
        logger.info(GfJsonUtil.toJSONString(data));
        if (data == null) {
            logger.error("签名的数据不能为空");
            return null;
        }
        List<String> keyList = new ArrayList<>();
        Set<Map.Entry<String, Object>> entrySet = data.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            keyList.add(entry.getKey());
        }
        Collections.sort(keyList);
        StringBuffer buffer = new StringBuffer();
        for (String keyEle : keyList) {
            buffer.append(keyEle + "=").append(data.get(keyEle)).append("&");
        }
        String sign = buffer.toString() + "key=" + key;
        logger.info("签名:" + sign);
        String encryptSign = MD5Util.getUpperMD5(sign);
        logger.info("加密签名:" + encryptSign);
        return encryptSign;
    }

    /**
     * 验签
     *
     * @return
     */
    public static boolean checkSignValid(String requestSign, String sign) {
        return requestSign.equals(sign);
    }


}
