package com.web.tk.mq.factory;

import com.web.tk.mq.IMQConsumer;
import com.web.tk.mq.activemq.JMSConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * 单消费者管理类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-5
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class SingleConsumer implements IMQConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolConsumer.class);

    private JMSConsumer consumer;
    private String queue;

    public SingleConsumer(String queue) {
        this.queue = queue;
        this.consumer = new JMSConsumer(this.queue);
    }

    @Override
    public void setMessageListener(MessageListener messageListener) {
        if (messageListener == null) {
            throw new NullPointerException("messageListener is null");
        }
        this.consumer.setMessageListener(messageListener);
    }

    @Override
    public void start() {
        try {
            this.consumer.start();
        } catch (JMSException e) {
            LOGGER.error("SingleConsumer, fail to start JMSConsumer: {}", e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        this.consumer.shutdown();
    }
}