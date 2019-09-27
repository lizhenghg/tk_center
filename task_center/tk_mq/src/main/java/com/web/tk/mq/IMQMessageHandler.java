package com.web.tk.mq;

/**
 * 消息中心专用消息接收回调接口
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-30
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface IMQMessageHandler {
    public void handle(MQMessage mqMessage);
}
