package com.web.tk.common.tk_common.classLoader;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 类操作对外调用层
 * NOTICE：该类只适合于web环境下，也就多线程环境下，也就是说，一个线程最多访问一次的情况下，
 *         不支持同一线程多次访问(如main主线程一跑到底，多次调用)！
 *
 *         同一线程多次调用的情况，可以使用ThreadClient进行多线程调用
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ClassClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassClient.class);

    private static IClass iClass = null;

    private static volatile boolean bInit = false;

    private ClassClient() {}

    public synchronized static void init(String basePath) {
        if (bInit) {
            return;
        }
        LOGGER.info("ClassClient init start：{}", basePath);
        iClass = new ClassHelper();
        bInit = true;
        LOGGER.info("ClassClient init end：{}", basePath);
    }

    public static Class<?> seekClass(final Object object, Class<?> parameterizedSuperClass,
                                     String typeParamName) {
        return iClass.seekClass(object, parameterizedSuperClass, typeParamName);
    }

    public static Set<Class<?>> scanClasses(String classPackage) {
        return iClass.scanClasses(classPackage);
    }

    public static List<String> getParameterNamesByAsm(Method method) throws NotFoundException {
        return iClass.getParameterNamesByAsm(method);
    }
}
