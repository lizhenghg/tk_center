package com.web.tk.common.tk_common.thread;

/**
 * 任务调度器接口
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-8-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface ITaskProcessor<T> {

    public Object handle(T t);

}
