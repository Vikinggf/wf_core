    package com.wf.core.log;

import com.wf.core.utils.GfJsonUtil;
import com.wf.core.utils.IpGetter;
import com.wf.core.utils.RedisManagerUtil;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenlongfei
 * @date 2016-09-27
 * 日志输出到指定redis
 */
public class LogAppender extends AppenderSkeleton {
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
    private String appName;
    private String reidsHost;
    private String redisPort;
    private String redisAuth;
    private String redisKey;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getReidsHost() {
        return reidsHost;
    }

    public void setReidsHost(String reidsHost) {
        this.reidsHost = reidsHost;
    }

    public String getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(String redisPort) {
        this.redisPort = redisPort;
    }

    public String getRedisAuth() {
        return redisAuth;
    }

    public void setRedisAuth(String redisAuth) {
        this.redisAuth = redisAuth;
    }

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    @Override
    protected void append(LoggingEvent event) {
        final LoggingEvent ev = event;
        final String appNames = this.appName;
        final String host = this.reidsHost;
        final Integer port = Integer.parseInt(this.redisPort);
        final String auth = this.redisAuth;
        final String key = this.redisKey;

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                LogCommand lc = new LogCommand();
                String ip = IpGetter.getIp();
                String hostName = IpGetter.getLocalHostName();
                lc.setIp(ip);
                lc.setAppName(appNames);
                StringBuffer sb = new StringBuffer();
                String[] errorList = ev.getThrowableStrRep();
                for (int i = 0; i < errorList.length; i++) {
                    sb.append(errorList[i]);
                }
//                sb.append(ev.getRenderedMessage());
                lc.setContent(sb.toString());
                Timestamp ts = new Timestamp(ev.getTimeStamp());
                lc.setDtNow(ts);
                lc.setHostName(hostName);
                lc.setLogType(appNames);
                lc.setSite("");
                lc.setSiteID(0);
                lc.setSubject(event.getLoggerName());
                lc.setUrl("http://");
                int levelLog = ev.getLevel().toInt();
                switch (levelLog) {
                    case 10000:
                        lc.setLogLevel(LogLevel.Debug);
                        break;
                    case 20000:
                        lc.setLogLevel(LogLevel.Info);
                        break;
                    case 30000:
                        lc.setLogLevel(LogLevel.Warn);
                        break;
                    case 40000:
                        lc.setLogLevel(LogLevel.Error);
                        break;
                    case 50000:
                        lc.setLogLevel(LogLevel.Fatal);
                        break;
                    default:
                        break;
                }
                RedisManagerUtil util = RedisManagerUtil.getInstance(host, port, auth);
                util.rpushNoLog(key, GfJsonUtil.toJSONString(lc));
            }
        });
    }

    @Override
    public void close() {
        this.closed = true;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
