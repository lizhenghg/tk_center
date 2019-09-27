package com.web.tk_communicate.http.container;

import com.web.tk_communicate.http.handler.IHandler;


/**
 * HttpWorkerHandler实体
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpWorkerHandlerEntry {

    private String method;
    private String uri;
    /**
     * Http请求处理器接口
     */
    private IHandler httpWorkerHandler;

    public HttpWorkerHandlerEntry(String method, String uri, IHandler handler) {
        if (!"GET".equals(method)
                && !"POST".equals(method)
                && !"PUT".equals(method)
                && !"DELETE".equals(method)) {
            throw new IllegalArgumentException("HttpWorkerHandlerEntry, method should be one of GET|POST|PUT|DELETE");
        }
        this.method = method;
        this.uri = uri;
        this.httpWorkerHandler = handler;
    }
    public boolean matchEntry(String method, String uri) {
        return method.equals(this.method) && uri.equals(this.uri);
    }

    public IHandler getHttpWorkerHandler() {
        return this.httpWorkerHandler;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}
