package com.web.distributed.cache.tk_cache.redis;

import com.google.common.collect.Sets;
import com.web.distributed.cache.tk_cache.config.TaskCacheConfig;
import com.web.tk.common.tk_common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;

import java.util.Objects;
import java.util.Set;

/**
 * RedisPool管理类，确保RedisPool全局唯一
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-4-20
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);

    //redis连接池单例，全局唯一
    private IRedisPool redisPool;

    private static volatile RedisManager instance = null;

    public static RedisManager getInstance() {
        if (instance == null) {
            synchronized (RedisManager.class) {
                if (instance == null) {
                    instance = new RedisManager();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }
    
    private RedisManager() {
        initRedisConfig();
    }

    private void initRedisConfig() {
        String reAddr = TaskCacheConfig.getInstance().getCacheRedisHost();
        String[] arrayAddr = reAddr.split(",");

        //192.168.142.133:8081
        if (!Assert.isNotNull(arrayAddr))
            return;

        boolean isCluster = TaskCacheConfig.getInstance().getCacheRedisIsCluster();
        Set<String> sentinels = Sets.newLinkedHashSet();
        String host = null;
        int port = 0;

        for (String addr : arrayAddr) {
            if (addr.isEmpty()) {
                continue;
            }
            String[] array = addr.split(":");
            if (array.length != 2) {
                LOGGER.warn("redis config wrong addr: {}", addr);
                continue;
            }
            if (!array[1].matches("\\d+")) {
                LOGGER.warn("redis config wrong addr: {}", addr);
                continue;
            }
            host = array[0];
            port = Integer.parseInt(array[1]);
            sentinels.add(new HostAndPort(host, port).toString());
        }

        if (isCluster) {
            LOGGER.info("RedisManager, start to init RedisSentinelPool...");
            redisPool = new RedisSentinelPool(TaskCacheConfig.getInstance().getCacheRedisSentinelName(), sentinels);
        } else {
            LOGGER.info("RedisManager, start to init RedisPool...");
            redisPool = new RedisPool(host, port);
        }
    }

    public IRedisPool getRedisPool() {
        return this.redisPool;
    }

}
