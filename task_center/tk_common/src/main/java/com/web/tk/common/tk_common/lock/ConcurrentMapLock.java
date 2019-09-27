package com.web.tk.common.tk_common.lock;

import com.web.tk.common.tk_common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步锁操作接口实现类，非分布式锁
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-25
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ConcurrentMapLock implements ILock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentMapLock.class);
    private static final Integer DEFAULT_LOCK_VALUE = 1;
    private Map<String, Object> lockMap = null;

    public ConcurrentMapLock() {
        lockMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean lock(String key) {
        return lock(key, DEFAULT_MAX_BLOCKING_TIME);
    }

    @Override
    public boolean lock(String key, long expireTime) {
        if (expireTime <= 0) {
            throw new IllegalArgumentException("expireTime is negative!");
        }
        Assert.notEmpty(key, "lock error：key is empty!");
        //重试次数,至少1次
        int retryingCount = ((int)(expireTime / DEFAULT_BLOCKING_INTERVAL)) + 1;

        while (true) {
            if (lockMap.putIfAbsent(key, DEFAULT_LOCK_VALUE) == null) {
                //锁定key成功，返回true
                return true;
            }
            //先执行自减再判断
            if (--retryingCount <= 0) {
                return false;
            }
            try {
                Thread.sleep(DEFAULT_BLOCKING_INTERVAL);
            } catch (InterruptedException e) {
                LOGGER.error(String.format("fail to sleep, key: %s", key), e);
                return false;
            }
        }
    }

    @Override
    public boolean lockWithoutBlock(String key) {
        Assert.notEmpty(key, "lockWithoutBlock error：key is empty!");
        if (lockMap.putIfAbsent(key, DEFAULT_LOCK_VALUE) == null) {
            return true;
        }
        return false;
    }

    @Override
    public void unlock(String key) {
        Assert.notEmpty(key, "unlock error：key is empty!");
        if (!lockMap.remove(key, DEFAULT_LOCK_VALUE)) {
            LOGGER.warn("fail to unlock, key：{}", key);
        }
    }
}
