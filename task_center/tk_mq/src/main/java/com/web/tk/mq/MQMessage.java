package com.web.tk.mq;

import java.io.Serializable;

/**
 * MQ消息传输类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class MQMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private Serializable content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Serializable getContent() {
        return content;
    }

    public void setContent(Serializable content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("MQMessage [title:%s, content:%s]", this.getTitle(), this.getContent());
    }
}