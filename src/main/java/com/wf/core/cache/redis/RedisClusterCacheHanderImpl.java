package com.wf.core.cache.redis;

import com.wf.core.cache.LockTask;
import com.wf.core.cache.RankingData;
import com.wf.core.cache.redis.redisson.CacheClusterRedissonClient;
import com.wf.core.utils.type.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.*;

import java.util.*;

/**
 * 类名称：RedisClusterCacheHanderImpl
 * 类描述：redis集群模式
 * 开发人：朱水平【Tank】
 * 创建时间：2018/11/20.10:08
 * 修改备注：
 *
 * @version 1.0.0
 */
public class RedisClusterCacheHanderImpl extends RedisOperate implements InitializingBean {
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

    public void setCacheClusterRedissonClient(CacheClusterRedissonClient cacheClusterRedissonClient) {
        this.cacheClusterRedissonClient = cacheClusterRedissonClient;
    }

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

    public static Integer getDefaultCacheTime() {
        return defaultCacheTime;
    }

    public static void setDefaultCacheTime(Integer defaultCacheTime) {
        RedisClusterCacheHanderImpl.defaultCacheTime = defaultCacheTime;
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

    @Override
    public <T> T lock(String key, LockTask<T> task) {
        LOG.error("集群版不支持lock，请使用 rlockPlus ");
        return null;
    }

    @Override
    public <T> T lock(String key, LockTask<T> task, Integer expireTime) {
        LOG.error("集群版不支持lock，请使用 rlockPlus ");
        return null;
    }


    @Override
    public Boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    @Override
    public String getString(String key) {
        return jedisCluster.get(key);
    }

    @Override
    public <T> T get(String key) {
        byte[] bvalue = jedisCluster.get(serializeKey(key));
        return (T) deserialize(bvalue);
    }

    @Override
    public Boolean set(String key, Object value, Integer expireTime) {
        jedisCluster.set(serializeKey(key), serialize(value));
        if (expireTime != null) {
            jedisCluster.expire(serializeKey(key), expireTime);
        }
        return true;
    }

    @Override
    public Boolean delete(String key, String... keys) {
        if (keys != null && keys.length > 0) {
            throw new RuntimeException("集群不支持批量删除");
        }
        int deleteCount = 0;
        if (key != null) {
            deleteCount += jedisCluster.del(serializeKey(key));
        }
        return deleteCount != 0;
    }

    @Override
    public long incrBy(String key, long increment, Integer expireTime) {
        Long count = jedisCluster.incrBy(serializeKey(key), increment);
        if (expireTime != null) {
            jedisCluster.expire(serializeKey(key), expireTime.intValue());
        }
        return count;
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return jedisCluster.zcount(serializeKey(key), min, max);
    }

    @Override
    public Long getExpire(String key) {
        return jedisCluster.ttl(serializeKey(key));
    }

    @Override
    public <T> T rpoplpush(String source, String target) {
        return (T) jedisCluster.rpoplpush(source, target);
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return jedisCluster.lrem(key,count,value);
    }

    @Override
    public Long zcard(String key) {
        return jedisCluster.zcard(serializeKey(key));
    }

    @Override
    public long incr(String key, Integer expireTime) {
        Long count = jedisCluster.incr(serializeKey(key));
        if (count == 1 && expireTime != null) {
            jedisCluster.expire(serializeKey(key), expireTime.intValue());
        }
        return count;
    }

    @Override
    public double incrByFloat(String key, double value, Integer expireTime) {
        double count = jedisCluster.incrByFloat(serializeKey(key), value);
        int time = expireTime == null ? defaultCacheTime : expireTime;
        jedisCluster.expire(serializeKey(key), time);
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
        return jedisCluster.sadd(key, value) == 1;
    }

    @Override
    public Long sadd(String key, Integer expireTime, String... value) {
        Long count = jedisCluster.sadd(key, value);
        if (expireTime != null) {
            jedisCluster.expire(key, expireTime);
        }
        return count;
    }

    @Override
    public long srem(String key, String... value) {
        if (value.length > 1) {
            return jedisCluster.srem(key, value);
        }
        return 0;
    }

    @Override
    public Set<String> smembers(String key) {
        return jedisCluster.smembers(key);
    }

    @Override
    public Boolean expire(String key, Integer expireTime) {
        return jedisCluster.expire(key, expireTime) == 1;
    }

    @Override
    public Boolean setNX(String key, Object value, Integer expireTime) {
        byte[] bkey = serializeKey(key);
        byte[] bvalue = serialize(value);
        Boolean result = jedisCluster.setnx(bkey, bvalue) == 1;
        if (result && expireTime != null) {
            jedisCluster.expire(bkey, expireTime);
        }
        return result;
    }

    @Override
    public Long lpush(String key, Integer expireTime, Object... value) {
        byte[] bkey = serializeKey(key);
        byte[][] bvalues = new byte[value.length][];
        for (int i = 0; i < bvalues.length; i++) {
            bvalues[i] = serialize(value[i]);
        }
        Long result = jedisCluster.lpush(bkey, bvalues);
        if (expireTime != null) {
            jedisCluster.expire(bkey, expireTime);
        }
        return result;
    }

    @Override
    public Long rpush(String key, Integer expireTime, Object... value) {
        byte[] bkey = serializeKey(key);
        byte[][] bvalues = new byte[value.length][];
        for (int i = 0; i < bvalues.length; i++) {
            bvalues[i] = serialize(value[i]);
        }
        Long result = jedisCluster.rpush(bkey, bvalues);
        if (expireTime != null) {
            jedisCluster.expire(bkey, expireTime);
        }
        return result;
    }

    @Override
    public <T> T rpop(String key) {
        return (T) deserialize(jedisCluster.rpop(serializeKey(key)));
    }

    @Override
    public Long lpushString(String key, Integer expireTime, String str) {
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
    public <T> List<T> lrange(String key, long start, long end) {
        byte[] bkey = serializeKey(key);
        List<byte[]> list = jedisCluster.lrange(bkey, start, end);
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
        jedisCluster.ltrim(serializeKey(key), start, end);
    }

    @Override
    public void zincrby(String key, double score, String target, Integer expireTime) {
        byte[] bkey = serializeKey(key);
        jedisCluster.zincrby(bkey, score, serialize(target));
        if (expireTime != null) {
            jedisCluster.expire(bkey, expireTime);
        }
    }

    @Override
    public long zadd(String key, double score, String target, Integer expireTime) {
        byte[] bkey = serializeKey(key);
        long vlaue = jedisCluster.zadd(bkey, score, serialize(target));
        if (expireTime != null) {
            jedisCluster.expire(bkey, expireTime);
        }
        return vlaue;
    }

    @Override
    public List<RankingData> zrevrangeWithScores(String key, long start, long end) {
        List<RankingData> rankingDatas = new ArrayList<>();
        Set<Tuple> result = jedisCluster.zrevrangeWithScores(serializeKey(key), start, end);
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
        Set<byte[]> set = jedisCluster.zrangeByScore(serializeKey(key), start, end);
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
        Long num = jedisCluster.zremrangeByScore(key, start, end);
        return num == null ? 0 : num;
    }

    @Override
    public long zrem(String key, String... members) {
        byte[][] bvalues = new byte[members.length][];
        for (int i = 0; i < bvalues.length; i++) {
            bvalues[i] = serialize(members[i]);
        }
        return jedisCluster.zrem(serializeKey(key), bvalues);
    }

    @Override
    public Long zrevrank(String key, String member) {
        return jedisCluster.zrevrank(serializeKey(key), serialize(member));
    }

    @Override
    public Double zscore(String key, String member) {
        return jedisCluster.zscore(serializeKey(key), serialize(member));
    }

    @Override
    public long time() {
        LOG.error("集群版不支持time，请使用服务器时间 ");
        return 0;
    }

    @Override
    public <T> T rlock(String key, int retry, Long waitTime, Long expireTime, LockTask<T> task) {
        throw new RuntimeException("集群版不支持rlock，请使用 rlockPlus ");
    }

    @Override
    public <T> T rlock(String key, LockTask<T> task) {
        throw new RuntimeException("集群版不支持rlock，请使用 rlockPlus ");
    }

    @Override
    public void subscribe(JedisPubSub jedisPubSub, String... channel) {
        jedisCluster.subscribe(jedisPubSub, channel);
    }

    @Override
    public <T> String hmset(String key, Map<String, T> hash, Integer expireTime) {
        if (hash == null || hash.isEmpty()) {
            return Y;
        }
        byte[] keys = serializeKey(key);
        Map<byte[], byte[]> maps = new HashMap<>(hash.size());
        Iterator<Map.Entry<String, T>> it = hash.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, T> entry = it.next();
            maps.put(serializeKey(entry.getKey()), serialize(entry.getValue()));
        }
        String result = jedisCluster.hmset(keys, maps);
        if (expireTime != null && expireTime > 0) {
            jedisCluster.expire(keys, expireTime);
        }
        return result;
    }

    @Override
    public <T> List<T> hmget(String key, String... fields) {
        if (fields == null || fields.length <= 0) {
            return null;
        }
        List<T> resultData = null;
        byte[] keys = serializeKey(key);
        byte[][] fieldBytes = new byte[fields.length][];

        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            fieldBytes[i] = serializeKey(field);
        }
        List<byte[]> result = jedisCluster.hmget(keys, fieldBytes);

        if (result != null && !result.isEmpty()) {
            resultData = new ArrayList<>(result.size());

            for (byte[] data : result) {
                T t = (T) deserialize(data);
                if (t != null) {
                    resultData.add(t);
                }
            }
        }
        return resultData;
    }

    @Override
    public Boolean hdel(String key, String... fields) {
        if (fields == null || fields.length <= 0) {
            return null;
        }
        byte[][] fieldBytes = new byte[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            fieldBytes[i] = serializeKey(field);
        }
        return jedisCluster.hdel(serializeKey(key), fieldBytes) > 0;
    }

    @Override
    public Long hlen(String key) {
        return jedisCluster.hlen(key);
    }

    /**
     * 这个方法不可以使用的原因是因为 hmset 把value值序列化了。
     *
     * @param key
     * @param field
     * @param value
     * @param expireTime
     * @return
     */
    @Override
    public Long hincrBy(String key, String field, long value, Integer expireTime) {

        long result = jedisCluster.hincrBy(key, field, value);
        if (expireTime != null && expireTime > 0) {
            jedisCluster.expire(key, expireTime);
        }
        return result;
    }

    @Override
    public Set<String> hkeys(String key) {
        return jedisCluster.hkeys(key);
    }

    @Override
    public <T> Map<String, T> hgetAll(String key) {
        Map<String, T> result = null;
        byte[] keys = serializeKey(key);
        Map<byte[], byte[]> map = jedisCluster.hgetAll(keys);
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
        return result;
    }

    @Override
    public Long hset(String key, String field, String value, Integer expireTime) {
        Long result = 0L;
        try {
            result = jedisCluster.hset(key, field, value);
            if (expireTime != null) {
                jedisCluster.expire(key, expireTime);
            }
        } catch (Exception e) {
            LOG.error("hset执行异常 ex={} key={}", ExceptionUtils.getStackTrace(e), key);
        }
        return result;
    }

    @Override
    public Long hset(String key, String field, Object value, Integer expireTime) {
        Long result = 0L;
        try {
            byte[] bkey = serializeKey(key);
            byte[] bfield = serializeKey(field);
            byte[] bvalue = serialize(value);
            result = jedisCluster.hset(bkey, bfield, bvalue);
            if (expireTime != null) {
                jedisCluster.expire(bkey, expireTime);
            }
        } catch (Exception e) {
            LOG.error("hset执行异常 ex={} key={}", ExceptionUtils.getStackTrace(e), key);
        }
        return result;
    }

    @Override
    public void publish(String channel, String message) {
        jedisCluster.publish(channel, message);
    }

    @Override
    public Boolean sismember(String key, String member) {
        return jedisCluster.sismember(key, member);
    }

    @Override
    public Boolean hexists(String key, String filed) {
        return jedisCluster.hexists(key, filed);
    }

    @Override
    protected RLock getLock(String key) {
        return cacheClusterRedissonClient.getLock(key);
    }
}
