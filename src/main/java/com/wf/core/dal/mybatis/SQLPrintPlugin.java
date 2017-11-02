package com.wf.core.dal.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author SQL监视插件，可以监控SQL执行的时间<br>
 * 开发期就可发现慢SQL<br>
 * 在sqlmap-config.xml配置中配置如下：<br>
 * <p>
 * <plugins> <plugin interceptor="com.ly.member.core.mybatis.SQLPrintPlugin"> <property name="show_sql"
 * value="true"/> </plugin> </plugins>
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})})
public class SQLPrintPlugin implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(SQLPrintPlugin.class);

    private static final String SHOW_SQL_PROP = "show_sql";

    private static final String SPRING_SHOEW_SQL = "SQLPrintPluginShowSql";

    private boolean showSql = false;

    /**
     * 拦截的入口
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue = null;

        long start = System.currentTimeMillis();
        try {
            returnValue = invocation.proceed();
        } catch (Exception e) {
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            long time = (end - start);
            if (showSql) {
                try {
                    IbatisExecutSqlFormat sqlFormat = new IbatisExecutSqlFormat();
//                    System.out.println(sqlFormat.formatSql(invocation, time));
                    logger.info("[{}]-{}","sqlprint",sqlFormat.formatSql(invocation, time));
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
        return returnValue;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        if (properties == null) {
            return;
        }

        if (properties.containsKey(SHOW_SQL_PROP)) {
            String value = properties.getProperty(SHOW_SQL_PROP);
            if (Boolean.TRUE.toString().equals(value)) {
                showSql = true;
            }
        }
    }
}
