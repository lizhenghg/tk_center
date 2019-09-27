package com.web.tk_communicate.http.container;

import java.lang.reflect.Method;

/**
 * HttpWorkerHandlerMethod实体
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpWorkerHandlerMethodEntry {

    private String method;
    private String uri;
    /**
     * http请求处理器方法
     */
    private Method methodHandler;

    public HttpWorkerHandlerMethodEntry(String method, String uri, Method methodHandler) {
        if (!"GET".equals(method)
                && !"POST".equals(method)
                && !"PUT".equals(method)
                && !"DELETE".equals(method)) {
            throw new IllegalArgumentException("HttpWorkerHandlerMethodEntry, method should be one of GET|POST|PUT|DELETE");
        }
        this.method = method;
        this.uri = uri;
        this.methodHandler = methodHandler;
    }

    public boolean matchEntry(String method, String uri) {
        return method.equals(this.method) && uri.equals(this.uri);
    }

    public Method getMethodHandler() {
        return this.methodHandler;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}