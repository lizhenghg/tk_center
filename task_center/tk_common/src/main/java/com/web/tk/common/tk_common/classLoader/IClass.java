package com.web.tk.common.tk_common.classLoader;

import javassist.NotFoundException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 类工具操作接口
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface IClass {

    public Class<?> seekClass(Object object, Class<?> parameterizedSuperClass, String typeParamName);

    public Set<Class<?>> scanClasses(String classPackage);

    List<String> getParameterNamesByAsm(Method method) throws NotFoundException;

}
