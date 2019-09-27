package com.web.tk.mq;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息中心顶级父类生产者接口
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-30
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface IMQProducer {

    /**
     * 发送Map消息
     * @param messageMap
     */
    void send(Map<String, Object> messageMap);

    /**
     * 发送obj消息
     * @param messageObj
     */
    void send(Serializable messageObj);
}
