package com.web.distributed.cache.tk_cache.redis;


import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.web.distributed.cache.tk_cache.ICache;
import com.web.distributed.cache.tk_cache.config.TaskCacheConfig;
import com.web.tk.common.tk_common.codec.Codec;
import com.web.tk.common.tk_common.exception.CommonException;
import com.web.tk.common.tk_common.serialize.ISerialize;
import com.web.tk.common.tk_common.serialize.SerializeClient;
import com.web.tk.common.tk_common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.commands.JedisCommands;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis对外业务层，技术流程决定了这个组件没法对外提供简便的使用(就是简单的new，然后再对象.方法执行)
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-4-20
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisCache<T> implements ICache<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

    private static final String CACHEVOPACKAGE = TaskCacheConfig.getInstance().getCacheVoPackage();

    private JedisCommands redisClient = null;

    private Class<T> classType = null;

    private String cacheName = null;

    public RedisCache() {
        init();
    }

    private void init() {
        redisClient = new RedisCommands(RedisManager.getInstance().getRedisPool());
    }

    @Override
    public void setClassType(Class<T> classType, String cacheName) {
        this.classType = classType;
        this.cacheName = cacheName;
    }

    public Class<T> getClassType() {
        return classType;
    }

    public String getCacheName() {
        return cacheName;
    }

    @Override
    public T get(String field) {
        ISerialize serialize = SerializeClient.getSerialize(CACHEVOPACKAGE);
        String val = redisClient.hget(getCacheName(), field);
        if (Assert.isEmpty(val)) {
            return null;
        }
        try {
            byte[] bt = Codec.decodeBase64(val);
            return serialize.deSerialize(getClassType(), bt);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(String.format("RedisCache, fail to decode value, field：%s, value：%s", field, val), e);
            throw new CommonException(String.format("RedisCache, fail to decode value, field：%s, value：%s", field, val), e);
        }
    }

    @Override
    public void put(String field, T obj) {
        put(field, obj, 0L);
    }

    @Override
    public void put(String field, T obj, long expireTime) {
        ISerialize serialize = SerializeClient.getSerialize(CACHEVOPACKAGE);
        byte[] bt = serialize.serialize(obj);
        String val = Codec.encodeBase64(bt);
        redisClient.hset(getCacheName(), field, val);
        if (expireTime > 0L) {
            redisClient.expire(field, (int)expireTime);
        }
    }

    @Override
    public boolean clear() {
        redisClient.del(getCacheName());
        return true;
    }

    //???
    @Override
    public boolean remove(String field) {
        Long count = redisClient.hdel(getCacheName(), field);
        return count > 0;
    }

    @Override
    public boolean containKey(String field) {
        return redisClient.hexists(getCacheName(), field);
    }

    @Override
    public boolean replace(String field, T obj) {
        ISerialize serialize = SerializeClient.getSerialize(CACHEVOPACKAGE);
        byte[] bt = serialize.serialize(obj);
        String val = Codec.encodeBase64(bt);

        redisClient.hset(getCacheName(), field, val);
        return true;
    }

    @Override
    public Map<String, T> getAll() {
        ISerialize serialize = SerializeClient.getSerialize(CACHEVOPACKAGE);
        Map<String, T> retMap = new HashMap<>();
        Map<String, String> srcMap = redisClient.hgetAll(getCacheName());
        if (!Assert.isNotNull(srcMap)) {
            return Collections.emptyMap();
        }
        for (Map.Entry<String, String> entry : srcMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                byte[] bytes = Codec.decodeBase64(value);
                T obj = serialize.deSerialize(getClassType(), bytes);
                retMap.put(key, obj);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(String.format("RedisCache, getAll(), fail to decode base64, key：{}, value：{}", key, value), e);
                continue;
            }
        }
        return retMap;
    }

    @Override
    public long size() {
        return redisClient.hlen(getCacheName());
    }
}