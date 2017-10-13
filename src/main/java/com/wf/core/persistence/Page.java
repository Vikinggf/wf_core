/**
 * http://www.lbanma.com
 */
package com.wf.core.persistence;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.subject.WebSubject;

import javax.servlet.ServletRequest;
import java.util.List;

/**
 * 分页类
 *
 * @param <T>
 * @author www.lbanma.com
 * @version 2013-7-2
 */
public class Page<T> {
    public static final String dbName = "mysql";
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final long MAX_RESULT_COUNT = 100;
    private String draw;
    private long start;
    private Long length;
    private long iTotalRecords;
    private long iTotalDisplayRecords;
    private List<T> data;
    private T p;

    public Page() {
        length = MAX_RESULT_COUNT;
    }

    public Page(T entity) {
        if (entity == null)
            throw new NullPointerException();
        try {
            ServletRequest request = ((WebSubject) SecurityUtils.getSubject()).getServletRequest();
            String sStart = request.getParameter("start");
            String sLength = request.getParameter("length");
            this.draw = request.getParameter("draw");
            if (sStart != null)
                start = Integer.parseInt(sStart);
            if (sLength != null)
                length = Long.parseLong(sLength);
            else
                length = MAX_RESULT_COUNT;
            this.p = entity;
        } catch (Exception e) {
            throw new RuntimeException("无法获取到request");
        }
    }

    public Page(T entity, long start, long length) {
        this.start = start;
        this.length = length;
        this.p = entity;
    }

    public Page<T> setData(List<T> data) {
        this.data = data;
        if (data != null)
            this.iTotalRecords = data.size();
        return this;
    }

    public Page<T> setCount(long count) {
        this.iTotalDisplayRecords = count;
        return this;
    }

    public long getMaxResults() {
        return length;
    }

    public long getFirstResult() {
        return start;
    }

    public long getPageSize() {
        return length;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public long getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public long getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(long iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public List<T> getData() {
        return data;
    }

    public T getP() {
        return p;
    }

    public void setP(T p) {
        this.p = p;
    }
}
