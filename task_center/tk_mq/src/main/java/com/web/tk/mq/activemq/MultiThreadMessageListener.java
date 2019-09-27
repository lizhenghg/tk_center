package com.web.tk.mq.activemq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * 消息消费者中使用的多线程消息监听服务
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-5
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class MultiThreadMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadMessageListener.class);

    private MessageHandler messageHandler;

    private final static int DEFAULT_HANDLE_THREAD_POOL = 30;

    private int maxHandleThreads;

    private ExecutorService handleThreadPool;

    public MultiThreadMessageListener(MessageHandler handler) {
        this(handler, DEFAULT_HANDLE_THREAD_POOL);
    }

    public MultiThreadMessageListener(MessageHandler handler, int maxHandleThreads) {
        this.maxHandleThreads = maxHandleThreads;
        this.messageHandler = handler;
        this.handleThreadPool = new FixedAndBlockedThreadPoolExecutor(this.maxHandleThreads);
    }

    //监听程序自动调用该方法，其中的this.messageHandler.handle(message)，该方法在MQClient处重写
    @Override
    public void onMessage(Message message) {
        this.handleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                MultiThreadMessageListener.this.messageHandler.handle(message);
            }
        });
    }
}