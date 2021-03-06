/**
 * http://www.lbanma.com
 */
package com.wf.core.persistence;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wf.core.utils.Reflections;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 数据Entity类
 *
 * @author www.lbanma.com
 * @version 2014-05-16
 */
public abstract class TreeEntity<T> extends DataEntity {

    private static final long serialVersionUID = 1L;

    protected T parent;    // 父级编号
    protected String parentIds; // 所有父级编号
    protected String name;    // 机构名称
    protected Integer sort;        // 排序

    public TreeEntity() {
        super();
        this.sort = 30;
    }

    public TreeEntity(Long id) {
        super(id);
    }

    /**
     * 父对象，只能通过子类实现，父类实现mybatis无法读取
     *
     * @return
     */
    @JsonBackReference
    @NotNull
    public abstract T getParent();

    /**
     * 父对象，只能通过子类实现，父类实现mybatis无法读取
     *
     * @return
     */
    public abstract void setParent(T parent);

    @Length(min = 1, max = 2000)
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Length(min = 1, max = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Long getParentId() {
        Long id = null;
        if (parent != null) {
            id = (Long) Reflections.getFieldValue(parent, "id");
        }
        return id == null ? 0 : id;
    }

}
