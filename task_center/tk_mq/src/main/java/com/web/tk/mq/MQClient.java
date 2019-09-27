package com.web.tk.mq;

import com.web.tk.mq.activemq.MessageHandler;
import com.web.tk.mq.activemq.MultiThreadMessageListener;
import com.web.tk.mq.factory.PoolConsumer;
import com.web.tk.mq.factory.PoolProducer;
import com.web.tk.mq.factory.SingleConsumer;
import com.web.tk.mq.factory.SingleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.Objects;

/**
 * MQ消息客户端
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class MQClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MQConfig.class);

    private static MQClient instance = null;
    private static volatile boolean initialized = false;

    private final int maxHandleThreads = 5 << 3;

    private IMQProducer producer;
    private IMQConsumer consumer;

    private IMQMessageHandler handler;

    private MQClient() {
        LOGGER.info("init MQClient");

        String queueName = Objects.requireNonNull(MQConfig.getInstance().getMqQueueName(), "value of queueName is null");
        int queueCount = MQConfig.getInstance().getMqQueueCount();

        if (queueCount > 1) {
            String[] queueNames = getQueueNameArray(queueName, queueCount);
            LOGGER.info("init MQClient with multi queue, queuename: {}, count: {}", queueName, queueCount);
            this.producer = new PoolProducer(queueNames);
            this.consumer = new PoolConsumer(queueNames);
        } else {
            LOGGER.info("init MQClient with single queue, queuename: {}", queueName);
            this.producer = new SingleProducer(queueName);
            this.consumer = new SingleConsumer(queueName);
        }
    }

    /**
     * 初始化MQClient，不存在多线程并发场景
     * @param configPath
     */
    public static void init(String configPath) {
        if (initialized)
            return;
        MQConfig.init(configPath);
        instance = new MQClient();
        initialized = true;
    }

    public static MQClient getInstance() {
        return Objects.requireNonNull(instance);
    }

    /**
     * 使用该方法把message传送到mq队列
     * @param mqMessage
     * @return
     */
    public boolean send(MQMessage mqMessage) {
        Objects.requireNonNull(mqMessage);
        this.producer.send(mqMessage);
        return true;
    }

    /**
     * 该方法对外使用，用于启动消费者监听
     * @param handler
     */
    public void setHandler(IMQMessageHandler handler) {
        Objects.requireNonNull(handler);
        this.handler = handler;

        this.consumer.setMessageListener(new MultiThreadMessageListener(new MessageHandler() {
            public void handle(Message message) {
                try {
                    invokeListener(message);
                } catch (JMSException e) {
                    LOGGER.error("fail to invoke handler", e);
                }
            }
        }, this.maxHandleThreads));

        this.consumer.start();
    }

    private void invokeListener(Message message) throws JMSException {
        if (this.handler != null) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            MQMessage mqMessage = (MQMessage) objectMessage.getObject();
            this.handler.handle(mqMessage);
        } else {
            LOGGER.error("handler is null");
        }
    }

    /**
     * 拼接队列数组名
     * @param queueName
     * @param queueCount
     * @return
     */
    private String[] getQueueNameArray(String queueName, int queueCount) {
        String[] queueNames = new String[queueCount];
        for (int i = 0; i < queueCount; i++) {
            queueNames[i] = queueName + "_" + i;
        }
        return queueNames;
    }
}