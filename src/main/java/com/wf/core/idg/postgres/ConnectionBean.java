package com.wf.core.idg.postgres;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * 连接对象
 * @author Fe
 * @version 2016年1月7日 <strong>1.0</strong>
 */
public class ConnectionBean {
	private Config config;
	private String url;
	private Properties info;
	private boolean use;
	private boolean notConn;
	private long create;
	private Connection connection;
	private Statement statement;
	private ConnectionBean next;
	private int index;
	private final Object lock = new Object();
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Properties getInfo() {
		return info;
	}
	public void setInfo(Properties info) {
		this.info = info;
	}
	public boolean isUse() {
		return use;
	}
	public void setUse(boolean use) {
		this.use = use;
	}
	public boolean isNotConn() {
		return notConn;
	}
	public void setNotConn(boolean notConn) {
		this.notConn = notConn;
	}
	public long getCreate() {
		return create;
	}
	public void setCreate(long create) {
		this.create = create;
	}
	public Statement getStatement() {
		return statement;
	}
	public void setStatement(Statement statement) {
		this.statement = statement;
	}
	public ConnectionBean getNext() {
		return next;
	}
	public void setNext(ConnectionBean next) {
		this.next = next;
	}
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Object getLock() {
		return lock;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	@Override
	public String toString() {
		return "ConnectionBean [url=" + url + ", use=" + use + ", notConn=" + notConn + ", create=" + create
				+ ", index=" + index + "]";
	}
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
}
