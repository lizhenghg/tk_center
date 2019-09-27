package com.web.tk_communicate.http.handler;

import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk_communicate.http.HttpServer;
import com.web.tk_communicate.http.context.HttpContext;
import com.web.tk_communicate.http.context.HttpThreadLocalContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.COOKIE;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Netty：HTTP处理类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        IHandler handler;
        HttpContext httpContext;
        HttpMethod method;
        String uri;

        StopWatch watch = new StopWatch();
        watch.start();
        LOGGER.info("http-service incoming request：method = {}, uri = {}", httpRequest.method(), httpRequest.uri());

        //删除没用的请求
        if (!httpRequest.decoderResult().isSuccess()) {
            watch.stop();
            LOGGER.error("http-service response bad request：method = {}, uri = {}, runTime = {} ms", httpRequest.method(), httpRequest.uri(), watch.getTime());
            sendError(ctx, BAD_REQUEST);
            return;
        }
        //获取Http请求方法
        method = httpRequest.method();
        if (method != GET
                && method != POST
                && method != PUT
                && method != DELETE
                && method != PATCH) {
            watch.stop();
            LOGGER.error("http-service request method illegal：method = {}, uri = {}, runTime = {} ms", httpRequest.method(), httpRequest.uri(), watch.getTime());
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }
        uri = httpRequest.uri();
        //处理无效uri
        if (uri.lastIndexOf("/") < 0) {
            watch.stop();
            LOGGER.error("http-service request uri illegal：method = {}, uri = {}, runTime = {} ms", httpRequest.method(), httpRequest.uri(), watch.getTime());
            sendError(ctx, BAD_REQUEST);
            return;
        }

        //获取指定的请求处理器
        handler = HttpServer.container.getHttpWorkerHandler(method.toString(), uri);
        if (handler == null) {
            watch.stop();
            LOGGER.error("http-service response handler not found: method = {}, uri = {}, runTime = {} ms", httpRequest.method(), httpRequest.uri(), watch.getTime());
            sendError(ctx, NOT_FOUND);
            return;
        }
        //Cookie解码
        Set<Cookie> cookies;
        String cookieValue = (String) httpRequest.headers().get(COOKIE);
        if (Assert.isEmpty(cookieValue)) {
            cookies = Collections.EMPTY_SET;
        } else {
            cookies = ServerCookieDecoder.decode(cookieValue);
        }

        QueryStringDecoder decoderQuery = new QueryStringDecoder(uri);
        Map<String, List<String>> uriAttributes = decoderQuery.parameters();

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        httpContext = new HttpContext(ctx, httpRequest, response, cookies, uriAttributes);
        HttpThreadLocalContext.setContext(httpContext);

        handler.handle(httpContext);

        watch.stop();
        LOGGER.error("http-service end request：method = {}, uri = {}, runTime = {} ms", httpRequest.method(), httpRequest.uri(), watch.getTime());
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("http-service exceptionCaught: error={}", cause.getMessage(), cause);
        ctx.channel().close();
    }
}