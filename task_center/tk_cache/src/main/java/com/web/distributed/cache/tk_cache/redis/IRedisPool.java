package com.web.distributed.cache.tk_cache.redis;

import redis.clients.jedis.Jedis;

/**
 * Redis连接池接口，实现基本的连接池操作方法，根据不同的部署方式让不同的实现类来实现
 * <br/>=======================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-4-20
 * <br/>jdk版本：1.8
 * <br/>=======================================
 */
public interface IRedisPool {

    /**
     * 从连接池中获取Jedis实例
     *
     * @return Jedis
     */
    Jedis getResource();

    /**
     * 关闭资源
     */
    void close(Jedis jedis);

    /**
     * 强制销毁连接
     *
     * @param jedis
     */
    void destroyResource(Jedis jedis);

}
