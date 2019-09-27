package com.web.tk.mq.factory;

import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk.mq.IMQConsumer;
import com.web.tk.mq.activemq.JMSConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * 消费者组管理类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-3
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class PoolConsumer implements IMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolConsumer.class);

    private JMSConsumer[] consumers;

    private String[] queueNames;

    private int queueSize;

    public PoolConsumer(String[] queueNames) {
        if (!Assert.isNotNull(queueNames)) {
            LOGGER.error("fail to construct PoolConsumer, queueName array is null");
            throw new IllegalArgumentException("queueName array is null");
        }
        this.queueNames = queueNames;
        this.queueSize = queueNames.length;

        this.consumers = new JMSConsumer[queueSize];
        for (int i = 0; i < queueSize; i++) {
            this.consumers[i] = new JMSConsumer(this.queueNames[i]);
        }
    }

    @Override
    public void setMessageListener(MessageListener messageListener) {
        if (messageListener == null) {
            throw new NullPointerException("messageListener is null");
        }
        for (JMSConsumer consumer : consumers) {
            consumer.setMessageListener(messageListener);
        }
    }

    @Override
    public void start() {
        try {
            for (JMSConsumer consumer : consumers) {
                consumer.start();
            }
        } catch (JMSException e) {
            LOGGER.error("PoolConsumer, fail to start JMSConsumer: {}", e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        for (JMSConsumer consumer : consumers) {
            consumer.shutdown();
        }
    }
}