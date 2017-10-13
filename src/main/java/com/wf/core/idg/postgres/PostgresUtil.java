package com.wf.core.idg.postgres;

/**
 * mysql 工具
 * @author Fe
 * @version 2015年12月29日 <strong>1.0</strong>
 */
public class PostgresUtil {

//	public static List<String> getUrl(Config config) {
//		List<String> list = new ArrayList<String>();
//		if (config.getDatabase() != null) {
//			for (String db : config.getDatabase())
//				list.add(db);
//		}
//		return list;
//	}
	
	public static String getUrl(Config config, String database) {
		return "jdbc:postgresql://" + config.getHost() + ":" + config.getPort() + "/" + database;
	}
}
