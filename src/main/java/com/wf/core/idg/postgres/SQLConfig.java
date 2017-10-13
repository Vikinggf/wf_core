package com.wf.core.idg.postgres;

import java.text.MessageFormat;

import com.wf.core.idg.util.ClassUtil;
import com.wf.core.idg.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sql工具
 * @author Fe
 * @version 2015年12月29日 <strong>1.0</strong>
 */
public class SQLConfig {
	public static final Logger logger = LoggerFactory.getLogger(SQLConfig.class);

	static String readFile(String sqlFileName) {
		String name = "sql_postgres/" + sqlFileName + ".sql";
		try {
			return IOUtil.readStream(ClassUtil.getResourceAsStream(SQLConfig.class, name)).trim().replace("'", "''");
		} catch (Exception e) {
			logger.error("无法读取配置：" + name);
			return null;
		}
	}

	public final static String parseSQL(String basicSQL, Object...params) {
		return MessageFormat.format(basicSQL, params);
	}

	public static final String databaseCheck = readFile("database_check");

	public static final String databaseCreate = readFile("database_create");

	public static final String nextvalSelect = readFile("nextval_select");

	public static final String idgAll = readFile("idg_all");

	public static final String idgCurrent = readFile("idg_current");
	
	public static final String idgStep = readFile("idg_step");

	public static final String idgExist = readFile("idg_exist");

	public static final String idgCreate = readFile("idg_create");

	public static final String idgDelete = readFile("idg_delete");
}
