package com.wf.core.cache.redis;

import com.wf.core.cache.CacheData;
import com.wf.core.cache.CacheHander;
import com.wf.core.cache.LockTask;
import com.wf.core.cache.RankingData;
import com.wf.core.cache.exception.CacheException;
import com.wf.core.cache.redis.redisson.CacheRedissonClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存实现
 *
 * @author Fe
 * @date 2016年3月3日
 */
public class RedisCacheHanderImpl extends RedisOperate  implements InitializingBean {
    private static Integer defaultCacheTime = 2 * 60 * 60;
    private JedisPoolConfig jedisPoolConfig;
    private JedisPool jedisPool;
    private String host;
    private int port;

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
    public Boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        boolean exists = jedis.exists(key);
        jedis.close();
        return exists;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Jedis jedis = jedisPool.getResource();
        byte[] bvalue = jedis.get(serializeKey(key));
        jedis.close();
        return (T) deserialize(bvalue);
    }

    @Override
    public Boolean set(String key, Object value, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(serializeKey(key), serialize(value));
        if (expireTime != null) {
            jedis.expire(key, expireTime);
        }
        jedis.close();
        return true;
    }

    @Override
    public <T> T lock(String key, LockTask<T> task) {
        return lock(key, task, defaultCacheTime);
    }

    @Override
    public Boolean delete(String key, String... keys) {
        Jedis jedis = jedisPool.getResource();
        int deleteCount = 0;
        if (key != null) {
            deleteCount += jedis.del(serializeKey(key));
        }
        if (keys != null && keys.length > 0) {
            List<byte[]> bkeys = new ArrayList<>(keys.length);
            for (String k : keys) {
                bkeys.add(serializeKey(k));
            }
            deleteCount += jedis.del(bkeys.toArray(new byte[keys.length][]));
        }
        jedis.close();
        return deleteCount != 0;
    }
    @Override
    public <T> T lock(String key, LockTask<T> task, Integer expireTime) {
        byte[] bkey = serializeKey(key);
        Boolean result = null;
        Jedis jedis = jedisPool.getResource();
        while (true) {
            result = jedis.setnx(bkey, YES) == 1;
            if (result == true) {
                if (expireTime != null){
                    jedis.expire(bkey, expireTime);
                }
                try {
                    return task.work();
                } catch (Throwable t) {
                    LOG.error("锁定任务执行异常 ex={}", ExceptionUtils.getStackTrace(t));
                    throw new CacheException("锁定任务执行异常", t);
                } finally {
                    jedis.del(bkey);
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
        Long count = jedis.incr(serializeKey(key));
        if (count == 1 && expireTime != null){
            jedis.expire(key, expireTime.intValue());
        }
        jedis.close();
        return count;
    }

    @Override
    public long incrBy(String key, long increment, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        Long count = jedis.incrBy(serializeKey(key), increment);
        if (expireTime != null){
            jedis.expire(key, expireTime.intValue());
        }
        jedis.close();
        return count;
    }

    @Override
    public double incrByFloat(String key,double value,Integer expireTime){
        Jedis jedis = jedisPool.getResource();
        double count = jedis.incrByFloat(serializeKey(key),value);
        int time = expireTime==null?defaultCacheTime:expireTime;
        jedis.expire(key,time);
        jedis.close();
        return  count;
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
    public long srem(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        long count = jedis.srem(key, value);
        jedis.close();
        return count;
    }

    @Override
    public Set<String> smembers(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.smembers(key);
        jedis.close();
        return result;
    }

    @Override
    public Boolean expire(String key, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        boolean result = jedis.expire(key, expireTime) == 1;
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
    public Boolean setNX(String key, Object value, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        byte[] bkey = serializeKey(key);
        byte[] bvalue = serialize(value);
        Boolean result = jedis.setnx(bkey, bvalue) == 1;
        if (result && expireTime != null) {
            jedis.expire(bkey, expireTime);
        }
        jedis.close();
        return result;
    }

    @Override
    public Long lpush(String key, Integer expireTime, Object... value) {
        Jedis jedis = jedisPool.getResource();
        byte[] bkey = serializeKey(key);
        byte[][] bvalues = new byte[value.length][];
        for (int i = 0; i < bvalues.length; i++) {
            bvalues[i] = serialize(value[i]);
        }
        Long result = jedis.lpush(bkey, bvalues);
        if (expireTime != null) {
            jedis.expire(bkey, expireTime);
        }
        jedis.close();
        return result;
    }

    @Override
    public <T> T rpop(String key) {
        Jedis jedis = jedisPool.getResource();
        byte[] bkey = serializeKey(key);
        byte[] value = jedis.rpop(bkey);
        jedis.close();
        return (T) deserialize(value);
    }

    @Override
    public Long lpushString(String key, Integer expireTime, String str) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.lpush(key, str);
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
    public <T> List<T> lrange(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        byte[] bkey = serializeKey(key);
        List<byte[]> list = jedis.lrange(bkey, start, end);
        jedis.close();
        if (list == null) {
            return new ArrayList<>();
        }
        List<Object> result = new ArrayList<>(list.size());
        for (byte[] data : list) {
            result.add(deserialize(data));
        }
        return (List<T>) result;
    }

    @Override
    public void ltrim(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        jedis.ltrim(serializeKey(key), start, end);
        jedis.close();
    }

    @Override
    public void zincrby(String key, double score, String target, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        byte[] bkey = serializeKey(key);
        jedis.zincrby(bkey, score, serialize(target));
        if (expireTime != null) {
            jedis.expire(bkey, expireTime);
        }
        jedis.close();
    }

    @Override
    public long zadd(String key, double score, String target, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        byte[] bkey = serializeKey(key);
        long vlaue = jedis.zadd(bkey, score, serialize(target));
        if (expireTime != null) {
            jedis.expire(bkey, expireTime);
        }
        jedis.close();
        return vlaue;
    }


    @Override
    public List<RankingData> zrevrangeWithScores(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        List<RankingData> rankingDatas = new ArrayList<>();
        Set<Tuple> result = jedis.zrevrangeWithScores(serializeKey(key), start, end);
        jedis.close();
        Iterator<Tuple> iterator = result.iterator();
        Tuple tuple;
        while (iterator.hasNext()) {
            tuple = iterator.next();
            rankingDatas.add(new RankingData(deserialize(tuple.getBinaryElement()), tuple.getScore()));
        }
        return rankingDatas;
    }

    @Override
    public List<String> zrangeByScore(String key, double start, double end) {
        Jedis jedis = jedisPool.getResource();
        Set<byte[]> set = jedis.zrangeByScore(serializeKey(key), start, end);

        jedis.close();

        if (set == null) {
            return null;
        }
        List<String> result = new ArrayList<>(set.size());
        for (byte[] bytes : set) {
            result.add((String) deserialize(bytes));
        }

        return result;
    }

    @Override
    public long zremrangeByScore(String key, double start, double end) {
        Jedis jedis = jedisPool.getResource();
        Long num = jedis.zremrangeByScore(key, start, end);

        jedis.close();

        if (num == null) {
            return 0;
        }

        return num;
    }

    @Override
    public long zrem(String key, String... members) {
        Jedis jedis = jedisPool.getResource();
        byte[][] bvalues = new byte[members.length][];
        for (int i = 0; i < bvalues.length; i++) {
            bvalues[i] = serialize(members[i]);
        }
        long value = jedis.zrem(serializeKey(key), bvalues);
        jedis.close();
        return value;
    }

    @Override
    public Long zrevrank(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Long value = jedis.zrevrank(serializeKey(key), serialize(member));
        jedis.close();
        return value;
    }

    @Override
    public Double zscore(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Double dou = jedis.zscore(serializeKey(key), serialize(member));
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
    public <T> T rlock(String key, LockTask<T> task) {
        return rlock(key, defaultRetryCount, defaultWaitTime, defaultLeaseTime, task);
    }

    @Override
    public <T> T rlock(String key, int retry, Long waitTime, Long expireTime, LockTask<T> task) {
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

    @Override
    public <T> String hmset(String key, Map<String, T> hash, Integer expireTime) {
        if (hash==null||hash.isEmpty()){
            return Y;
        }
        Jedis jedis = jedisPool.getResource();
        String result = Y;

        try {
            byte[] keys = serializeKey(key);
            Map<byte[],byte[]> maps = new HashMap<>(hash.size());
            Iterator<Map.Entry<String, T>> it = hash.entrySet().iterator();

            while (it.hasNext()){
                Map.Entry<String, T> entry = it.next();
                maps.put(serializeKey(entry.getKey()),serialize(entry.getValue()));
            }
            result = jedis.hmset(keys,maps);
            if (expireTime!=null && expireTime>0){
                jedis.expire(key,expireTime);
            }
        }finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public <T> List<T> hmget(String key, String... fields) {
        if (fields==null || fields.length<=0){
            return null;
        }
        List<T> resultData = null;
        Jedis jedis = jedisPool.getResource();

        try {
            byte[] keys = serializeKey(key);
            byte[][] fieldBytes = new byte[fields.length][];

            for (int i = 0; i <fields.length; i++) {
                String field = fields[i];
                fieldBytes[i]=serializeKey(field);
            }
            List<byte[]> result = jedis.hmget(keys,fieldBytes);

            if (result != null && !result.isEmpty()){
                resultData = new ArrayList<>(result.size());
                for (byte[] data :result ) {
                    T t = (T)deserialize(data);

                    if (t!=null){
                        resultData.add(t);
                    }
                }
            }
        }finally {
            jedis.close();
        }
        return resultData;
    }

    @Override
    public Long hlen(String key) {
        Jedis jedis = this.jedisPool.getResource();
        try {
            Long len = jedis.hlen(key);
            return len;
        } finally {
            jedis.close();
        }
    }

    @Override
    public Long hincrBy(String key, String field, long value, Integer expireTime) {
        Jedis jedis = jedisPool.getResource();
        long result = jedis.hincrBy(key,field,value);
        if (expireTime!=null && expireTime >0){
            jedis.expire(key,expireTime);
        }
        jedis.close();
        return result;
    }

    @Override
    public Set<String> hkeys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> set =null;
        try {
            set = jedis.hkeys(key);
        }finally {
            jedis.close();
        }
        return set;
    }

    @Override
    public <T> Map<String, T> hgetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        Map<String, T> result = null;
        try {
            byte[] keys = serializeKey(key);
            Map<byte[], byte[]> map = jedis.hgetAll(keys);
            if (map != null && !map.isEmpty()) {
                result = new HashMap<>(map.size());
                Iterator<Map.Entry<byte[], byte[]>> entrys = map.entrySet().iterator();

                while (entrys.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = entrys.next();
                    String mapKey = new String(entry.getKey());
                    T t = (T) deserialize(entry.getValue());
                    result.put(mapKey, t);
                }
            }
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channel) {
        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(jedisPubSub, channel);
    }

    @Override
    public void publish(String channel, String message) {
        Jedis jedis = jedisPool.getResource();
        jedis.publish(channel, message);
        jedis.close();
    }

    @Override
    protected RLock getLock(String key) {
        return cacheRedissonClient.getLock(key);
    }
}
