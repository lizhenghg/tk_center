package com.web.tk_communicate.http.handler;

import com.web.tk_communicate.http.HttpServer;
import com.web.tk_communicate.http.context.HttpContext;
import com.web.tk_communicate.http.result.RenderJson;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

/**
 * Netty：HTTP抽象处理类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractHttpHandler implements IHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpHandler.class);

    @Override
    public void handle(HttpContext ctx) {
        HttpRequest request = ctx.getRequest();
        String method = request.method().toString();
        String uri = request.uri();

        Method methodHandler = HttpServer.container.getHttpWorkerHandlerMethod(method, uri);
        if (methodHandler == null) {
            LOGGER.error("Not found request method. method:{}, uri: {}", method, uri);
            ctx.getResponse().setStatus(BAD_REQUEST);
            ctx.writeResponse();
            return;
        }
        try {
            callFunction(methodHandler, ctx);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.error("AbstractHttpHandler fail to handle: {}", e.getMessage(), e);
            ctx.getResponse().setStatus(INTERNAL_SERVER_ERROR);
            ctx.writeResponse();
        }
    }

    /**
     * 这里抽出来，让后面的子类重写，最后执行的不是这个方法，而是被AbstractHttpHandler子类所重写的方法
     * @param func
     * @param ctx
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    protected void callFunction(Method func, HttpContext ctx)
            throws InvocationTargetException, IllegalAccessException {
        func.invoke(this, ctx);
    }

    /**
     * 结果返回处理类
     * @param ctx
     * @param respObj
     * @param httpCode
     */
    protected void renderJSON(HttpContext ctx, Object respObj, int httpCode) {
        RenderJson renderJson = new RenderJson(respObj, httpCode);
        renderJson.apply(ctx);
    }
}