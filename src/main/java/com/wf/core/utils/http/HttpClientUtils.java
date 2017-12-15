package com.wf.core.utils.http;

import com.wf.core.utils.TraceIdUtils;
import com.wf.core.utils.exception.BusinessCommonException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http工具
 *
 * @author Fe 2017年1月10日
 */
public class HttpClientUtils {
    public static final String CHARSET = "utf-8";

    /**
     * post
     *
     * @param url
     * @param logger
     * @return
     */
    public static String post(String url, Logger logger) {
        return request(url, "POST", CHARSET, logger);
    }

    /**
     * get
     *
     * @param url
     * @param logger
     * @return
     */
    public static String get(String url, Logger logger) {
        return request(url, "GET", CHARSET, logger);
    }

    /**
     * 请求
     *
     * @param url
     * @param method
     * @param logger
     * @return
     */
    public static String request(String url, String method, Logger logger) {
        return request(url, method, CHARSET, logger);
    }

    /**
     * 请求
     *
     * @param url
     * @param method
     * @param charset
     * @param logger
     * @return
     */
    public static String request(String url, String method, String charset, Logger logger) {
        HttpURLConnection httpConnection = null;
        InputStream data_In = null;
        try {
            logger.info("request: " + url);
            httpConnection = (HttpURLConnection) new URL(url).openConnection();
            httpConnection.setConnectTimeout(30000);
            httpConnection.setReadTimeout(30000);
            httpConnection.setRequestMethod(method);
            data_In = httpConnection.getInputStream();
            String strRead = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(data_In, charset));
            StringBuilder result = new StringBuilder();
            while ((strRead = reader.readLine()) != null) {
                result.append(strRead).append('\n');
            }
            reader.close();
            logger.info("response: " + result.toString());
            return result.toString();
        } catch (IOException e) {
            Integer code = null;
            if (httpConnection != null) {
                try {
                    code = httpConnection.getResponseCode();
                } catch (IOException e1) {
                }
            }
            if (code != null) {
                logger.error("请求网络错误 traceId={} responseCode={} url={} ex={}",
                        TraceIdUtils.getTraceId(), code, url, ExceptionUtils.getStackTrace(e));
                throw new BusinessCommonException("请求网络错误, " + code + "：" + url, e);
            } else {
                logger.error("请求网络错误 traceId={} url={} ex={}",
                        TraceIdUtils.getTraceId(), url, ExceptionUtils.getStackTrace(e));
                throw new BusinessCommonException("请求网络错误：" + url, e);
            }
        } finally {
            if (data_In != null) {
                try {
                    data_In.close();
                } catch (IOException e) {
                }
            }
            if (httpConnection != null)
                httpConnection.disconnect();
        }
    }
}
