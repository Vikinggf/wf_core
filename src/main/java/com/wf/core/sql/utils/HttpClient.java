package com.wf.core.sql.utils;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.text.SimpleDateFormat;

public abstract class HttpClient implements InitializingBean {
	private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
	private ObjectMapper mapper;
	private String url;

	/**
	 * get请求
	 * @param uri
	 * @param responseClass
	 * @return
	 */
	public <Response> Response get(String uri, Class<Response> responseClass) {
		return request(uri, null, responseClass, HttpMethod.GET);
	}

	/**
	 * get请求
	 * @param uri
	 * @param responseClass
	 * @param params
	 * @return
	 */
	public <Response> Response get(String uri, Class<Response> responseClass, Object...params) {
		for (Object param : params)
			uri += "/" + (param == null ? null : param.toString());
		return request(uri, null, responseClass, HttpMethod.GET);
	}

	/**
	 * post请求
	 * @param uri
	 * @param request
	 * @return
	 */
	public <Request> void post(String uri, Request request) {
		request(uri, request, null, HttpMethod.POST);
	}

	/**
	 * post请求
	 * @param uri
	 * @param request
	 * @param responseClass
	 * @return
	 */
	public <Request, Response> Response post(String uri, Request request, Class<Response> responseClass) {
		return request(uri, request, responseClass, HttpMethod.POST);
	}

	/**
	 * 请求
	 * @param uri
	 * @param request
	 * @param responseClass
	 * @param method
	 * @return
	 */
	public <Request, Response> Response request(String uri, Request request, Class<Response> responseClass, HttpMethod method) {
		HttpRequestWithBody http = new HttpRequestWithBody(method, uri);
		if (request != null) {
			try {
				http.header("Content-Type", "application/json");
				http.body(mapper.writeValueAsString(request));
			} catch (JsonProcessingException e) {
				throw new HttpClientException("请求转换成json异常", e);
			} catch (IOException e) {
				throw new HttpClientException("请求转换成json异常", e);
			}
		}
		HttpResponse<String> response;
		try {
			response = http.asString();
		} catch (UnirestException e) {
			throw new HttpClientException("请求异常", e);
		}
		if (response.getStatus() == 200) {
			String json = response.getBody();
			if (json == null || json.isEmpty())
				return null;
			else {
				try {
					return mapper.readValue(json, responseClass);
				} catch (IOException e) {
					throw new HttpClientException("响应转换成json异常", e);
				}
			}
		} else {
			logger.error("请求内部接口失败：{}",response.getBody());
			throw new HttpClientException("请求处理失败");
		}
	}

	public HttpClient() {
		mapper = new ObjectMapper();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mapper.setDateFormat(fmt);
		mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static class HttpClientException extends RuntimeException {
		private static final long serialVersionUID = -8767489679043116606L;
		public HttpClientException(String message) {
			super(message);
		}
		public HttpClientException(String message, Throwable e) {
			super(message, e);
		}
	}
	
	/**
	 * 获取金额
	 * @param url
	 * @return
	 */
	protected final String url(String url) {
		return this.url + getName() + url;
	}
	
	protected abstract String getName();

	@Override
	public void afterPropertiesSet() throws Exception {
		url = Global.getConfig("platform.uri");
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}
}
