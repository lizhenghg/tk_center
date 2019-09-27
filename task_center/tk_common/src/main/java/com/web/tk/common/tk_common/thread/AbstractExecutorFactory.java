package com.web.tk.common.tk_common.thread;

import java.util.concurrent.Executor;

/**
 * 使用抽象工厂模式管理线程池
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-8-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class AbstractExecutorFactory {

    public final Executor create(Enum desc) {
        Executor executor = createExecutor(desc);
        return executor;
    }

    public abstract Executor createExecutor(Enum desc);

    public abstract void shutdown(Executor executor);

}
