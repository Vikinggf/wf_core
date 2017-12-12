package com.wf.core.extjs.data;

import java.util.Collections;
import java.util.List;

/**
 * extJs DataGrid控件的数据模型
 * @author zwf
 */
public class DataGrid<T> {
	private List<T> data;

	private Boolean success;

	private Long total;

	public DataGrid() {
		this(Collections.EMPTY_LIST, 0L);
	}

	public DataGrid(List<T> data, Long total) {
		this(data, total, true);
	}

	public DataGrid(List<T> data, Long total, Boolean success) {
		this.data = data;
		this.total = total;
		this.success = success;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}
