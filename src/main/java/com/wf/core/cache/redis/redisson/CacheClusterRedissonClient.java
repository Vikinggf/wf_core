package com.wf.core.cache.redis.redisson;


import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.TimeUnit;

public class CacheClusterRedissonClient implements InitializingBean {

    private Config config = new Config();
    private RedissonClient redisson = null;

    private String redisAddrs;

    public String getRedisAddrs() {
        return redisAddrs;
    }

    public void setRedisAddrs(String redisAddrs) {
        this.redisAddrs = redisAddrs;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        String[] uris = redisAddrs.split(";");
        if (uris != null && uris.length > 0) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            for (String item : uris) {
                clusterServersConfig.addNodeAddress("redis://"+item);
            }
        }
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

    public RLock getLock(String name){
        return redisson.getLock(name);
    }
}
