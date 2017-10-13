package com.wf.core.persistence;

import java.util.Date;

/**
 * 数据Entity类
 *
 * @author www.lbanma.com
 * @version 2014-05-16
 */
public abstract class DataEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    protected Date createTime;    // 创建日期
    protected Date updateTime;    // 更新日期
    protected int deleteFlag;    // 删除标记（0：正常；1：删除；2：审核）

    public DataEntity() {
        super();
        this.deleteFlag = DEL_FLAG_NORMAL;
    }

    public DataEntity(Long id) {
        super(id);
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
