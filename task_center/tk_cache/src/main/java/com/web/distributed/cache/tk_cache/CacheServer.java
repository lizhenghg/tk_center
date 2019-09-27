package com.web.distributed.cache.tk_cache;

import com.web.distributed.cache.tk_cache.config.TaskCacheConfig;
import com.web.tk.common.tk_common.classLoader.ClassClient;
import com.web.tk.common.tk_common.classLoader.ClassHelper;
import com.web.tk.common.tk_common.exception.CommonException;
import com.web.tk.common.tk_common.lock.NondistributedLockClient;
import com.web.tk.common.tk_common.serialize.SerializeClient;
import com.web.tk.common.tk_common.thread.ThreadClient;
import com.web.tk.common.tk_common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存服务类,用来启动tk_cache子模块的应用程序初始化
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class CacheServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServer.class);

    private static Map<String, AbstractCache<?>> hsCache = null;

    //使用ConcurrentHashMap实现简单的锁机制，处理多线程场景
    private static NondistributedLockClient lockClient = NondistributedLockClient.getInstance();

    private static volatile boolean bInit = false;

    public static void init(String basePath) {
        if (bInit) {
            return;
        }
        if (lockClient.lockWithoutBlock(basePath)) { //同一时刻只能有一个线程获取到锁，其他的获取不到锁就立刻返回false
            LOGGER.info("CacheServer init start");
            bInit = true;
            //加载tk_cache配置文件
            TaskCacheConfig.init(basePath);
            //启动并实例化ClassClient
            ClassClient.init(null);
            //启动并实例化SerializeClient
            SerializeClient.init(TaskCacheConfig.getInstance().getCacheVoPackage());
            //初始化并存储Class对象
            initAndputClass();
            LOGGER.info("CacheServer init end");
            //是否解锁不影响业务，不使用try{}finally{}
            lockClient.unlock(basePath);
        } else {
            LOGGER.error("fail to lock entity, basePath: {}", basePath);
            throw new CommonException("fail to lock entity, basePath：" + basePath);
        }
    }

    private static void initAndputClass() {
        hsCache = new ConcurrentHashMap<>();
        ThreadClient threadClient = ThreadClient.getInstance();
        threadClient.startTask(ClassHelper.class, "scanClasses", new Object[]{TaskCacheConfig.
               getInstance().getScanCachePackage()});
        Set<Class<?>> set = (Set<Class<?>>) threadClient.getResult();

        if (Assert.isNotNull(set)) {
            for (Iterator<Class<?>> itor = set.iterator(); itor.hasNext();) {
                Class<?> clazz = itor.next();
                if (clazz.isInterface()
                        || clazz.isPrimitive()
                        || clazz.isAnnotation()
                        || clazz.isEnum()
                        || clazz.isAnonymousClass()) {
                    continue;
                }
                String superClassName = clazz.getSuperclass().getName();
                if (AbstractCache.class.getName().equals(superClassName)) {
                    try {
                        AbstractCache<?> cacheObj = (AbstractCache<?>) clazz.newInstance();
                        hsCache.put(clazz.getName(), cacheObj);
                    } catch (InstantiationException e) {
                        LOGGER.warn("putClass, InstantiationException：{}", e.getMessage(), e);
                    } catch (IllegalAccessException e) {
                        LOGGER.warn("putClass, IllegalAccessException：{}", e.getMessage(), e);
                    }
                }
            }
        }
    }

    public static AbstractCache<?> getService(Class<?> clazz) {
        return hsCache.get(clazz.getName());
    }
}