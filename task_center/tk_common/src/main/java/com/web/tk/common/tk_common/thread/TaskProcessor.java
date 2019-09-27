package com.web.tk.common.tk_common.thread;

import com.web.tk.common.tk_common.validate.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 简单任务调度实现类
 * <br/>===============================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-8-27
 * <br/>JDK版本：1.8
 */
public class TaskProcessor<T> implements ITaskProcessor, Callable<Object> {

    private T processor;

    private String method;

    private Object[] objects;

    private Object ret;

    public TaskProcessor(T processor, String method) {
        this(processor, method, new Object[] {});
    }

    public TaskProcessor(T processor, String method, Object...objects) {
        this.processor = processor;
        this.method = method;
        this.objects = objects;
    }

    public Object getRet() {
        return this.ret;
    }

    public void setRet(Object ret) {
        this.ret = ret;
    }

    @Override
    public Object handle(Object object) {
        Class<?> clazz = (Class<?>) object;
        Method[] methods = Assert.requireNonEmpty(clazz.getDeclaredMethods(),
                "object must not be null or empty");
        Method method = null;
        for (Method mt : methods) {
            if (mt.getName().equals(this.method)
                    && mt.getParameterCount() == objects.length) {
                method = mt;
                break;
            }
        }
        if (method != null) {
            try {
                return method.invoke(clazz.newInstance(), objects);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Object call() throws Exception {
        return handle(processor);
    }
}