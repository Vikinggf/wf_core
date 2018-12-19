package com.wf.core.web.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.poi.ss.formula.functions.T;

/**
 * 类名称：ResponseBean
 * 类描述：统一响应结构类
 * 开发人：朱水平【Tank】
 * 创建时间：2018/12/18.14:32
 * 修改备注：
 *
 * @version 1.0.0
 */
@ApiModel(value = "ResponseBean", description = "响应信息体")
public class ResponseBean<T> extends BaseRspBean {

    @ApiModelProperty(value = "响应数据体")
    private T data;
    @ApiModelProperty(value = "错误信息")
    private String message;

    /**
     * 指定错误代号，及错误信息  建议使用。
     *
     * @param code    错误代号
     * @param message 错误信息
     */
    public ResponseBean(int code, String message) {
        super(code);
        this.message = message;
    }

    /**
     * 兼容以前的历史代码 固定一个错误码。
     *
     * @param message 错误信息
     */
    public ResponseBean(String message) {
        super(101);
        this.message = message;
    }


    /**
     * 可以定制对应响应码及响应数据
     *
     * @param code 成功响应码
     * @param data 响应数据
     */
    public ResponseBean(int code, T data) {
        super(code);
        this.data = data;
    }

    /**
     * 固定响应码为200
     *
     * @param data 响应数据
     */
    public ResponseBean(T data) {
        super(200);
        this.data = data;

    }

    /**
     * 固定响应码为200 无响应数据
     */
    public ResponseBean() {
        super(200);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
