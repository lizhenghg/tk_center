package com.web.tk.mq.vo;

import java.io.Serializable;

/**
 * point-to-point模式下消费者VO，对应现实中的指定的application
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-2
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ConsumerVO implements Serializable {

    private String name;
    private String callbackUrl;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("ConsumerVO[name=%s, callbackUrl=%s, description=%s]", getName(),
                getCallbackUrl(), getDescription());
    }
}
