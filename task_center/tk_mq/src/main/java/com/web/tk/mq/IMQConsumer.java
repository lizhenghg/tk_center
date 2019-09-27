package com.web.tk.mq;

import javax.jms.MessageListener;

/**
 * 消息中心顶级父类消费者接口
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface IMQConsumer {

    /**
     * 设置消息监听
     * @param messageListener
     */
    void setMessageListener(MessageListener messageListener);

    /**
     * 启动监听
     */
    void start();

    /**
     * 关闭监听
     */
    void shutdown();

}