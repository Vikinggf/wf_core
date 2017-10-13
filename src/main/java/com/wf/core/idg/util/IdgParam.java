package com.wf.core.idg.util;

/**
 * idg
 * @author Fe 2016年3月8日
 */
public class IdgParam {
	final String name;
	final boolean intType;
	final String sql;
	final boolean[] notCheck;
	public IdgParam(String name, boolean intType, String sql, boolean[] notCheck) {
		this.name = name;
		this.intType = intType;
		this.sql = sql;
		this.notCheck = notCheck;
	}

	public String getName() {
		return name;
	}

	public boolean isIntType() {
		return intType;
	}

	public String getSql() {
		return sql;
	}

	public boolean[] getNotCheck() {
		return notCheck;
	}
}
