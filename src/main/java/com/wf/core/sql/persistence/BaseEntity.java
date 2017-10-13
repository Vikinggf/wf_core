/**
 * http://www.lbanma.com
 */
package com.wf.core.sql.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wf.core.sql.supcan.SupCol;
import com.wf.core.sql.supcan.SupTreeList;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

/**
 * Entity支持类
 * @author www.lbanma.com
 * @version 2014-05-16
 */
@SupTreeList
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 实体编号（唯一标识）
	 */
	protected Long id;

	public BaseEntity() {
		
	}
	
	public BaseEntity(Long id) {
		this();
		this.id = id;
	}

	@SupCol(isUnique="true", isHide="true")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
    /**
	 * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
	 * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     * @return
     */
	@JsonIgnore
	public boolean isNewRecord() {
        return id == null;
    }
	
    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        BaseEntity that = (BaseEntity) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
    
	/**
	 * 删除标记（0：正常；1：删除；2：审核；）
	 */
	public static final int DEL_FLAG_NORMAL = 0;
	public static final int DEL_FLAG_DELETE = 1;
//	public static final int DEL_FLAG_AUDIT = 2;
	
}
