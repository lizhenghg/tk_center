package com.web.tk_communicate.http.context;


/**
 * 当前线程HttpContext变量操作类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-20
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpThreadLocalContext {

    /**
     * 把当前http请求上下文保存在本地线程中
     */
    private static ThreadLocal<HttpContext> localContext = new ThreadLocal<>();

    //jdk1.8，不用先set再get.直接get也不会报异常
    public static HttpContext getContext() {
        return localContext.get();
    }

    public static void setContext(HttpContext context) {
        localContext.set(context);
    }
    /**
     * 注意凡是从线程池中创建的线程，一定要在方法结束时候执行remove()方法，因为该线程不一定会被线程池销毁，不被销毁意味着这条线程
     * set的ThreadLocal.ThreadLocalMap中的数据依然存在
     */
    public static void remove() {
        localContext.remove();
    }
}