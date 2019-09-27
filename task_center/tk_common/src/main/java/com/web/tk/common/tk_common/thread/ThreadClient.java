package com.web.tk.common.tk_common.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 简单线程调度器客户端
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-8-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ThreadClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadClient.class);

    private static volatile ThreadClient client = null;

    private TaskProcessor<Class<?>> processor = null;

    private static final Object sync = new Object();

    private static final int MAX_BLOCK_TIME = 5;

    public static ThreadClient getInstance() {
        if (client == null) {
            synchronized (sync) {
                if (client == null) {
                    client = new ThreadClient();
                }
            }
        }
        return Objects.requireNonNull(client);
    }

    @SuppressWarnings("unused")
    public void startTask(Class<?> clazz, String method) {
        LOGGER.info("start create thread");
        processor = new TaskProcessor(clazz, method);
        excuteExecutor(processor);
    }

    public void startTask(Class<?> clazz, String method, Object ... objects) {
        LOGGER.info("start create thread");
        processor = new TaskProcessor(clazz, method, objects);
        excuteExecutor(processor);
    }

    private void excuteExecutor(Callable<Object> callable) {
        AbstractExecutorFactory factory = new ExecutorFactory();
        ExecutorService executor = (ExecutorService) factory.create(ExecutorFactory.DESC.CACHED);
        Future<Object> future = executor.submit(callable);
        try {
            Object ret = future.get(MAX_BLOCK_TIME, TimeUnit.SECONDS);
            this.processor.setRet(ret);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            factory.shutdown(executor);
        }
    }

    public Object getResult() {
        return this.processor.getRet();
    }

    public void close() {
        this.processor = null;
    }
}