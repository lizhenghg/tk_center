package com.web.tk.mq.factory;

import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk.mq.IMQProducer;
import com.web.tk.mq.activemq.JMSProducer;

import java.io.Serializable;
import java.util.Map;

/**
 * 单生产者管理类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-5
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class SingleProducer implements IMQProducer {

    private JMSProducer producer;
    private String queue;

    public SingleProducer(String queue) {
        Assert.notEmpty(queue, "queueName is null");
        this.queue =queue;
        this.producer = new JMSProducer();
    }


    @Override
    public void send(Map<String, Object> messageMap) {
        this.producer.send(this.queue, messageMap);
    }

    @Override
    public void send(Serializable messageObj) {
        this.producer.send(this.queue, messageObj);
    }
}