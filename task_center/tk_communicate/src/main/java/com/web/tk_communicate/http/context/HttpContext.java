package com.web.tk_communicate.http.context;

import com.web.tk.common.tk_common.validate.Assert;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.SET_COOKIE;


/**
 * HTTP上下文
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-20
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpContext {

    /**
     * Netty处理器上下文
     */
    private ChannelHandlerContext ctx;
    /**
     * Netty Http请求对象
     */
    private HttpRequest request;
    /**
     * Netty Http响应对象
     */
    private FullHttpResponse response;

    /**
     * Cookie
     */
    private Set<Cookie> cookies;
    /**
     * uri请求参数
     */
    private Map<String, List<String>> uriAttributes;

    public HttpRequest getRequest() {
        return this.request;
    }

    public FullHttpResponse getResponse() {
        return this.response;
    }

    public ChannelHandlerContext getContext() {
        return this.ctx;
    }

    /**
     * 构造器
     * @param ctx
     * @param request
     * @param response
     * @param cookies
     * @param uriAttributes
     */
    public HttpContext(ChannelHandlerContext ctx, HttpRequest request, FullHttpResponse response,
                       Set<Cookie> cookies, Map<String, List<String>> uriAttributes) {
        this.ctx = ctx;
        this.request = request;
        this.response = response;
        this.cookies = cookies;
        this.uriAttributes = uriAttributes;
    }


    /**
     * 获取uri属性列表
     * @param key
     * @return
     */
    public List<String> getUriAttribute(String key) {
        if (Assert.isNotNull(uriAttributes)) {
            for (Map.Entry<String, List<String>> entry : uriAttributes.entrySet()) {
                if (entry.getKey().toLowerCase().equals(key.toLowerCase())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 组装Cookie
     */
    private void buildCookie() {
        if (Assert.isNotNull(cookies)) {
            Iterator<Cookie> itor = cookies.iterator();
            while (itor.hasNext()) {
                Cookie cookie = itor.next();
                response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
            }
        }
    }

    /**
     * 返回响应
     */
    @SuppressWarnings("Notice")
    public void writeResponse() {
        //针对长连接不关闭，需要客户端在调用前自行配置是否使用长连接
        //Notice：这里风险很大，由于调用者的误用，容易造成channel不能正常关闭.建议后期加上获取各调用者信息的功能，统计是否正确使用了长连接
        boolean close = (HttpHeaderValues.CLOSE.toString().equals(request.headers().get(CONNECTION)) || request.protocolVersion() == HttpVersion.HTTP_1_0)
                                             && !(HttpHeaderValues.KEEP_ALIVE.toString().equals(request.headers().get(CONNECTION)));
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
        buildCookie();
        ChannelFuture future = ctx.channel().writeAndFlush(response);

        HttpThreadLocalContext.remove();

        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}