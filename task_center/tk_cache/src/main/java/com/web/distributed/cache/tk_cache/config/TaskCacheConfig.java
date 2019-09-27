package com.web.distributed.cache.tk_cache.config;

import com.web.tk.common.tk_common.config.ConfigAdapter;
import com.web.tk.common.tk_common.config.AbstractConfig;

import java.util.Objects;

/**
 * app层面的全局cache配置文件操作类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-4-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class TaskCacheConfig {

    private static final String CACHE_REDIS_HOST = "redis_host";
    private static final String CACHE_REDIS_SENTINEL_NAME = "redis_sentinel_name";
    private static final String CACHE_REDIS_IS_CLUSTER = "redis_is_cluster";
    static final private String SCAN_CACHE_PACKAGE = "scan_cache_package";
    static final private String CACHE_CLASS = "cache_class";
    static final private String CACHE_JVM_CLASS = "cache_jvm_class";
    static final private String CACHE_VO_PACKAGE  = "cache_vo_package";

    //cache配置文件类单例，全局唯一，简单来说，就是一个业务层的XXConfig对应一个配置文件，对应一个AbstractConfig
    private static volatile TaskCacheConfig instance = null;

    private AbstractConfig configInstance = null;

    private static String filePath;

    //对外启动点
    public static void init(String basePath) {
        filePath = basePath + "cache.properties";
    }

    private TaskCacheConfig() {
        this(filePath);
    }

    private TaskCacheConfig(String filePath) {
        configInstance = new ConfigAdapter(filePath);
    }

    public static TaskCacheConfig getInstance() {
        if (instance == null) {
            synchronized (TaskCacheConfig.class) {
                if (instance == null) {
                    instance = new TaskCacheConfig();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    public String getCacheRedisHost() {
        return configInstance.getStringSetting(CACHE_REDIS_HOST);
    }

    public String getCacheRedisSentinelName() {
        return configInstance.getStringSetting(CACHE_REDIS_SENTINEL_NAME);
    }

    public boolean getCacheRedisIsCluster() {
        return configInstance.getBooleanSetting(CACHE_REDIS_IS_CLUSTER);
    }

    public String getScanCachePackage() {
        return configInstance.getStringSetting(SCAN_CACHE_PACKAGE);
    }

    public String getCacheVoPackage() {
        return configInstance.getStringSetting(CACHE_VO_PACKAGE);
    }

    public String getCacheClass() {
        return configInstance.getStringSetting(CACHE_CLASS);
    }

    public String getJvmCacheClass() {
        return configInstance.getStringSetting(CACHE_JVM_CLASS);
    }

    //重新加载
    public void reload() {
        configInstance.reload(filePath);
    }
}