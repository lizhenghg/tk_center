package com.web.distributed.cache.tk_cache;

import com.web.distributed.cache.tk_cache.config.TaskCacheConfig;
import com.web.tk.common.tk_common.classLoader.ClassClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 缓存对外应用层，缓存基础类，实现基本的缓存逻辑
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class AbstractCache<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);

    private ICache<T> iCache;

    private String classTypeName;

    public AbstractCache() {
        String cacheClassName = TaskCacheConfig.getInstance().getCacheClass();
        classTypeName = this.getClass().getName();

        try {
            Class<ICache<T>> cacheClass = (Class<ICache<T>>) Class.forName(cacheClassName);
            LOGGER.info("init cache：{}", classTypeName);
            iCache = cacheClass.newInstance();
            iCache.setClassType(seekClass(), classTypeName);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            LOGGER.warn("AbstractCache, fail to constructor：{}", ex);
        }
    }

    public Class<T> seekClass() {
        Class<?> classes = ClassClient.seekClass(this, AbstractCache.class, "T");
        return (Class<T>)classes;
    }

    public T get(String key) {
        return this.iCache.get(key);
    }

    public void put(String key, T obj) {
        this.iCache.put(key, obj);
    }

    public void put(String key, T obj, long expireTime) {
        this.iCache.put(key, obj, expireTime);
    }

    public void clear() {
        this.iCache.clear();
    }

    public void remove(String key) {
        this.iCache.remove(key);
    }

    public boolean containKey(String key) {
        return this.iCache.containKey(key);
    }


    public boolean replace(String key, T obj) {
        return this.iCache.replace(key, obj);
    }

    public Map<String, T> getAll() {
        return this.iCache.getAll();
    }

    public long size() {
        return this.iCache.size();
    }

}
