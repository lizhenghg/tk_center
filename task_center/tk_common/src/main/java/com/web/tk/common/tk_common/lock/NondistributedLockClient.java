package com.web.tk.common.tk_common.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

/**
 *
 * 同步锁客户端,非分布式
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-25
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class NondistributedLockClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NondistributedLockClient.class);
    private static NondistributedLockClient instance = null;
    private ILock lockComponent = null;

    private NondistributedLockClient() {
        lockComponent = new ConcurrentMapLock();
    }

    public static NondistributedLockClient getInstance() {
        if (instance == null) {
            synchronized (NondistributedLockClient.class) {
                if (instance == null) {
                    LOGGER.info("init lockClient successfully");
                    instance = new NondistributedLockClient();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    public boolean lock(String key) {
        return lockComponent.lock(key);
    }

    public boolean lock(String key, long expireTime) {
        return lockComponent.lock(key, expireTime);
    }

    public boolean lockWithoutBlock(String key) {
        return lockComponent.lockWithoutBlock(key);
    }

    public void unlock(String key) {
        lockComponent.unlock(key);
    }

}