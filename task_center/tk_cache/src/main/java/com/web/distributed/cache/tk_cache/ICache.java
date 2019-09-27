package com.web.distributed.cache.tk_cache;

import java.util.Map;

/**
 * 缓存功能接口类
 * <br/>===============================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-4-24
 * <br/>JDK版本：1.8
 */

public interface ICache<T> {

    /**
     * 设置当前缓存存储的类对象
     *
     * @param classType
     * @param cacheName
     */
    public abstract void setClassType(Class<T> classType, String cacheName);

    /**
     * 根据指定的key获取缓存值，如果不存在，则返回null
     *
     * @param key
     * @return
     */
    public abstract T get(String key);

    /**
     * 往缓存中推送要存储的数据，如果存在，则更新
     *
     * @param key
     * @param obj
     */
    public abstract void put(String key, T obj);

    /**
     * 往缓存中推送要存储的数据，如果存在，则更新.同时设置失效期
     *
     * @param key
     * @param obj
     * @param expireTime
     */
    public abstract void put(String key, T obj, long expireTime);

    /**
     * 清空缓存对象
     */
    public abstract boolean clear();


    /**
     * 通过指定的key删除指定的缓存
     *
     * @param key
     */
    abstract boolean remove(String key);


    /**
     * 从缓存中寻找给定key值的缓存
     *
     * @param key
     * @return
     */
    abstract boolean containKey(String key);

    /**
     * 替换掉缓存中存在的key-value
     *
     * @param key
     * @param obj
     * @return
     */
    abstract boolean replace(String key, T obj);

    /**
     * 获取全部键值对
     *
     * @return
     */
    Map<String, T> getAll();

    /**
     * 获取缓存存储数据总数
     *
     * @return
     */
    long size();
}
