package com.wf.core.web.base;

import com.alibaba.fastjson.JSONObject;
import com.wf.core.extjs.data.DataGrid;
import com.wf.core.log.LogExceptionStackTrace;
import com.wf.core.persistence.Page;
import com.wf.core.utils.RequestUtils;
import com.wf.core.utils.TraceIdUtils;
import com.wf.core.utils.type.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理extjs客户端请求的控制器支持类
 *
 * @author zwf
 */
public abstract class ExtJsController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获取Http请求中的json字符串，此方法只能被调用一次
	 *
	 * @return
	 */
	protected String getRequestJsonString() {
		HttpServletRequest request = RequestUtils.getRequest();
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("read http request body error, ex={}, traceId={}", LogExceptionStackTrace.erroStackTrace(e), TraceIdUtils.getTraceId());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 获取Http请求中的json字符串并转化为json对象,此方法只能被调用一次
	 *
	 * @return
	 */
	protected JSONObject getRequestJson() {
		return JSONObject.parseObject(getRequestJsonString());
	}

	/**
	 * 将extjs客户端请求的json格式数据转化为Page对象
	 *
	 * @param clazz 实体类
	 * @param <T>
	 * @return
	 */
	protected <T> Page<T> getPage(Class<T> clazz) {
		return getPage(getRequestJsonString(), clazz);
	}

	/**
	 * json字符串转化为Page对象
	 *
	 * @param jsonStr json字符串
	 * @param clazz   实体类
	 * @param <T>
	 * @return
	 */
	protected <T> Page<T> getPage(String jsonStr, Class<T> clazz) {
		JSONObject json = JSONObject.parseObject(jsonStr);
		T t = JSONObject.parseObject(json.getString("data"), clazz);
		long start = json.getLongValue("start");
		long length = json.getLongValue("limit");
		if (length == 0L) {
			length = 20L;
		}
		return new Page<>(t, start, length);
	}

	/**
	 * 将extjs form表单提交的json数据转换为实体类
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	protected <T> T getForm(Class<T> clazz) {
		return JSONObject.parseObject(getRequestJsonString(), clazz);
	}

	/**
	 * 将Page对象转换为extjs客户端能识别的DataGrid
	 *
	 * @param page
	 * @param <T>
	 * @return
	 */
	protected <T> DataGrid<T> dataGrid(Page<T> page) {
		if (page == null) {
			return new DataGrid<>();
		}
		return new DataGrid<>(page.getData(), page.getiTotalDisplayRecords());
	}


	private Map<Object, Object> response(boolean success, Object data) {
		Map<Object, Object> result = new HashMap<>();
		result.put("success", success);
		result.put("data", data);
		return result;
	}

	/**
	 * 后台处理成功后返回给extjs客户端
	 *
	 * @param data 后台返回的数据
	 * @return
	 */
	protected Map<Object, Object> success(Object data) {
		return response(true, data);
	}

	/**
	 * 后台处理成功返回消息字符串给extjs客户端
	 * @param message 消息字符串
	 * @return
	 */
	protected Map<Object, Object> success(String message) {
		return success(MapUtils.toMap("msg", message));
	}

	/**
	 * 后台返回成功给extjs客户端
	 *
	 * @return
	 */
	protected Map<Object, Object> success() {
		return success(null);
	}

	/**
	 * 后台处理失败返回给extjs客户端
	 * @param data 后台返回的数据
	 * @return
	 */
	protected Map<Object, Object> error(Object data) {
		return response(false, data);
	}

	/**
	 * 后台处理失败返回消息字符串给extjs客户端
	 * @param message 消息字符串
	 * @return
	 */
	protected Map<Object, Object> error(String message) {
		return error(MapUtils.toMap("msg", message));
	}

	/**
	 * 后台返回失败给extjs客户端
	 * @return
	 */
	protected Map<Object, Object> error() {
		return error(null);
	}

	/**
	 * 捕捉异常，并转换为json格式数据返回给前端extjs
	 * @param response
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public Object handleException(HttpServletResponse response, Exception e) {
		logger.error("系统异常 ex={}, traceId={}", LogExceptionStackTrace.erroStackTrace(e), TraceIdUtils.getTraceId());
		return error(e.getMessage());
	}
}
