package com.wf.core.idg.postgres;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.wf.core.idg.IDGeneratorException;
import org.postgresql.xa.PGXADataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wf.core.idg.postgres.SQLConfig.*;


/**
 * MysqlIDGeneratorFactory
 * @author Fe
 * @version 2015年12月29日 <strong>1.0</strong>
 */
public class PostgresIDGeneratorFactory {
	
	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static final long CONNECTION_ACTIVE_TIME = 3 * 60 * 60 * 1000;
	private static final long CONNECTION_ERROR_REDO_TIME = 3 * 60 * 1000;
	private static final long CONNECTION_MAX_ACTIVE_TIME = CONNECTION_ACTIVE_TIME * 2;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Object LOCK = new Object(), LOCK2 = new Object();
	private Set<Config> configs;
	private String propertiesConfig;
	private ConnectionBean currentConnection;
	private int totalConnection;
	private int errorConnection;

	public String getPropertiesConfig() {
		return propertiesConfig;
	}

	public void setPropertiesConfig(String propertiesConfig) {
		this.propertiesConfig = propertiesConfig;
		if (propertiesConfig == null || propertiesConfig.length() == 0)
			throw new IDGeneratorException("idg.configs属性未在application.properties中配置");
		//解析prop
		String[] databaseConfigs = propertiesConfig.split(";");
		configs = new HashSet<Config>();
		for (String databaseConfig : databaseConfigs) {
			databaseConfig = databaseConfig.trim();
			if (databaseConfig.length() == 0)
				continue;
			Config config = new Config();
			String[] keyValues = databaseConfig.split(",");
			for (String kv : keyValues) {
				String[] kvs = kv.split(":");
				if (kvs.length != 2)
					throw new IDGeneratorException("错误的idg.configs配置：" + kv);
				if ("host".equals(kvs[0]))
					config.setHost(kvs[1]);
				else if ("username".equals(kvs[0]))
					config.setUsername(kvs[1]);
				else if ("password".equals(kvs[0]))
					config.setPassword(kvs[1]);
				else if ("db".equals(kvs[0]))
					config.setDatabase(kvs[1]);
				else if ("port".equals(kvs[0]))
					config.setPort(Integer.parseInt(kvs[1]));
				else if ("cache".equals(kvs[0]))
					config.setCache(Integer.parseInt(kvs[1]));
				else if ("start".equals(kvs[0]))
					config.setStart(Integer.parseInt(kvs[1]));
			}
			configs.add(config);
		}
		this.initConnection();
	}

	private void initConnection() {
		ConnectionBean next = null;
		for (Config config : configs) {
			String database = config.getDatabase();
			ConnectionBean bean = new ConnectionBean();
			bean.setUrl(PostgresUtil.getUrl(config, database));
			Properties info = new Properties();
			info.setProperty("user", config.getUsername());
			info.setProperty("password", config.getPassword());
			info.setProperty("allowMultiQueries", "true");
			info.setProperty("characterEncoding", "UTF-8");
			bean.setConfig(config);
			bean.setInfo(info);
			bean.setNext(next);
			bean.setNotConn(true);
			bean.setIndex(totalConnection);
			next = bean;
			totalConnection++;
			if (currentConnection == null)
				currentConnection = bean;
		}
		currentConnection.setNext(next);
		errorConnection = totalConnection;
		getConnectionBean();
	}

	/**
	 * 获取一个id
	 */
	public long nextId(IdgParam idg) {
		while (true) {
			for (int i = 0; i < totalConnection; i++) {
				ConnectionBean bean;
				synchronized (LOCK2) {
					do {									//get到一个未使用的bean
						bean = getConnectionBean();
					} while (bean.isUse());
					bean.setUse(true);
				}
				Long id;
				synchronized (bean.getLock()) {
					id = nextId0(bean, idg);
				}
				bean.setUse(false);
				if (id != null)
					return id;
			}
			if (totalConnection == errorConnection) {
				reConnection(getConnectionBean(), System.currentTimeMillis());
				throw new IDGeneratorException("所有连接均出现错误，idg无法使用");
			}
		}
	}

	/**
	 * 获取一个id的实现
	 * @param bean
	 * @param idg
	 * @return
	 */
	private Long nextId0(ConnectionBean bean, IdgParam idg) {
		long current = System.currentTimeMillis();
		if (current > bean.getCreate() + CONNECTION_MAX_ACTIVE_TIME) {
			if (!reConnection(bean, current))
				return null;
		}
		if (bean.isNotConn()) {			//连接错误、超时、未初始化等
			if (!reConnection(bean, current))
				return null;
		}
		Long id;
		try {
			if (idg.notCheck[bean.getIndex()]) {
				checkIdgRecord(bean, idg.name);
				idg.notCheck[bean.getIndex()] = false;
			}
			ResultSet rs = bean.getStatement().executeQuery(idg.sql);
			if (rs.next())
				id = rs.getLong(1);
			else
				id = null;
			rs.close();
		} catch (Exception e) {
			logger.error("idg生成下一个id时发生异常", e);
			try {
				bean.getStatement().close();
			} catch (SQLException e1) {}
			try {
				bean.getConnection().close();
			} catch (SQLException e1) {}
			bean.setNotConn(true);
			bean.setConnection(null);
			bean.setStatement(null);
			synchronized (LOCK) {
				errorConnection++;
			}
			return null;
		}
		if (id != null) {
			if (current > bean.getCreate() + CONNECTION_ACTIVE_TIME)	//当前使用超时
				reConnection(bean, current);
		}
		return id;
	}

	/**
	 * 检查idg记录
	 * @param bean
	 * @param name
	 */
	private void checkIdgRecord(ConnectionBean bean, String name) throws SQLException {
		//验证记录是否存在且步长正确
		ResultSet rs = bean.getStatement().executeQuery(parseSQL(idgStep, name));
		if (rs.next()) {		//记录存在
			int step = rs.getInt(1);
			if (step != totalConnection) {
				throw new IDGeneratorException(name + "步长错误，当前使用的步长应该为" + totalConnection + "，但是数据库实际步长为" + step + 
						"，请调用MysqlIDGeneratorFactory.initIDGenerator()重置步长。");
			}
		} else {				//记录不存在
			int index = bean.getIndex();
			if (index != 0)
				index = 0 - bean.getIndex();
			index += totalConnection;
			bean.getStatement().execute(parseSQL(idgCreate, name, totalConnection + "", index + bean.getConfig().getStart() + "", bean.getConfig().getCache() + ""));
			logger.info("初始化" + name + ":current=" + index + ",step=" + totalConnection);
		}
		rs.close();
	}

	/**
	 * 重新连接
	 * @param bean
	 * @return 是否重连成功
	 */
	private boolean reConnection(ConnectionBean bean, long current) {
		if (current < bean.getCreate() + CONNECTION_ERROR_REDO_TIME)
			return false;
		bean.setCreate(current);
		try {
			Connection connection = DriverManager.getConnection(bean.getUrl(), bean.getInfo());
			Statement statement = connection.createStatement();
			bean.setNotConn(false);
			bean.setStatement(statement);
			bean.setConnection(connection);
			synchronized (LOCK) {
				errorConnection--;
			}
			return true;
		} catch (SQLException e) {
			logger.error("获取连接失败", e);
			return false;
		}
	}

	/**
	 * 获取一个连接
	 * @return
	 */
	private ConnectionBean getConnectionBean() {
		ConnectionBean current = currentConnection;
		currentConnection = currentConnection.getNext();
		return current;
	}

	/**
	 * 初始化idg
	 */
	public void initIDGenerator() {
		logger.warn("开始执行初始化...");
		logger.info("开始检查数据结构...");
		this.initDataStructures();
		logger.info("数据结构检查完成!");
		logger.info("开始开启连接...");
		List<ConfigBean> connList = this.getConnection();
		logger.info("连接已经开启!");
		logger.info("获取所有需要确认的ID...");
		Set<String> ids = this.getAllIds(connList);
		logger.debug("所有的id对象：" + ids);
		logger.info("获取所有需要确认的ID完成!");
		logger.info("开始验证id...");
		for (String id : ids)
			checkId(connList, id);
		logger.info("验证id完成!");
		this.close(connList);
		logger.warn("初始化完成!");
	}

	/**
	 * 验证id
	 * @param connList
	 * @param name
	 */
	private void checkId(List<ConfigBean> connList, String name) {
		try {
			//1.开始事务
			for (ConfigBean bean : connList) {
				bean.setXid(UUIDXid.getInstance());
				Connection conn = bean.getXaconn().getConnection();
				bean.getXaconn().getXAResource().start(bean.getXid(), XAResource.TMNOFLAGS);
				if (conn.getAutoCommit())
					conn.setAutoCommit(false);
				bean.setConn(conn);
			}
			//2.查询出最大值
			boolean stepOk = true;
			int count = 0;
			int step = -1;
			long startValue = 0;
			for (ConfigBean bean : connList) {
				Statement statement = bean.getConn().createStatement();
				count++;
				long value = -1;
				ResultSet rs = statement.executeQuery(parseSQL(idgExist, name));
				if (!rs.next()) {
					stepOk = false;
					rs.close();
				} else {
					rs.close();
					rs = statement.executeQuery(parseSQL(idgCurrent, name));
					if (rs.next()) {								//存在子元素，首先确认value的值，再处理标记位
						value = rs.getLong(1);
						if (stepOk) {								//stepOk为false，不需要计算步长是否正确
							int currentStep = rs.getInt(2);
							if (step == -1)							//步长为-1，是未初始化
								step = currentStep;
							else {									//步长不为-1，需要比较步长是否相同
								if (step != currentStep)			//步长不等，需要重新计算
									stepOk = false;
							}
						}
					} else {										//不存在，直接步长不等
						if (stepOk)
							stepOk = false;
					}
					if (value > startValue)
						startValue = value;

				}
				statement.close();
			}
			if (stepOk) {
				for (ConfigBean bean : connList) {
					bean.getXaconn().getXAResource().end(bean.getXid(), XAResource.TMSUCCESS);
					if (XAResource.XA_OK != bean.getXaconn().getXAResource().prepare(bean.getXid()))
						throw new XAException("准备状态不OK，操作取消。");
				}
				for (ConfigBean bean : connList) {
					bean.getXaconn().getXAResource().rollback(bean.getXid());
					bean.setXid(null);
				}
				logger.info(name + ":所有步长都相同，不需要重置，已经忽略。");
			} else {
				//3.更新值
				int index = connList.get(0).getConfig().getStart();
				for (ConfigBean bean : connList) {
					Statement statement = bean.getConn().createStatement();
					ResultSet rs = statement.executeQuery(parseSQL(idgExist, name));
					if (rs.next())
						statement.execute(parseSQL(idgDelete, name));
					rs.close();
					statement.execute(parseSQL(idgCreate, name, count + "", index++ + startValue + "", bean.getConfig().getCache() + ""));
					statement.close();
				}
				//4.提交事务
				for (ConfigBean bean : connList) {
					bean.getXaconn().getXAResource().end(bean.getXid(), XAResource.TMSUCCESS);
					if (XAResource.XA_OK != bean.getXaconn().getXAResource().prepare(bean.getXid()))
						throw new XAException("准备状态不OK，操作取消。");
				}
				for (ConfigBean bean : connList) {
					bean.getXaconn().getXAResource().commit(bean.getXid(), false);
					bean.setXid(null);
				}
				logger.warn(name + "：存在不相同的步长，已经重置。");
			}
		} catch (Exception e) {
			this.close(connList);
			throw new IDGeneratorException("事务执行失败", e);
		}
	}

	/**
	 * 获取所有所有的id
	 * @param connList
	 * @return
	 */
	private Set<String> getAllIds(List<ConfigBean> connList) {
		Set<String> ids = new HashSet<String>();
		for (ConfigBean bean : connList) {
			Connection conn;
			try {
				conn = bean.getXaconn().getConnection();
			} catch (SQLException e) {
				throw new IDGeneratorException("数据库异常", e);
			}
			try {
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(parseSQL(idgAll));
				while (rs.next()) {
					ids.add(rs.getString(1));
				}
				try { conn.close(); } catch (SQLException e) {}
			} catch (SQLException e) {
				try { conn.close(); } catch (SQLException e1) {}
				try {
					close(connList);
					throw new IDGeneratorException("执行失败：" + conn.getMetaData().getURL(), e);
				} catch (SQLException e1) {
					throw new IDGeneratorException("执行失败，并且获取详细连接信息失败", e);
				}
			}
		}
		return ids;
	}

	/**
	 * 初始化连接
	 * @return
	 */
	private List<ConfigBean> getConnection() {
		List<ConfigBean> connList = new ArrayList<ConfigBean>();
		for (Config config : configs) {
			String db = config.getDatabase();
			if (db != null) {
				PGXADataSource xaDataSource = new PGXADataSource();
				String url = PostgresUtil.getUrl(config, db);
				xaDataSource.setUrl(url);
				xaDataSource.setUser(config.getUsername());
				xaDataSource.setPassword(config.getPassword());
				XAConnection conn;
				try {
					conn = xaDataSource.getXAConnection();
				} catch (SQLException e) {
					throw new IDGeneratorException("连接数据库失败，url=" + url + ",username=" + config.getUsername() + ",password=" + config.getPassword(), e);
				}
				logger.debug(config.getHost() + ":" + config.getPort() + " " + db + " 已经连接");
				ConfigBean bean = new ConfigBean();
				bean.setConfig(config);
				bean.setXaconn(conn);
				bean.setDatabase(db);
				connList.add(bean);
			}
		}
		return connList;
	}

	/**
	 * 初始化数据结构
	 */
	private void initDataStructures() {
		final String infoDb = "postgres";
		for (Config config : configs) {
			PGXADataSource xaDataSource = new PGXADataSource();
			String url = PostgresUtil.getUrl(config, infoDb);
			xaDataSource.setUrl(url);
			xaDataSource.setUser(config.getUsername());
			xaDataSource.setPassword(config.getPassword());
			Connection conn;
			try {
				conn = xaDataSource.getConnection();
			} catch (SQLException e) {
				throw new IDGeneratorException("连接数据库失败，url=" + url + ",username=" + config.getUsername() + ",password=" + config.getPassword(), e);
			}
			logger.debug(config.getHost() + ":" + config.getPort() + " postgres 已经连接");
			if (config.getDatabase() == null || config.getDatabase().isEmpty()) {
				logger.warn(config.getHost() + ":" + config.getPort() + "不存在database，已经忽略");
				try { conn.close(); } catch (SQLException e) {}
				continue;
			}
			try {
				//验证数据库，如果不存在，将创建数据库
				Statement statement = conn.createStatement();
				String db = config.getDatabase();
				try {
					ResultSet rs = statement.executeQuery(parseSQL(databaseCheck, db));
					if (!rs.next()) {
						statement.execute(parseSQL(databaseCreate, db));
						logger.warn(config.getHost() + ":" + config.getPort() + " 创建数据库：" + db);
					}
					rs.close();
				} catch (SQLException e) {
					throw new IDGeneratorException("初始化数据库失败" + config.getHost() + ":" + config.getPort() + " " + db, e);
				}
			} catch (SQLException e) {
				throw new IDGeneratorException("初始化数据库失败" + config.getHost() + ":" + config.getPort() + config.getDatabase(), e);
			} finally {
				try { conn.close(); } catch (SQLException e) {}
			}
		}
	}

	private void close(List<ConfigBean> connList) {
		if (connList != null) {
			for (ConfigBean bean : connList) {
				if (bean != null && bean.getXaconn() != null) {
					try {
						if (bean.getXid() != null) {
							bean.getXaconn().getXAResource().rollback(bean.getXid());
							bean.setXid(null);
						}
						bean.getXaconn().close();
					} catch (Exception e) {}
				}
			}
		}
	}

	public int getTotalConnection() {
		return totalConnection;
	}
}
