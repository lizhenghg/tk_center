package com.web.tk_communicate.http.handler;

import com.web.tk_communicate.http.context.HttpContext;

/**
 * 顶层http请求处理器接口
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-18
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface IHandler {
    public void handle(HttpContext ctx);
}
