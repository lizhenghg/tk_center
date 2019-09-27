package com.web.tk_communicate.config;

import com.web.tk.common.tk_common.config.AbstractConfig;
import com.web.tk.common.tk_common.config.ConfigAdapter;

import java.util.Objects;

/**
 * app层面的全局配置文件操作类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-12
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpConfig {

    private static final String CHANNEL_READ_TIMEOUT = "app_channel_read_timeout";
    private static final String CHANNEL_WRITE_TIMEOUT = "app_channel_write_timeout";
    private static final String DEFAULT_PORT = "app_default_port";
    private static final String HANDLER_PACKAGE = "app_handler_package";

    //app配置文件类单例，全局唯一，简单来说，就是一个业务层的XXConfig对应一个配置文件，对应一个AbstractConfig
    private static volatile HttpConfig instance;

    private AbstractConfig configInstance;

    private static String filePath;

    public static HttpConfig getInstance() {
        if (instance == null) {
            synchronized (HttpConfig.class) {
                if (instance == null) {
                    instance = new HttpConfig();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    public static void init(String basePath) {
        filePath = basePath + "app.properties";
    }

    private HttpConfig() {
        this(filePath);
    }

    private HttpConfig(String filePath) {
        configInstance = new ConfigAdapter(filePath);
    }

    public int getChannelReadTimeout() {
        return configInstance.getIntSetting(CHANNEL_READ_TIMEOUT);
    }

    public int getChannelWriteTimeout() {
        return configInstance.getIntSetting(CHANNEL_WRITE_TIMEOUT);
    }

    public int getDefaultPort() {
        return configInstance.getIntSetting(DEFAULT_PORT);
    }

    public String getHandlerPackage() {
        return configInstance.getStringSetting(HANDLER_PACKAGE);
    }

    public void reload() {
        configInstance.reload(filePath);
    }
}