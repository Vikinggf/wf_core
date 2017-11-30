package com.wf.core.idg.postgres;

import javax.sql.XAConnection;
import javax.transaction.xa.Xid;
import java.sql.Connection;

/**
 * config bean
 * @author Fe
 * @version 2015年12月30日 <strong>1.0</strong>
 */
public class ConfigBean {
	private Config config;
	private XAConnection xaconn;
	private Connection conn;
	private String database;
	private Xid xid;
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	public XAConnection getXaconn() {
		return xaconn;
	}
	public void setXaconn(XAConnection xaconn) {
		this.xaconn = xaconn;
	}
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public Xid getXid() {
		return xid;
	}
	public void setXid(Xid xid) {
		this.xid = xid;
	}
	@Override
	public String toString() {
		return "ConfigBean [config=" + config + ", database=" + database + ", xid=" + xid + "]";
	}
}
