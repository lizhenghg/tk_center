package com.web.tk.scheduler.api.response;

import java.io.Serializable;

/**
 * 简单响应类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class SimpleResponse implements Serializable {

    private boolean result;

    public boolean getResult() {
        return this.result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("{\"result\": %s}", result);
    }
}