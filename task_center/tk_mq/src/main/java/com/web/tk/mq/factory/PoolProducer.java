package com.web.tk.mq.factory;

import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk.mq.IMQProducer;
import com.web.tk.mq.activemq.JMSProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * 生产者组管理类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-3
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class PoolProducer implements IMQProducer {

    private final static Logger LOGGER = LoggerFactory.getLogger(PoolProducer.class);
    private JMSProducer[] producers;
    private String[] queueNames;

    private int queueSize;
    private int currentIndex = 0;

    public PoolProducer(String[] queueNames) {
        if (!Assert.isNotNull(queueNames)) {
            LOGGER.error("fail to construct PoolProducer, queueName array is null");
            throw new IllegalArgumentException("queueName array is null");
        }
        this.queueNames = queueNames;
        this.queueSize = queueNames.length;

        //新建生产者数组
        producers = new JMSProducer[queueSize];
        for (int i = 0; i < queueSize; i++) {
            producers[i] = new JMSProducer();
        }
    }


    @Override
    public void send(Map<String, Object> messageMap) {
        int index = selectIndex();
        producers[index].send(queueNames[index], messageMap);
    }

    @Override
    public void send(Serializable messageObj) {
        int index = selectIndex();
        producers[index].send(queueNames[index], messageObj);
    }

    /**
     * 选择一个范围内的位置
     * 不要求绝对的平均分布，不做去重处理，不做分布式同步处理
     * @return
     */
    private int selectIndex() {
        //直接操作this.currentIndex有范围溢出风险
        int currentIndex = this.currentIndex;
        if (currentIndex >= queueSize) {
            currentIndex = 0;
            this.currentIndex = 0;
        }
        this.currentIndex ++;
        return currentIndex;
    }
}