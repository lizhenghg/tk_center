package com.web.tk.common.tk_common.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 工厂模式继承类管理线程池
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-8-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ExecutorFactory extends AbstractExecutorFactory {

    public enum DESC {CACHED, SCHEDULED, FIXED, SINGLE}

    private static final int COREPOOLSIZE = Runtime.getRuntime().availableProcessors();
    private static final int NTHREADS = COREPOOLSIZE;

    @Override
    public Executor createExecutor(Enum desc1) {
        Executor executor = null;
        DESC desc = (DESC)desc1;
        switch (desc) {
            case CACHED:
                executor = Executors.newCachedThreadPool();
                break;
            case SCHEDULED:
                executor = Executors.newScheduledThreadPool(COREPOOLSIZE);
                break;
            case FIXED:
                executor = Executors.newFixedThreadPool(NTHREADS);
                break;
            case SINGLE:
                executor = Executors.newSingleThreadExecutor();
                break;
        }
        return executor;
    }

    @Override
    public void shutdown(Executor executor) {
        ExecutorService executorService = (ExecutorService) executor;
        executorService.shutdown();
    }
}