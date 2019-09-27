package com.web.tk.common.tk_common.config;

import com.web.tk.common.tk_common.exception.CommonException;
import com.web.tk.common.tk_common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置文件操作抽象类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-18
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfig.class);

    protected final Map<String, String> setting = new ConcurrentHashMap<>();

    private String filePath;

    private AbstractConfig() {
    }

    public AbstractConfig(String filePath) {
        this.filePath = filePath;
        init(filePath);
    }

    /**
     * 使用线程同步读取配置文件
     * 该方法不支持子类重写
     *
     * @param filePath
     */
    private synchronized void init(String filePath) {
        if (Assert.isEmpty(filePath)) return;
        File file = null;
        file = new File(filePath);
        if (file.exists() && file.isFile()) {
            try (InputStream input = new FileInputStream(file)) {
                Properties properties = new Properties();
                properties.load(input);
                //遍历配置文件加入到Map中进行缓存
                Enumeration<?> propertyNames = properties.propertyNames();
                while (propertyNames != null &&
                        propertyNames.hasMoreElements()) {
                    String key = (String) propertyNames.nextElement();
                    String value = properties.getProperty(key);
                    setting.put(key, value);
                    LOGGER.info("load config：{}, key：{}, value：{}", filePath, key, value);
                }
            } catch (IOException exception) {
                if (exception instanceof FileNotFoundException) {
                    throw new CommonException("FileNotFoundException", exception);
                } else {
                    throw new CommonException("IOException", exception);
                }
            }
        }
    }

    /**
     * 重新加载配置文件，当配置文件有变更时，动态通知
     *
     * @param configPath
     */
    public void reload(String configPath) {
        init(filePath);
    }

    public String getStringSetting(String key) {
        return this.setting.get(key);
    }

    public int getIntSetting(String key) {
        try {
            return Integer.parseInt(this.setting.get(key));
        } catch (Exception ex) {
            throw new CommonException("value is not int", ex);
        }
    }

    public boolean getBooleanSetting(String key) {
        try {
            return Boolean.getBoolean(this.setting.get(key));
        } catch (Exception ex) {
            throw new CommonException("value is not boolean", ex);
        }
    }

    /**
     * 设置配置文件参数，该方法不常用，应少用
     *
     * @param key
     * @param value
     */
    public void setSetting(String key, String value) {
        this.setting.put(key, value);
    }

    //下面的留给子类自己实现
    public abstract long getLongSetting(String key);

    public abstract byte[] getByteSetting(String key, String charset);
}
