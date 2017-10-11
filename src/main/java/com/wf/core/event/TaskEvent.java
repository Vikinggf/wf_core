//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wf.core.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class TaskEvent {
    private Long userId;
    private Date createTime;
    private Long channelId;
    private String[] params;
    private Long bizId;

    public TaskEvent() {
    }

    public Long getBizId() {
        return this.bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String[] getParams() {
        return this.params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public abstract Map<String, Object> toMap();

    public abstract String getMqQueue();

    protected Map<String, Object> doToMap() {
        Map<String, Object> map = new HashMap();
        map.put("userId", this.userId);
        map.put("createTime", this.createTime);
        map.put("params", this.params);
        map.put("bizId", this.bizId);
        return map;
    }
}
