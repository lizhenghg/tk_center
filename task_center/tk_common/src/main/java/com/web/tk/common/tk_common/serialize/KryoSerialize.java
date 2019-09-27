package com.web.tk.common.tk_common.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Sets;
import com.web.tk.common.tk_common.classLoader.ClassHelper;
import com.web.tk.common.tk_common.thread.ThreadClient;
import com.web.tk.common.tk_common.validate.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.Set;

/**
 * kryo序列化辅助类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-28
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class KryoSerialize implements ISerialize {

    private static ThreadLocal<Kryo> threadLocalKryo = new ThreadLocal<>();

    private Set<Class<?>> hsClass = Sets.newLinkedHashSet();

    public KryoSerialize(String classPath) {
        ThreadClient threadClient = ThreadClient.getInstance();
        threadClient.startTask(ClassHelper.class, "scanClasses", new Object[]{classPath});
        Set<Class<?>> set = (Set<Class<?>>) threadClient.getResult();

        if (Assert.isNotNull(set)) {
            for (Class<?> clazz : set) {
                if (clazz.isInterface()
                        || clazz.isEnum()
                        || clazz.isPrimitive()
                        || clazz.isAnnotation()
                        || clazz.isAnonymousClass()) {
                    continue;
                }
                addClass(clazz);
            }
        }
    }

    private void addClass(Class<?> clazz) {
        hsClass.add(clazz);
    }

    private Kryo getKryo() {
        Kryo kryo = threadLocalKryo.get();
        if (kryo == null) {
            return buildKryo();
        }
        return kryo;
    }

    private Kryo buildKryo() {
        if (!Assert.isNotNull(hsClass))
            return null;

        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(true);
        int index = 1;
        for (Iterator<Class<?>> itor = hsClass.iterator(); itor.hasNext();) {
            Class<?> loadClass = itor.next();
            kryo.register(loadClass, index++);
        }
        threadLocalKryo.set(kryo);
        return kryo;
    }

    @Override
    public byte[] serialize(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);

        getKryo().writeObject(output, object);

        output.flush();

        return bos.toByteArray();
    }

    @Override
    public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Input input = new Input(bis);

        T outObj = getKryo().readObject(input, clazz);

        return outObj;
    }
}