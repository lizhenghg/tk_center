package com.web.distributed.cache.tk_cache.redis;

import com.web.tk.common.tk_common.exception.CommonException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolAbstract;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Set;

/**
 * Redis连接池接口实现类
 * <br/>=======================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-4-22
 * <br/>jdk版本：1.8
 * <br/>=======================================
 */
public class RedisSentinelPool implements IRedisPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelPool.class);

    private static final int MAX_TIME_OUT = 125 << 6;

    private static JedisPoolAbstract jedisPool = null;

    private int maxIdle = 5;

    private int maxTotal = 5 << 2;

    private int minIdle = 0;

    private long max_wait_mills = 375L << 4;

    private boolean testOnBorrow = 1 == 1;

    private boolean testWhileIdle = true;

    private int retryCount = 5;

    private int retryingWaitingMillis = 125 << 4;

    private RedisSentinelPool() {
    }

    public RedisSentinelPool(final String masterName, final Set<String> sentinels) {
        this(masterName, sentinels, null);
    }

    public RedisSentinelPool(final String masterName, final Set<String> sentinels, final GenericObjectPoolConfig config) {
        this(masterName, sentinels, config, MAX_TIME_OUT);
    }

    public RedisSentinelPool(final String masterName, final Set<String> sentinels, final GenericObjectPoolConfig config, final int maxTimeout) {
        init(masterName, sentinels, config, maxTimeout);
    }

    public void init(final String masterName, final Set<String> sentinels, GenericObjectPoolConfig config, final int maxTimeout) {
        if (config == null) {
            config = new GenericObjectPoolConfig();
            //设置等待最长时间，单位：毫秒
            config.setMaxWaitMillis(max_wait_mills);
            //设置最大空闲等待
            config.setMaxIdle(maxIdle);
            //设置最小空闲等待
            config.setMinIdle(minIdle);
            //设置最大连接数
            config.setMaxTotal(maxTotal);
            //在空闲时检查有效性
            config.setTestWhileIdle(testWhileIdle);
            //获取连接时是否测试连接可行性
            config.setTestOnBorrow(testOnBorrow);
        }
        jedisPool = new JedisSentinelPool(masterName, sentinels, config, maxTimeout);    //设置读取超时，默认2000毫秒
    }

    @Override
    public Jedis getResource() {
        Jedis jedis = null;
        jedis = getResourceFromPool();
        int count = 0;
        while (jedis == null && count < retryCount) {
            count++;
            jedis = getResourceFromPool();
            wait(retryingWaitingMillis);
        }
        if (jedis == null) {
            LOGGER.error("RedisSentinelPool, fail to get resource from pool, jedis is null");
            throw new CommonException("RedisSentinelPool, fail to get resource from pool, jedis is null");
        }
        return jedis;
    }

    @Override
    public void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 强制销毁连接
     *
     * @param jedis
     */
    @Override
    public void destroyResource(Jedis jedis) {
        if (jedis != null && jedis.isConnected()) {
            try {
                jedis.quit();
            } catch (Exception e) {
                LOGGER.error(String.format("RedisSentinelPool, destroy the jedis, fail to quit jedis. msg: %s", e.getMessage()), e);
            }
        }
    }

    /**
     * 从池中安全地获取单个连接
     *
     * @return Jedis
     */
    private synchronized Jedis getResourceFromPool() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (Exception e) {
            LOGGER.error("RedisSentinelPool, fail to get Resource", e);
        }
        return jedis;
    }

    private void wait(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            LOGGER.error(String.format("RedisSentinelPool, fail to sleep, msg：%s", e.getMessage()), e);
        }
    }
}