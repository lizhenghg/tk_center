package com.web.tk_communicate.http;

import com.google.common.collect.Lists;
import com.web.tk.common.tk_common.annotation.HttpRouterInfo;
import com.web.tk.common.tk_common.classLoader.ClassHelper;
import com.web.tk.common.tk_common.thread.ThreadClient;
import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk_communicate.config.HttpConfig;
import com.web.tk_communicate.http.container.HttpContainer;
import com.web.tk_communicate.http.handler.AbstractHttpWorkerHandler;
import com.web.tk_communicate.http.handler.HttpChannelInitializer;
import com.web.tk_communicate.http.handler.IHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

/**
 * Http对外暴露类，提供http各种服务
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static HttpContainer container = new HttpContainer();

    private static volatile boolean initialized = false;

    /**
     * 对外启动方法，这里不进行多线程处理
     * @param basePath
     */
    public void init(String basePath) {
        if (initialized) {
            return;
        }
        Assert.notEmpty(basePath, "configPath must not be null");
        //加载tk_commnunicate项目的配置文件
        HttpConfig.init(basePath);
        /**
         * 启动tk_commnunicate的http服务
         */
        int processNum = Runtime.getRuntime().availableProcessors();
        this.init(HttpConfig.getInstance().getChannelReadTimeout(), HttpConfig.getInstance().getChannelWriteTimeout(),
                processNum, processNum << 1, processNum << 1, null);
        //添加http监听
        if (this.addListen(new InetSocketAddress(HttpConfig.getInstance().getDefaultPort())) != 0) {
            throw new IllegalStateException("http-service init fail, can't bind the specified port: "
                    + HttpConfig.getInstance().getDefaultPort());
        }
        //加载指定路径的Handler
        this.initHttpHandler(HttpConfig.getInstance().getHandlerPackage());
        initialized = true;
    }


    /**
     * 扫描并加载HttpWorkerHandler
     * @param handlerPackage
     */
    private void initHttpHandler(String handlerPackage) {
        ThreadClient threadClient = ThreadClient.getInstance();
        threadClient.startTask(ClassHelper.class, "scanClasses", new Object[] {handlerPackage});
        Set<Class<?>> set = (Set<Class<?>>) threadClient.getResult();
        threadClient.close();

        if (!Assert.isNotNull(set)) {
            return;
        }
        for (Class<?> clazz : set) {
            if (clazz.isInterface()
                    || clazz.isAnonymousClass()
                    || clazz.isAnnotation()
                    || clazz.isPrimitive()
                    || clazz.isEnum()) {
                continue;
            }
            if (clazz.isAnnotationPresent(HttpRouterInfo.class)) {
                try {
                    AbstractHttpWorkerHandler httpWorkerHandler = (AbstractHttpWorkerHandler) clazz.newInstance();
                    httpWorkerHandler.registerToServer(this);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.warn(clazz.getName() + " init fail：{}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 接收网络连接事件的工作线程
     */
    private EventLoopGroup bossGroup;

    /**
     * 网络建立连接后，处理网络数据读写的工作线程
     */
    private EventLoopGroup workerGroup;

    /**
     * 执行请求处理任务线程组
     */
    private EventExecutorGroup requestGroup = null;

    /**
     * 服务启动器
     */
    private ServerBootstrap bootstrap;

    /**
     * 监听网络信号通道流
     */
    private List<Channel> listenChannels;


    /**
     *
     * @param channel_read_timeout      通道读取超时时长
     * @param channel_write_timeout     通道写入超时时长
     * @param listeners                 网络连接监听线程数
     * @param workers                   网络读写线程数
     * @param requests                  请求数
     * @param fileDataDir               文件数据存储目录
     */
    private void init(int channel_read_timeout, int channel_write_timeout,
                      int listeners, int workers, int requests, String fileDataDir) {
        LOGGER.info("start to init httpServer");
        bossGroup = new NioEventLoopGroup(listeners);
        workerGroup = new NioEventLoopGroup(workers);
        listenChannels = Lists.newArrayList();

        if (requests > 0) {
            requestGroup = new DefaultEventExecutorGroup(requests);
        }

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                                               .childHandler(new HttpChannelInitializer(requestGroup, channel_read_timeout, channel_write_timeout))
                                               .option(ChannelOption.SO_BACKLOG, 2 << 6)
                                               .childOption(ChannelOption.SO_KEEPALIVE, true);
        //服务启动后删除已经退出的临时文件
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = fileDataDir;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = fileDataDir;

        LOGGER.info("httpServer init succefully");
    }

    /**
     * 添加网络地址监听
     * @param address
     * @return
     */
    private int addListen(InetSocketAddress address) {
        try {
            //A ChannelFuture represents an I/O operation which has not yet occurred. It means,
            //any requested operation might not have been performed yet because all operations are asynchronous in Netty
            ChannelFuture future = bootstrap.bind(address).sync();
            listenChannels.add(future.channel());
            return 0;
        } catch (InterruptedException e) {
            LOGGER.error("Bind socket address fail,[address: {},port: {}]", address.getHostName(), address.getPort());
            return 1;
        }
    }

    /**
     * 执行netty端口监听
     */
    public void run() {
        try {
            for (Channel channel : listenChannels) {
                //等待直到该connection关闭
                //在这里不会关闭，除非手动关闭服务器或者服务器崩了
                channel.closeFuture().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 注册HttpWorkerHandler对象到指定容器
     * @param method
     * @param uri
     * @param handler
     * @return
     */
    public int registerHttpWorkerHandler(String method, String uri, IHandler handler) {
        return container.registerHttpWorkerHandler(method, uri, handler);
    }

    /**
     * 注册HttpWorkerHandlerMethodEntry对象到指定容器
     * @param method
     * @param uri
     * @param methodHandler
     * @return
     */
    public int registerHttpWorkerHandlerMethod(String method, String uri, Method methodHandler) {
        return container.registerHttpWorkerHandlerMethod(method, uri, methodHandler);
    }
}