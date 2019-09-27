package com.web.tk_communicate.http.result;

import com.web.tk_communicate.http.context.HttpContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

/**
 * 返回结果抽象类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class Result {

    public abstract void apply(HttpContext ctx);

    protected void setContentType(FullHttpResponse response) {
        response.headers().set(CONTENT_TYPE, "application/json;charset=UTF-8");
    }

    protected String getEncoding() {
        return CharsetUtil.UTF_8.name();
    }
}
