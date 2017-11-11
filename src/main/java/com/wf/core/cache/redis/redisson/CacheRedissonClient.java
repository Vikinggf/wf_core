package com.wf.core.cache.redis.redisson;


import com.wf.core.cache.CacheHander;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.InitializingBean;

import javax.sound.sampled.Port;
import java.util.concurrent.TimeUnit;

public class CacheRedissonClient implements InitializingBean {

    private Config config = new Config();
    private RedissonClient redisson = null;

    private String host;
    private String port;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        redisson = Redisson.create(config);
    }

    public boolean tryLock(String name, long waitTime, long leaseTime) throws InterruptedException {
        RLock lock = redisson.getLock(name);
        boolean result = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        return result;
    }

    public void unLock(String name) {
        RLock lock = redisson.getLock(name);
        lock.unlock();
    }
}
