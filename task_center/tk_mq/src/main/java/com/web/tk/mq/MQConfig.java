package com.web.tk.mq;

import com.web.tk.common.tk_common.config.AbstractConfig;
import com.web.tk.common.tk_common.config.ConfigAdapter;

import java.util.Objects;

/**
 * tk_mq全局配置文件操作类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-27
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MQConfig {

    private static final String MQ_BROKER_URL = "mq_broker_url";

    private static final String MQ_USERNAME = "mq_username";

    private static final String MQ_PASSWORD = "mq_password";

    private static final String MQ_QUEUE_NAME = "mq_queue_name";

    private static final String MQ_QUEUE_COUNT = "mq_queue_count";

    private static volatile MQConfig instance;

    private AbstractConfig configInstance;

    private static String filePath;

    public static void init(String basePath) {
        filePath = basePath + "mq.properties";
    }

    public static MQConfig getInstance() {
        if (instance == null) {
            synchronized (MQConfig.class) {
                if (instance == null) {
                    instance = new MQConfig();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    private MQConfig() {
        this(filePath);
    }

    private MQConfig(String filePath) {
        configInstance = new ConfigAdapter(filePath);
    }

    public String getMqBrokerUrl() {
        return this.configInstance.getStringSetting(MQ_BROKER_URL);
    }

    public String getMqUsername() {
        return this.configInstance.getStringSetting(MQ_USERNAME);
    }

    public String getMqPassword() {
        return this.configInstance.getStringSetting(MQ_PASSWORD);
    }

    public String getMqQueueName() {
        return this.configInstance.getStringSetting(MQ_QUEUE_NAME);
    }

    public int getMqQueueCount() {
        return this.configInstance.getIntSetting(MQ_QUEUE_COUNT);
    }
}
