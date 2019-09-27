package com.web.tk.common.tk_common.lock;

/**
 * 同步锁操作接口
 * 锁住指定的String
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-25
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface ILock {

    /**
     * 最大默认阻塞时间，单位是毫秒
     */
    public static final long DEFAULT_MAX_BLOCKING_TIME = 625L << 4;

    /**
     * 阻塞间隔，单位是毫秒
     */
    public static final long DEFAULT_BLOCKING_INTERVAL = 125L << 3;

    /**
     * 阻塞锁，最大阻塞时间为默认值
     * @param key
     * @return
     */
    public abstract boolean lock(String key);

    /**
     * 阻塞锁，最大阻塞时间为expireTime
     * @param key
     * @param expireTime
     * @return
     */
    public abstract boolean lock(String key, long expireTime);

    /**
     * 非阻塞锁，key被占用时直接返回false
     * @param key
     * @return
     */
    public abstract boolean lockWithoutBlock(String key);

    /**
     * 解锁
     * @param key
     */
    void unlock(String key);

}
