package com.web.tk.mq.activemq;

import javax.jms.Message;

/**
 * 消息回调接口
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-6
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface MessageHandler {
    public void handle(Message message);
}
