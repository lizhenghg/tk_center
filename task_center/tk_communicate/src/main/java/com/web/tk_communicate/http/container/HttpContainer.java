package com.web.tk_communicate.http.container;

import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk_communicate.http.handler.IHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * http容器操作类，保存所有的HttpWorkerHandler和Method
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpContainer.class);

    //HttpWorkerHandler对象容器
    private final List<HttpWorkerHandlerEntry> handlerEntries = new ArrayList<>();

    //HttpWorkerHandlerMethod方法容器
    private final List<HttpWorkerHandlerMethodEntry> handlerMethodEntries = new ArrayList<>();

    /**
     * 注册HttpWorkerHandler对象到指定容器
     * @param method
     * @param uri
     * @param handler
     * @return
     */
    public int registerHttpWorkerHandler(String method, String uri, IHandler handler) {
        final HttpWorkerHandlerEntry httpWorkerHandlerEntry =
                new HttpWorkerHandlerEntry(method, uri, handler);
        if (Assert.isNotNull(handlerEntries)) {
            for (HttpWorkerHandlerEntry entry : handlerEntries) {
                if (entry.matchEntry(method, uri)) {
                    LOGGER.warn("HttpContainer can't add the same HttpWorkerHandlerEntry: method：{}, uri：{}", method, uri);
                    return 1;
                }
            }
            handlerEntries.add(httpWorkerHandlerEntry);
        } else {
            handlerEntries.add(httpWorkerHandlerEntry);
        }
        return 0;
    }

    /**
     * 注册HttpWorkerHandlerMethodEntry对象到指定容器
     * @param method
     * @param uri
     * @param methodHandler
     * @return
     */
    public int registerHttpWorkerHandlerMethod(String method, String uri, Method methodHandler) {
        final HttpWorkerHandlerMethodEntry httpWorkerHandlerMethodEntry =
                new HttpWorkerHandlerMethodEntry(method, uri, methodHandler);
        if (Assert.isNotNull(handlerMethodEntries)) {
            for (HttpWorkerHandlerMethodEntry entry : handlerMethodEntries) {
                if (entry.matchEntry(method, uri)) {
                    LOGGER.warn("HttpContainer can't add the same HttpWorkerHandlerMethodEntry: method：{}, uri：{}", method, uri);
                    return 1;
                }
            }
            handlerMethodEntries.add(httpWorkerHandlerMethodEntry);
        } else {
            handlerMethodEntries.add(httpWorkerHandlerMethodEntry);
        }
        return 0;
    }

    /**
     * 根据method和uri获取到指定的请求处理器
     * @param method
     * @param uri
     * @return
     */
    public IHandler getHttpWorkerHandler(String method, String uri) {
        if (Assert.isNotNull(handlerEntries)) {
            if (handlerEntries instanceof RandomAccess) {
                for (HttpWorkerHandlerEntry entry : handlerEntries) {
                    if (entry.getMethod().equals(method) && entry.getUri().equals(uri)) {
                        return entry.getHttpWorkerHandler();
                    }
                }
            } else {
                for (Iterator<HttpWorkerHandlerEntry> itor = handlerEntries.iterator();
                     itor.hasNext(); ) {
                    HttpWorkerHandlerEntry entry = itor.next();
                    if (entry.getMethod().equals(method) && entry.getUri().equals(uri)) {
                        return entry.getHttpWorkerHandler();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据method和uri获取到指定的方法请求处理器
     * @param method
     * @param uri
     * @return
     */
    public Method getHttpWorkerHandlerMethod(String method, String uri) {
        if (Assert.isNotNull(handlerMethodEntries)) {
            if (handlerMethodEntries instanceof RandomAccess) {
                for (HttpWorkerHandlerMethodEntry entry : handlerMethodEntries) {
                    if (entry.getMethod().equals(method) && entry.getUri().equals(uri)) {
                        return entry.getMethodHandler();
                    }
                }
            } else {
                for (Iterator<HttpWorkerHandlerMethodEntry> itor = handlerMethodEntries.iterator();
                     itor.hasNext(); ) {
                    final HttpWorkerHandlerMethodEntry entry = itor.next();
                    if (entry.getMethod().equals(method) && entry.getUri().equals(uri)) {
                        return entry.getMethodHandler();
                    }
                }
            }
        }
        return null;
    }
}