package com.web.tk.mq.activemq;


import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk.mq.MQConfig;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * jms生产者类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-5
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class JMSProducer implements ExceptionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMSProducer.class);

    //设置最大的连接数
    private final static int DEFAULT_MAX_CONNECTIONS = 5;

    //设置每个连接中使用的最大活动会话数
    private final static int DEFAULT_MAX_ACTIVE_SESSION = 100;

    //线程池数量
    private static final int DEFAULT_THREAD_POOL_SIZE = 30;

    //使用异步发送方式
    private static final boolean DEFAULT_USE_ASYNC_SEND_FOR_JMS = true;

    //是否持久化消息
    private static final boolean DEFAULT_IS_PERSISTENT = true;

    //url连接地址
    private final static String BROKER_URL = MQConfig.getInstance().getMqBrokerUrl();

    //用户名
    private final static String USERNAME = MQConfig.getInstance().getMqUsername();

    //密码
    private final static String PASSWORD = MQConfig.getInstance().getMqPassword();

    //生产者线程池
    private ExecutorService threadPool;

    //Active连接工厂
    private PooledConnectionFactory connectionFactory;

    public JMSProducer() {
        init();
    }

    private void init() {
        this.threadPool = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        //ActiveMQ的连接工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKER_URL);
        activeMQConnectionFactory.setSendAcksAsync(DEFAULT_USE_ASYNC_SEND_FOR_JMS);
        //Active中的连接池工厂
        this.connectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        this.connectionFactory.setCreateConnectionOnStartup(true);
        this.connectionFactory.setMaxConnections(DEFAULT_MAX_CONNECTIONS);
        this.connectionFactory.setMaximumActiveSessionPerConnection(DEFAULT_MAX_ACTIVE_SESSION);
    }

    /**
     * 发送Map消息
     * @param queue
     * @param object
     */
    public void send(final String queue, final Object object) {
        this.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMsg(queue, object);
                } catch (Exception ex) {
                    LOGGER.error("fail to send message, error: {}, queeu: {}", ex.getMessage(), queue, ex);
                }
            }
        });
    }

    /**
     * 真正执行发送消息的方法
     * @param queue
     * @param object
     * @throws Exception
     */
    private void sendMsg(String queue, Object object) throws Exception {
        Connection connection = null;
        Session session = null;
        try {
            connection = this.connectionFactory.createConnection();
            //false参数表示为非事务型消息，后面的参数表示消费端消息的确认类型为自动确认
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queue);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DEFAULT_IS_PERSISTENT ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            Message message;
            if (object instanceof Map) {
                message = getMessage(session, (Map<String, Object>) object);
            } else {
                message = session.createObjectMessage((Serializable) object);
            }
            producer.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            closeSession(session);
            closeConnection(connection);
        }
    }

    private Message getMessage(Session session, Map<String, Object> map) throws JMSException {
        MapMessage message = session.createMapMessage();
        if (Assert.isNotNull(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                message.setObject(entry.getKey(), entry.getValue());
            }
        }
        return message;
    }

    private void closeSession(Session session) {
        try {
            if (session != null) {
                session.close();
            }
        }
        catch (Exception e) {
            LOGGER.error("fail to close session", e);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception e) {
            LOGGER.error("fail to close connection", e);
        }
    }

    @Override
    public void onException(JMSException e) {
        LOGGER.error("exception occur!!!", e);
    }
}
