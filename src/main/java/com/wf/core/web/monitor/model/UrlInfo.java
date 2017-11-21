package com.wf.core.web.monitor.model;

import java.util.Date;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class UrlInfo {
    private String osCode;
    private String url;
    private Double elapsedTime;
    private Date createTime;

    public String getOsCode() {
        return osCode;
    }

    public void setOsCode(String osCode) {
        this.osCode = osCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
