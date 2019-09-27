package com.web.tk.mq.activemq;


import com.web.tk.mq.MQConfig;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Objects;

/**
 * jms消费者类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-5
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class JMSConsumer implements ExceptionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMSConsumer.class);

    //队列预取值，设置消费者消息缓冲区的大小
    private static final int DEFAULT_QUEUE_PREFETCH = 25 << 2;

    private static final String BROKER_URL = MQConfig.getInstance().getMqBrokerUrl();

    private static final String USERNAME = MQConfig.getInstance().getMqUsername();

    private static final String PASSWORD = MQConfig.getInstance().getMqPassword();

    private MessageListener messageListener;

    private Connection connection;

    private Session session;

    private String queue;

    public JMSConsumer(String queue) {
        this.queue = Objects.requireNonNull(queue);
    }

    /**
     * 启动执行监听
     * @throws JMSException
     */
    public void start() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKER_URL);
        this.connection = connectionFactory.createConnection();

        //activeMQ预取策略
        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setQueuePrefetch(DEFAULT_QUEUE_PREFETCH);
        ((ActiveMQConnection)this.connection).setPrefetchPolicy(prefetchPolicy);
        this.connection.setExceptionListener(this);
        this.connection.start();

        //会话采用非事务级别，消息到达机制使用自动通知机制
        this.session = this.connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(this.queue);
        MessageConsumer consumer = this.session.createConsumer(destination);
        consumer.setMessageListener(Objects.requireNonNull(this.messageListener));
    }

    /**
     * 增加消息监听对象
     * @param messageListener
     */
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * 关闭连接
     */
    public void shutdown() {
        try {
            if (this.session != null) {
                this.session.close();
                this.session = null;
            }
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("fail to shutdown queue, queue", this.queue), ex);
        }
    }

    @Override
    public void onException(JMSException e) {
        LOGGER.error(String.format("exception occur!!!, queue: %s", queue), e);
    }
}