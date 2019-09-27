package com.web.tk.mq;

import java.util.Objects;

/**
 * 消息任务执行handler处理中心
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-16
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class TaskCenterHandler implements IMQMessageHandler{

    private volatile static TaskCenterHandler instance = null;
    private static final Object syncObj = new Object();

    public static TaskCenterHandler getInstance() {
        if (instance == null) {
            synchronized (syncObj) {
                if (instance == null) {
                    instance = new TaskCenterHandler();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }


    //这里开始进行消费
    @Override
    public void handle(MQMessage mqMessage) {

    }
}
