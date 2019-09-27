package com.web.tk_communicate.http.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 实现ChannelInitializer的子类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private EventExecutorGroup requestGroup = null;
    private int channelReadTimeout = 0;
    private int channelWriteTimeout = 0;

    public HttpChannelInitializer(EventExecutorGroup requestGroup, int channelReadTimeout, int channelWriteTimeout) {
        this.requestGroup = requestGroup;
        this.channelReadTimeout = channelReadTimeout;
        this.channelWriteTimeout = channelWriteTimeout;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new ReadTimeoutHandler(this.channelReadTimeout));
        pipeline.addLast(new WriteTimeoutHandler(this.channelWriteTimeout));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator((2 << 20) * 5)); //1024 * 1024 * 10
        pipeline.addLast("encoder", new HttpResponseEncoder());

        //如果不需要自动对内容进行压缩，将下面这一行注释
        pipeline.addLast("deflater", new HttpContentCompressor());

        if (requestGroup == null) {
            pipeline.addLast("handler", new HttpHandler());
        } else {
            pipeline.addLast(requestGroup, "handler", new HttpHandler());
        }
    }
}