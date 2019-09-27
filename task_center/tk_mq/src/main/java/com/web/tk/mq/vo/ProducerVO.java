package com.web.tk.mq.vo;

import javax.swing.tree.AbstractLayoutCache;
import java.io.Serializable;

/**
 * point-to-point模式下生产者VO，对应现实中的不同部门/企业...
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-2
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ProducerVO implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("ProducerVO [name=%s, getClass()=%s, hashCode()=%s]", getName(),
                getClass(), hashCode());
    }
}
