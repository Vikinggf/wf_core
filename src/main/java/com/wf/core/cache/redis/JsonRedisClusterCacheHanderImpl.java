package com.wf.core.cache.redis;

import com.wf.core.cache.JsonCacheData;
import com.wf.core.cache.JsonCacheHander;
import com.wf.core.cache.LockTask;
import com.wf.core.cache.RankingData;
import com.wf.core.cache.exception.CacheException;
import com.wf.core.cache.redis.redisson.CacheClusterRedissonClient;
import com.wf.core.utils.type.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 类名称：JsonRedisClusterCacheHanderImpl
 * 类描述：
 * 开发人：朱水平【Tank】
 * 创建时间：2019/1/8.20:56
 * 修改备注：
 *
 * @version 1.0.0
 */
public class JsonRedisClusterCacheHanderImpl implements JsonCacheHander, InitializingBean {
    private static Integer defaultCacheTime = 2 * 60 * 60;
    protected static final int defaultRetryCount = 3;
    protected static final long defaultWaitTime = 5L;
    protected static final long defaultLeaseTime = 30L;

    /**
     * 连接池信息
     */
    private JedisPoolConfig jedisPoolConfig;
    /**
     * redis集群ip:port地址
     */
    private String redisAddrs;

    /**
     * 集群连接客户端
     */
    private JedisCluster jedisCluster;

    private CacheClusterRedissonClient cacheClusterRedissonClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        if (StringUtils.isBlank(redisAddrs)) {
            throw new NullPointerException("redis 集群地址配置为空");
        }
        String[] uris = redisAddrs.split(";");
        if (uris != null && uris.length > 0) {
            for (String item : uris) {
                String[] ipAndport = item.split(":");
                if (ipAndport.length != 2) {
                    throw new IllegalArgumentException("redis 集群地址解析错误，请查看配置是否有误");
                }
                jedisClusterNodes.add(new HostAndPort(ipAndport[0], Integer.valueOf(ipAndport[1])));
            }
        }
        jedisCluster = new JedisCluster(jedisClusterNodes, jedisPoolConfig);
        LOG.info("redis集群连接已经创建：" + redisAddrs);
    }


    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        this.jedisPoolConfig = jedisPoolConfig;
    }

    public String getRedisAddrs() {
        return redisAddrs;
    }

    public void setRedisAddrs(String redisAddrs) {
        this.redisAddrs = redisAddrs;
    }

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public void setCacheClusterRedissonClient(CacheClusterRedissonClient cacheClusterRedissonClient) {
        this.cacheClusterRedissonClient = cacheClusterRedissonClient;
    }

    public CacheClusterRedissonClient getCacheClusterRedissonClient() {
        return cacheClusterRedissonClient;
    }


    @Override
    public String lock(String key, LockTask<String> task) {
        LOG.error("集群版不支持lock，请使用 rlockPlus ");
        return null;
    }

    @Override
    public String lock(String key, LockTask<String> task, Integer expireTime) {
        LOG.error("集群版不支持lock，请使用 rlockPlus ");
        return null;
    }

    @Override
    public String cache(String key, JsonCacheData data) {
        return cache(key, data, defaultCacheTime);
    }

    @Override
    public String cache(String key, JsonCacheData data, Integer expireTime) {
        String result = this.get(key);
        if (result == null) {
            result = data.findData();
            this.set(key, result, expireTime);
        }
        return result;
    }

    @Override
    public String getString(String key) {
        return jedisCluster.get(key);
    }

    @Override
    public String get(String key) {
        return this.getString(key);
    }

    @Override
    public Boolean set(String key, String value) {
        return this.set(key, value, defaultCacheTime);
    }

    @Override
    public Boolean set(String key, String value, Integer expireTime) {
        jedisCluster.set(key, value);
        if (expireTime != null) {
            jedisCluster.expire(key, expireTime.intValue());
        }
        return true;
    }

    @Override
    public Boolean delete(String key, String... keys) {
        int deleteCount = 0;
        if (key != null) {
            deleteCount += jedisCluster.del(key);
        }
        for (String k : keys) {
            deleteCount += jedisCluster.del(k);
        }
        return deleteCount != 0;
    }

    @Override
    public long incr(String key) {
        return incr(key, defaultCacheTime);
    }

    @Override
    public long incrBy(String key, long increment) {
        return incrBy(key, increment, defaultCacheTime);
    }

    @Override
    public long incrBy(String key, long increment, Integer expireTime) {
        Long count = jedisCluster.incrBy(key, increment);
        if (expireTime != null) {
            jedisCluster.expire(key, expireTime.intValue());
        }
        return count;
    }

    @Override
    public long incrCurrent(String key) {
        return incrBy(key, 0, null);
    }

    @Override
    public long incr(String key, Integer expireTime) {
        Long count = jedisCluster.incr(key);
        if (count == 1 && expireTime != null) {
            jedisCluster.expire(key, expireTime);
        }
        return count;
    }

    @Override
    public long scard(String key) {
        return jedisCluster.scard(key);
    }

    @Override
    public String spop(String key) {
        return jedisCluster.spop(key);
    }

    @Override
    public Boolean sadd(String key, String value) {
        return (jedisCluster.sadd(key, value) == 1);
    }

    @Override
    public Boolean expire(String key, Integer expireTime) {
        return jedisCluster.expire(key, expireTime) == 1;
    }

    @Override
    public Boolean setNX(String key, Integer expireTime) {
        return setNX(key, Y, expireTime);
    }

    @Override
    public Boolean setNX(String key, String value, Integer expireTime) {
        Boolean result = jedisCluster.setnx(key, value) == 1;
        if (result && expireTime != null) {
            jedisCluster.expire(key, expireTime.intValue());
        }
        return result;
    }

    @Override
    public Long lpush(String key, Integer expireTime, String... str) {
        Long result = jedisCluster.lpush(key, str);
        if (expireTime != null) {
            jedisCluster.expire(key, expireTime);
        }
        return result;
    }

    @Override
    public Long llen(String key) {
        return jedisCluster.llen(key);
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        List<String> list = jedisCluster.lrange(key, start, end);
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    @Override
    public void ltrim(String key, long start, long end) {
        jedisCluster.ltrim(key, start, end);
    }

    @Override
    public void zincrby(String key, double score, String target) {
        zincrby(key, score, target, defaultCacheTime);
    }

    @Override
    public void zincrby(String key, double score, String target, Integer expireTime) {
        jedisCluster.zincrby(key, score, target);
        if (expireTime != null) {
            jedisCluster.expire(key, expireTime);
        }
    }

    @Override
    public long zadd(String key, double score, String target) {
        return zadd(key, score, target, defaultCacheTime);
    }

    @Override
    public long zadd(String key, double score, String target, Integer expireTime) {
        long vlaue = jedisCluster.zadd(key, score, target);
        if (expireTime != null) {
            jedisCluster.expire(key, expireTime);
        }
        return vlaue;
    }

    @Override
    public List<RankingData> zrevrangeWithScores(String key, long start, long end) {
        List<RankingData> rankingDatas = new ArrayList<>();
        Set<Tuple> result = jedisCluster.zrevrangeWithScores(key, start, end);
        Iterator<Tuple> iterator = result.iterator();
        Tuple tuple;
        while (iterator.hasNext()) {
            tuple = iterator.next();
            rankingDatas.add(new RankingData(tuple.getBinaryElement(), tuple.getScore()));
        }
        return rankingDatas;
    }

    @Override
    public long zrem(String key, String... member) {
        return jedisCluster.zrem(key, member);
    }

    @Override
    public Long zrevrank(String key, String member) {
        return jedisCluster.zrevrank(key, member);
    }

    @Override
    public Double zscore(String key, String member) {
        return jedisCluster.zscore(key, member);
    }

    @Override
    public long time() {
        throw new RuntimeException("集群版不支持time，请使用服务器时间 ");
    }

    @Override
    public String rlock(String key, int retry, Long waitTime, Long expireTime, LockTask<String> task) {
        throw new RuntimeException("集群版不支持rlock，请使用 rlockPlus ");
    }

    @Override
    public String rlock(String key, LockTask<String> task) {
        throw new RuntimeException("集群版不支持rlock，请使用 rlockPlus ");
    }

    @Override
    public <T> T rlockPlus(String key, LockTask<T> task) {
        return rlockPlus(key, defaultWaitTime, defaultLeaseTime, task);
    }

    @Override
    public <T> T rlockPlus(String key, Long waitTime, Long expireTime, LockTask<T> task) {
        if (waitTime == null) {
            waitTime = defaultWaitTime;
        }
        if (expireTime == null) {
            expireTime = defaultLeaseTime;
        }
        RLock lock = cacheClusterRedissonClient.getLock(key);
        try {
            if (lock.tryLock(waitTime, expireTime, TimeUnit.SECONDS)) {
                return task.work();
            }
        } catch (InterruptedException e) {
            LOG.error("线程锁定异常 ex={} key={}", ExceptionUtils.getStackTrace(e),key);
            throw new CacheException("线程锁定异常", e);
        } catch (Throwable e) {
            LOG.error("锁定任务执行异常 ex={} key={}", ExceptionUtils.getStackTrace(e),key);
            throw new CacheException("锁定任务执行异常", e);
        } finally {
            try {
                if (lock != null && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (Exception e) {
                LOG.error("释放锁异常 ex={} key={}", ExceptionUtils.getStackTrace(e),key);
                throw new CacheException("释放锁异常", e);
            }
        }
        throw new CacheException(key + " 获取锁时等待超时，等待上个任务执行完后再重试", new RuntimeException(key + " 获取锁时等待超时，等待上个任务执行完后再重试"));
    }

    @Override
    public String hmset(String key, Map<String, String> hash, Integer expireTime) {
        String result = "Y";
        if (hash != null && !hash.isEmpty()) {
            result = jedisCluster.hmset(key, hash);
            if (expireTime != null && expireTime.intValue() > 0) {
                jedisCluster.expire(key, expireTime.intValue());
            }
            return result;
        } else {
            return result;
        }
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        if (fields == null) {
            return null;
        }
        return jedisCluster.hmget(key, fields);
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        Map<String, String> map = null;
        try {
            map = jedisCluster.hgetAll(key);
        } catch (Exception e) {
            LOG.error("hgetAllWithoutSerialize执行异常 ex={} key={}", ExceptionUtils.getStackTrace(e), key);
        }
        return map;
    }

}
