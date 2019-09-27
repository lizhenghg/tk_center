package com.web.tk.common.tk_common.serialize;

/**
 * 序列化顶级接口
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-28
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface ISerialize {

    public abstract byte[] serialize(Object object);

    public abstract <T> T deSerialize(Class<T> clazz, byte[] bytes);

}
