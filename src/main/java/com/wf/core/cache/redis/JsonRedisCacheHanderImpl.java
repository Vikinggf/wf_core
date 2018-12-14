package com.wf.core.cache.redis;

import com.wf.core.cache.JsonCacheData;
import com.wf.core.cache.JsonCacheHander;
import com.wf.core.cache.LockTask;
import com.wf.core.cache.RankingData;
import com.wf.core.cache.exception.CacheException;
import com.wf.core.cache.redis.redisson.CacheRedissonClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存实现
 *
 * @author Fe
 * @date 2016年3月3日
 */
public class JsonRedisCacheHanderImpl implements JsonCacheHander, InitializingBean {
    private static Integer defaultCacheTime = 2 * 60 * 60;
    private JedisPoolConfig jedisPoolConfig;
    private JedisPool jedisPool;
    private String host;
    private int port;

    public static final int defaultRetryCount = 3;
    public static final long defaultWaitTime = 5L;
    public static final long defaultLeaseTime = 30L;

    private CacheRedissonClient cacheRedissonClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool(jedisPoolConfig, host, port);
        LOG.info("redis连接池已经创建：" + host + " " + port);
    }

    public void setCacheRedissonClient(CacheRedissonClient cacheRedissonClient) {
        this.cacheRedissonClient = cacheRedissonClient;
    }

    public JedisPoolConfig getJedisPoolConfig() {
        return jedisPoolConfig;
    }

    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        this.jedisPoolConfig = jedisPoolConfig;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String cache(String key, JsonCacheData data) {
        return cache(key, data, defaultCacheTime);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String cache(String key, JsonCacheData data, Integer expireTime) {
        String result = this.get(key);
        if (result == null) {
            result = data.findData();
            this.set(key, result, expireTime);
        }
        return result;
    }

    @Override
    public Boolean set(String key, String value) {
        return this.set(key, value, defaultCacheTime);
    }

    @Override
    public long incr(String key) {
        return incr(key, defaultCacheTime);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    @Override
    public Boolean set(String key, String value, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        if (expireTime != null) {
            jedis.expire(key, expireTime.intValue());
        }
        jedis.close();
        return true;
    }

    @Override
    public long incrCurrent(String key) {
        return incrBy(key, 0, null);
    }

    @Override
    public Boolean setNX(String key, Integer expireTime) {
        return setNX(key, Y, expireTime);
    }

    @Override
    public String lock(String key, LockTask<String> task) {
        return lock(key, task, defaultCacheTime);
    }

    @Override
    public Boolean delete(String key, String... keys) {
        Jedis jedis = jedisPool.getResource();
        int deleteCount = 0;
        if (key != null) {
            deleteCount += jedis.del(key);
        }
        for (String k : keys) {
            deleteCount += jedis.del(k);
        }
        jedis.close();
        return deleteCount != 0;
    }

    /*@Override
    public Set<String> keys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> set = jedis.keys(key + "*");
        jedis.close();
        return set;
    }*/

    @Override
    public String lock(String key, LockTask<String> task, Integer expireTime) {
        Boolean result = null;
        Jedis jedis = jedisPool.getResource();
        while (true) {
            result = jedis.setnx(key, YES) == 1;
            if (result == true) {
                if (expireTime != null) {
                    jedis.expire(key, expireTime.intValue());
                }
                try {
                    return task.work();
                } catch (Throwable t) {
                    LOG.error("锁定任务执行异常 ex={}", ExceptionUtils.getStackTrace(t));
                    throw new CacheException("锁定任务执行异常", t);
                } finally {
                    jedis.del(key);
                    jedis.close();
                }
            } else {
                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    LOG.error("锁定任务线程等待异常 ex={}", ExceptionUtils.getStackTrace(e));
                    throw new CacheException("锁定任务执行异常", e);
                }
            }
        }
    }

    @Override
    public long incr(String key, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        Long count = jedis.incr(key);
        if (count == 1 && expireTime != null) {
            jedis.expire(key, expireTime.intValue());
        }
        jedis.close();
        return count;
    }

    @Override
    public long incrBy(String key, long increment) {
        return incrBy(key, increment, defaultCacheTime);
    }

    @Override
    public long incrBy(String key, long increment, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        Long count = jedis.incrBy(key, increment);
        if (expireTime != null) {
            jedis.expire(key, expireTime.intValue());
        }
        jedis.close();
        return count;
    }

    @Override
    public long scard(String key) {
        Jedis jedis = jedisPool.getResource();
        Long count = jedis.scard(key);
        jedis.close();
        return count;
    }

    @Override
    public String spop(String key) {
        Jedis jedis = jedisPool.getResource();
        String data = jedis.spop(key);
        jedis.close();
        return data;
    }

    @Override
    public Boolean sadd(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        boolean result = jedis.sadd(key, value) == 1;
        jedis.close();
        return result;
    }

    @Override
    public Boolean expire(String key, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        boolean result = jedis.expire(key, expireTime.intValue()) == 1;
        jedis.close();
        return result;
    }

    @Override
    public String getString(String key) {
        Jedis jedis = jedisPool.getResource();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    @Override
    public Boolean setNX(String key, String value, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        Boolean result = jedis.setnx(key, value) == 1;
        if (result && expireTime != null) {
            jedis.expire(key, expireTime.intValue());
        }
        jedis.close();
        return result;
    }

    @Override
    public Long lpush(String key, Integer expireTime, String... value) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.lpush(key, value);
        if (expireTime != null) {
            jedis.expire(key, expireTime);
        }
        jedis.close();
        return result;
    }

    @Override
    public Long llen(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.llen(key);
        jedis.close();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        List<String> list = jedis.lrange(key, start, end);
        if (list == null) {
            return new ArrayList<>();
        }
        jedis.close();
        return list;
    }

    @Override
    public void ltrim(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        jedis.ltrim(key, start, end);
        jedis.close();
    }

    @Override
    public void zincrby(String key, double score, String target) {
        zincrby(key, score, target, defaultCacheTime);
    }

    @Override
    public void zincrby(String key, double score, String target, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        jedis.zincrby(key, score, target);
        if (expireTime != null) {
            jedis.expire(key, expireTime);
        }
        jedis.close();
    }

    @Override
    public long zadd(String key, double score, String target) {
        return zadd(key, score, target, defaultCacheTime);
    }

    @Override
    public long zadd(String key, double score, String target, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        long vlaue = jedis.zadd(key, score, target);
        if (expireTime != null) {
            jedis.expire(key, expireTime);
        }
        jedis.close();
        return vlaue;
    }


    @Override
    public List<RankingData> zrevrangeWithScores(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        List<RankingData> rankingDatas = new ArrayList<>();
        Set<Tuple> result = jedis.zrevrangeWithScores(key, start, end);
        jedis.close();
        Iterator<Tuple> iterator = result.iterator();
        Tuple tuple;
        while (iterator.hasNext()) {
            tuple = iterator.next();
            rankingDatas.add(new RankingData(tuple.getBinaryElement(), tuple.getScore()));
        }
        return rankingDatas;
    }

    @Override
    public long zrem(String key, String... members) {
        Jedis jedis = jedisPool.getResource();
        long value = jedis.zrem(key, members);
        jedis.close();
        return value;
    }

    @Override
    public Long zrevrank(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Long value = jedis.zrevrank(key, member);
        jedis.close();
        return value;
    }

    @Override
    public Double zscore(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Double dou = jedis.zscore(key, member);
        jedis.close();
        return dou;
    }

    @Override
    public long time() {
        Jedis jedis = jedisPool.getResource();
        List<String> times = jedis.time();
        long time = Long.valueOf(times.get(0)) * 1000;
        jedis.close();
        return time;
    }


    @Override
    public String rlock(String key, LockTask<String> task) {
        return rlock(key, defaultRetryCount, defaultWaitTime, defaultLeaseTime, task);
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
        RLock lock = cacheRedissonClient.getLock(key);
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
        if(hash != null && !hash.isEmpty()) {
            Jedis jedis = jedisPool.getResource();
            String result = "Y";
            try {
                result = jedis.hmset(key, hash);
                if(expireTime != null && expireTime.intValue() > 0) {
                    jedis.expire(key, expireTime.intValue());
                }
            } finally {
                jedis.close();
            }

            return result;
        } else {
            return "Y";
        }
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        if (fields == null) {
            return null;
        }
        List<String> resultData = null;
        Jedis jedis = jedisPool.getResource();

        try {
            resultData = jedis.hmget(key, fields);
        } finally {
            jedis.close();
        }
        return resultData;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> map = null;
        try {
            map = jedis.hgetAll(key);
        } catch (Exception e) {
            LOG.error("hgetAllWithoutSerialize执行异常 ex={} key={}", ExceptionUtils.getStackTrace(e), key);

        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return map;
    }


    @Override
    public String rlock(String key, int retry, Long waitTime, Long expireTime, LockTask<String> task) {
        Boolean result = null;
        if (retry <= 0) {
            retry = defaultRetryCount;
        }
        if (waitTime == null) {
            waitTime = defaultWaitTime;
        }
        if (expireTime == null) {
            expireTime = defaultLeaseTime;
        }
        int retryCount = 0;
        while (retryCount++ < retry) {
            // TODO: 2017/9/26 这个地方用redisson的锁来进行加锁
            try {
                result = cacheRedissonClient.tryLock(key, waitTime, expireTime);
            } catch (InterruptedException e) {
                result = false;
            }
            if (result == true) {
                try {
                    return task.work();
                } catch (Throwable t) {
                    LOG.error("锁定任务执行异常 ex={}", ExceptionUtils.getStackTrace(t));
                    throw new CacheException("锁定任务执行异常", t);
                } finally {
                    cacheRedissonClient.unLock(key);
                }
            } else {
                try {
                    Thread.sleep(8);
                } catch (InterruptedException e) {
                    LOG.error("锁定任务线程等待异常 ex={}", ExceptionUtils.getStackTrace(e));
                    throw new CacheException("锁定任务执行异常", e);
                }
            }
        }
        throw new CacheException("锁定任务执行异常", new RuntimeException());
    }
}
