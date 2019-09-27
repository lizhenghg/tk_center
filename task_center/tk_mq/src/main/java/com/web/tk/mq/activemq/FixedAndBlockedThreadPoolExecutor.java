package com.web.tk.mq.activemq;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 简单支持堵塞固定大小线程池操作类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-5-5
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class FixedAndBlockedThreadPoolExecutor extends ThreadPoolExecutor {

    private Lock lock = new ReentrantLock();

    private Condition condition = this.lock.newCondition();

    public FixedAndBlockedThreadPoolExecutor(int size) {
        super(size, size, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }

    /**
     * 当线程池中没有空闲线程时,会挂起此方法的调用线程.直到线程池中有线程有空闲线程
     * @param command
     */
    @Override
    public void execute(Runnable command) {
        this.lock.lock();
        super.execute(command);
        try {
            if (getPoolSize() == getMaximumPoolSize()) {
                this.condition.await();
            }
        } catch (InterruptedException ex) {
            ex.getStackTrace();
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * 线程操作完毕后会执行该方法
     * @param r
     * @param t
     */
    @Override
    public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        try {
            this.lock.lock();
            this.condition.signal();
        } finally {
            this.lock.unlock();
        }
    }
}