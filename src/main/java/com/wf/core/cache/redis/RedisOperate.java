package com.wf.core.cache.redis;

import com.wf.core.cache.CacheData;
import com.wf.core.cache.CacheHander;
import com.wf.core.cache.LockTask;
import com.wf.core.cache.exception.CacheException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 类名称：RedisOperate
 * 类描述：
 * 开发人：朱水平【Tank】
 * 创建时间：2018/11/20.11:00
 * 修改备注：
 *
 * @version 1.0.0
 */
public abstract class RedisOperate implements CacheHander {
    /**
     * 默认KEY缓存时间
     */
    protected static Integer defaultCacheTime = 2 * 60 * 60;
    protected static final int defaultRetryCount = 3;
    protected static final long defaultWaitTime = 5L;
    protected static final long defaultLeaseTime = 30L;



    protected byte[] serializeKey(String key) {
        return key.getBytes();
    }

    protected byte[] serialize(Object object) {
        if (object == null){
            return NULL;
        }
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
            throw new CacheException("实例化失败：" + object.getClass().getName() + "未实现java.io.Serializable或全局变量未实现", e);
        }
    }

    protected Object deserialize(byte[] bytes) {
        if (bytes != null) {
            if (Arrays.equals(NULL, bytes)){
                return null;
            }
            ByteArrayInputStream byteArrayOutputStream = null;
            try {
                byteArrayOutputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayOutputStream);
                return objectInputStream.readObject();
            } catch (Exception e) {
                throw new CacheException("实例化失败", e);
            }
        }
        return null;
    }

    @Override
    public long zadd(String key, double score, String target) {
        return zadd(key, score, target, defaultCacheTime);
    }

    @Override
    public void zincrby(String key, double score, String target) {
        zincrby(key, score, target, defaultCacheTime);
    }

    @Override
    public Boolean setNX(String key, Integer expireTime) {
        return setNX(key, Y, expireTime);
    }

    @Override
    public long incrCurrent(String key) {
        return incrBy(key, 0, null);
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
    public Boolean set(String key, Object value) {
        return this.set(key, value, defaultCacheTime);
    }

    @Override
    public <T> T cache(String key, CacheData data) {
        return cache(key, data, defaultCacheTime);
    }

    @Override
    public <T> T cache(String key, CacheData data, Integer expireTime) {
        Object result = this.get(key);
        if (result == null) {
            result = data.findData();
            this.set(key, result, expireTime);
        }
        return (T) result;
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
        RLock lock = getLock(key);
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

    protected abstract RLock getLock(String key);
}
