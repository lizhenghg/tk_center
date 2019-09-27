package com.web.distributed.cache.tk_cache.jvm;

import com.google.common.collect.Maps;
import com.web.distributed.cache.tk_cache.ICache;
import com.web.tk.common.tk_common.validate.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
public class MemoryCache<T> implements ICache<T> {

    private Map<Object, T> hsMap = null;
    private Lock reentrantLock = null;

    public MemoryCache() {
        init();
    }

    private void init() {
        hsMap = Maps.newHashMap();
        reentrantLock = new ReentrantLock(true);
    }

    @Override
    public void setClassType(Class<T> classType, String cacheName) {
    }

    @Override
    public T get(String key) {
        return hsMap.get(key);
    }

    @Override
    public void put(String key, T obj) {
        try {
            reentrantLock.lock();
            put(key, obj, 0L);
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void put(String key, T obj, long expireTime) {
        hsMap.put(key, obj);
    }

    @Override
    public boolean clear() {
        hsMap.clear();
        return true;
    }

    @Override
    public boolean remove(String key) {
        try {
            reentrantLock.lock();
            return hsMap.remove(key) != null;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public boolean containKey(String key) {
        return hsMap.containsKey(key);
    }

    @Override
    public boolean replace(String key, T obj) {
        try {
            reentrantLock.lock();
            return hsMap.replace(key, obj) != null;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public Map<String, T> getAll() {
        if (!Assert.isNotNull(hsMap)) {
            return Collections.emptyMap();
        }
        try {
            reentrantLock.lock();
            Map<String, T> result = Maps.newHashMap();
            for (Map.Entry<String, T> entry : result.entrySet()) {
                String key = entry.getKey();
                T value = entry.getValue();
                result.put(key, value);
            }
            return result;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public long size() {
        return hsMap.size();
    }
}