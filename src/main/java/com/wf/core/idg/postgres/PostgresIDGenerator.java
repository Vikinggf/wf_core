package com.wf.core.idg.postgres;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Table;

import com.wf.core.idg.IDGeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * mysql idg
 * @author Fe
 * @version 2015年12月27日 <strong>1.0</strong>
 */
public class PostgresIDGenerator {
	private Map<Class<?>, IdgParam> clazzIdgParams = new HashMap<Class<?>, IdgParam>();
	private Map<String, IdgParam> stringIdgParams = new HashMap<String, IdgParam>();
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private PostgresIDGeneratorFactory factory;

	public PostgresIDGeneratorFactory getFactory() {
		return factory;
	}

	public void setFactory(PostgresIDGeneratorFactory factory) {
		this.factory = factory;
	}

	/**
	 * 获取下一个id
	 * @param entity
	 * @return
	 * @throws IDGeneratorException
	 */
	public Number nextId(Object entity) throws IDGeneratorException {
		if (entity == null)
			throw new IDGeneratorException("entity对象为空，无法生成id");
		IdgParam param;
		if (entity instanceof String) {
			String strEntity = entity.toString();
			param = stringIdgParams.get(strEntity);
			if (param == null) {
				synchronized (this) {
					param = clazzIdgParams.get(strEntity);
					if (param == null) {
						String name = "string_" + strEntity.toLowerCase();
						boolean[] notCheck = new boolean[factory.getTotalConnection()];
						for (int i = 0; i < notCheck.length; i++)
							notCheck[i] = true;
						String sql = SQLConfig.parseSQL(SQLConfig.NEXTVAL_SELECT, name);
						param = new IdgParam(name, false, sql, notCheck);
						stringIdgParams.put(strEntity, param);
					}
				}
			}
		} else {
			Class<?> clazz = entity.getClass();
			param = clazzIdgParams.get(clazz);
			if (param == null) {
				synchronized (this) {
					param = clazzIdgParams.get(clazz);
					if (param == null) {
						Table table = clazz.getAnnotation(Table.class);
						if (table == null)
							throw new IDGeneratorException(clazz.getName() + "不存在javax.persistence.Table注解，无法生成id");
						String name = table.name().replace('.', '_').toLowerCase();
						boolean[] notCheck = new boolean[factory.getTotalConnection()];
						for (int i = 0; i < notCheck.length; i++)
							notCheck[i] = true;
						String sql = SQLConfig.parseSQL(SQLConfig.NEXTVAL_SELECT, name);
						boolean inttype = true;
						try {
							Field field = clazz.getDeclaredField("id");
							if (field.getType() == Long.class || field.getType() == long.class)
								inttype = false;
						} catch (Exception e) {
							logger.error(clazz.getName() + "的id字段类型获取失败，将默认为int类型", e);
						}
						param = new IdgParam(name, inttype, sql, notCheck);
						clazzIdgParams.put(clazz, param);
					}
				}
			}
		}
		long id = factory.nextId(param);
		if (param.intType)
			return (int) id;
		else
			return id;
	}

	public void initIDGenerator() {
		factory.initIDGenerator();
	}
}
