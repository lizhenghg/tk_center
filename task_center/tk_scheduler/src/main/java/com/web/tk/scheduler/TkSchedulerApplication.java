package com.web.tk.scheduler;

import com.web.distributed.cache.tk_cache.CacheServer;
import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk.mq.MQClient;
import com.web.tk.mq.TaskCenterHandler;
import com.web.tk_communicate.http.HttpServer;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class TkSchedulerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TkSchedulerApplication.class);

    public static void main(String[] args) {

        String appBasePath = "";
        try {
            ProtectionDomain protectionDomain = TkSchedulerApplication.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            appBasePath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), "UTF-8");
        } catch (URISyntaxException | UnsupportedEncodingException ex) {
            LOGGER.error("can't find appBasePath...", ex);
        }

        Assert.notEmpty(appBasePath, "appBasePath must not be empty!");

        String configPath = null;
        File file = new File(appBasePath);
        if (file.exists() && file.isDirectory()) {
            appBasePath = file.getParentFile().getAbsolutePath();
            configPath = String.format("%s/config/", appBasePath);
        }

        //启动全局应用程序log4j
        String log4jPath = String.format("%slog4j.xml", configPath);
        DOMConfigurator.configureAndWatch(log4jPath, 1875 << 5); //60000豪秒

        //tk_cache子模块应用程序初始化
        CacheServer.init(configPath);

        //初始化MQ
        MQClient.init(configPath);
        //设置消费者监听器
        MQClient.getInstance().setHandler(TaskCenterHandler.getInstance());

        //tk_communicate子模块应用程序初始化(初始化Http通讯)
        HttpServer server = new HttpServer();
        server.init(configPath);
        server.run();
    }
}