package com.web.tk.common.tk_common.classLoader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.web.tk.common.tk_common.exception.CommonException;
import com.web.tk.common.tk_common.validate.Assert;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 在当前类中查找指定的类型，如：模板参数类型
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-27
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ClassHelper implements IClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassHelper.class);

    //考虑到ClassHelper为单例，在多线程场景下不能共享Set<String>，所以使用ThreadLocal.本例使用场景不多，多线程不激烈
    private static ThreadLocal<Set<String>> folderRecorder = new ThreadLocal<>();

    private static ThreadLocal<Map<String, String>> initialMap = new ThreadLocal<>();

    private static Set<String> getSet() {
        Set<String> value;
        return (value = folderRecorder.get()) == null ? buildSet() : value;
    }

    private static Set<String> buildSet() {
        Set<String> set = Sets.newLinkedHashSet();
        folderRecorder.set(set);
        return set;
    }

    private static Map<String, String> getMap() {
        Map<String, String> value;
        return (value = initialMap.get()) == null ? buildMap() : value;
    }

    private static Map<String, String> buildMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        initialMap.set(map);
        return map;
    }


    /**
     * 根据指定对象、Class、泛型字义，获取该Class对象的泛型参数对应的实体，返回该实体Class
     * sample：
     * public class EstablishPointTableCache extends AbstractCache<EstablishPointTableVO> {}
     * public abstract class AbstractCache<T>{}
     * <p>
     * Object object对应：EstablishPointTableCache
     * parameterizedSuperClass对应：AbstractCache的Class
     * typeParamName对应："T"
     *
     * @param object
     * @param parameterizedSuperClass
     * @param typeParamName
     * @return
     */
    @Override
    public Class<?> seekClass(final Object object, Class<?> parameterizedSuperClass,
                              String typeParamName) {
        final Class<?> thisClass = object.getClass();
        Class<?> superClass = thisClass.getSuperclass();
        //判断传进来的前面两个参数是否继承关系
        if (superClass != parameterizedSuperClass) {
            return Object.class;
        }
        Type genericSuperType;
        //判断parameterizedSuperClass是否泛型，不是的话直接抛异常
        if (!((genericSuperType = thisClass.getGenericSuperclass())
                instanceof ParameterizedType)) {
            LOGGER.error(parameterizedSuperClass + " must be ParameterizedType");
            throw new IllegalArgumentException(parameterizedSuperClass + " must be ParameterizedType");
        }

        //判断parameterizedSuperClass泛型对应的泛型字义(一般是T、K、V、E...)
        TypeVariable<?>[] typeParams = superClass.getTypeParameters();
        int typeParamIndex = -1;
        for (int i = 0, m = typeParams.length; i < m; i++) {
            if (typeParamName.equals(typeParams[i].getName())) {
                typeParamIndex = i;
                break;
            }
        }
        if (typeParamIndex == -1) {
            LOGGER.error("invaild param：{}, it must be the same as：{}", typeParamName, parameterizedSuperClass + "'TypeParameters");
            throw new IllegalArgumentException("invaild param：" + typeParamName);
        }
        //这里开始解析获取EstablishPointTableVO，得到的是EstablishPointTableVO的Class
        //正常情况是这样的：public class EstablishPointTableCache extends AbstractCache<EstablishPointTableVO>
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperType).getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[typeParamIndex];

        //二次判断还属于泛型的话，属于这种情况：
        //public class EstablishPointTableCache<T> extends AbstractCache<EstablishPointTableVO<T>>
        //此时解析获取到的是EstablishPointTableVO<T>
        if (actualTypeArgument instanceof ParameterizedType) {
            actualTypeArgument = ((ParameterizedType) actualTypeArgument).getRawType();  //这里获取EstablishPointTableVO
        }
        if (actualTypeArgument instanceof Class) { //找到了EstablishPointTableVO的Class
            return (Class<?>) actualTypeArgument;
        }
        //判断泛型数组，属于下面情况(不唯一)：
        //public class EstablishPointTableCache<T> extends AbstractCache<EstablishPointTableVO<T>[]>
        //public class EstablishPointTableCache<T> extends AbstractCache<EstablishPointTableVO<String>[]>
        if (actualTypeArgument instanceof GenericArrayType) {
            //这里获取到EstablishPointTableVO<T>或者EstablishPointTableVO<String>
            Type genericComponentType = ((GenericArrayType) actualTypeArgument).getGenericComponentType();
            if (genericComponentType instanceof ParameterizedType) {
                genericComponentType = ((ParameterizedType) genericComponentType).getRawType(); //获取了T或者String的Class
            }
            if (genericComponentType instanceof Class) {
                return (Class<?>) genericComponentType;
            }
        }
        //其他情况<K,V>暂时不作处理(跟业务有悖)
        return fail(parameterizedSuperClass, typeParamName);
    }

    /**
     * 从指定包扫描所有类，返回所有的Class，默认递归扫描
     *
     * @param classPackage
     * @return
     */
    @Override
    public Set<Class<?>> scanClasses(String classPackage) {
        return scanClasses(classPackage, true);
    }

    private Set<Class<?>> scanClasses(String classPackage, boolean recursive) {
        if (Assert.isEmpty(classPackage))
            return Collections.EMPTY_SET;
        Set<Class<?>> classes = Sets.newLinkedHashSet();
        String packageName = classPackage;
        String packageDirName = packageName.replace(".", "/");

        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().
                    getResources(packageDirName);
            while (dirs != null && dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        //获取jar里的一个实体 可以是目录和一些jar包里的其他文件 如META-INF等文件
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.charAt(0) == '/') {
                            name = name.substring(1);
                        }
                        if (name.startsWith(packageDirName)) {
                            int idx = name.lastIndexOf('/');
                            if (idx != -1) {
                                packageName = name.substring(0, idx).replace('/', '.');
                            }
                            if ((idx != -1) || recursive) {
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        classes.add(Class.forName(packageName + '.' + className));
                                    } catch (ClassNotFoundException e) {
                                        LOGGER.warn("ClassNotFoundException, localClass fail：{}", className, e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(String.format("IOException, fail to scan class, please check the package：%s", classPackage), e);
            throw new CommonException("IOException, fail to scan class, please check the package：" + classPackage, e);
        }
        return classes;
    }


    /**
     * 本方法适用单文件夹获取class以及父文件夹中文件夹与class文件共存场景
     *
     * @param packageName = "xx.xx.xx"
     * @param packagePath = "/xx/xx/xx"
     * @param recursive
     * @param classes
     */
    private void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                  final boolean recursive, Set<Class<?>> classes) {
        if (!Assert.isNotNull(getMap())) {
            initialMap.get().put(packageName, packagePath);
        }
        File dir = new File(packagePath);
        if (!(dir.exists() && dir.isDirectory())) {
            return;
        }
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (recursive && pathname.isDirectory()) || (pathname.getName().endsWith(".class"));
            }
        });

        Map<String, String> paramMap = initialMap.get();
        String key = paramMap.keySet().iterator().next();
        String value = paramMap.get(key);

        if (Assert.isNotNull(dirFiles)) {

            String finalPath;

            for (File file : dirFiles) {
                if (file.isDirectory()) {
                    finalPath = file.getPath().replace("\\", "/");
                    String suffixStr = value;
                    if (value.charAt(0) == '/') {
                        suffixStr = value.substring(1);
                    }
                    getSet().add(finalPath.replace(suffixStr, ""));
                    continue;
                }
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader()
                            .loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("ClassNotFoundException, localClass fail：{}", className, e);
                }
            }
            //在一个父文件夹里，先处理完该层全部的class文件，然后再处理子文件夹
            Set<String> classSet;
            if (Assert.isNotNull(classSet = folderRecorder.get())) {
                String relativePath = classSet.iterator().next();
                classSet.remove(relativePath);
                findAndAddClassesInPackageByFile(key + relativePath.replace("/", "."),
                        value + relativePath, recursive, classes);
            }
        } else {
            //防止出现空文件夹情况
            Set<String> iSet = folderRecorder.get();
            if (Assert.isNotNull(iSet)) {
                String relativePath = iSet.iterator().next();
                iSet.remove(relativePath);
                findAndAddClassesInPackageByFile(key + relativePath.replace("/", "."),
                        value + relativePath, recursive, classes);
            }
        }
    }


    /**
     * 使用Javassist的字节码技术根据Method获取方法的参数列表
     * @param method
     * @return
     * @throws NotFoundException
     */
    public List<String> getParameterNamesByAsm(Method method) throws NotFoundException {
        if (method.getParameterCount() == 0) {
            return Collections.EMPTY_LIST;
        }
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(method.getDeclaringClass().getName());
        CtMethod[] arrayM = ctClass.getDeclaredMethods(method.getName());
        CtMethod ctMethod = arrayM[0];

        boolean flag = true;
        if (arrayM.length > 1) {
            for (CtMethod cm : arrayM) {
                if (cm.getName().equals(method.getName())
                        && cm.getParameterTypes().length == method.getParameterCount()) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (Assert.isNotNull(paramTypes)) {
                        CtClass[] ctparamTypes = cm.getParameterTypes();
                        for (int i = 0, len = paramTypes.length; i < len; i++) {
                            if (paramTypes[i].getName().equalsIgnoreCase(ctparamTypes[i].getName())) {
                            } else {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            ctMethod = cm;
                            break;
                        }
                    }
                }
            }
        }
        if (!flag) {
            throw new NotFoundException("couldn't find the method:" + method.getName());
        }
        //通过javassist的反射方法获取方法的参数名
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

        List<String> parameterNames = Lists.newArrayList();
        int len = ctMethod.getParameterTypes().length;
        int pos = 0;

        //非static方法要这样子处理，找到table中this之后的参数位置，从那里开始读取
        if (!Modifier.isStatic(ctMethod.getModifiers())) {
            for (int i = 0, count = attribute.tableLength(); i < count; i++) {
                if ("this".equalsIgnoreCase(attribute.variableName(i))) {
                    pos = i + 1;
                    break;
                }
            }
        }
        for (int i = 0; i < len; i++) {
            parameterNames.add(attribute.variableName(pos + i));
        }
        return parameterNames;
    }

    private Class<?> fail(Class<?> type, String typeParamName) {
        throw new IllegalStateException("cannot determine the type of the parameter '" + typeParamName + "'：" + type);
    }

}

