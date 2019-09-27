package com.web.tk.common.tk_common.serialize;

import com.web.tk.common.tk_common.exception.CommonException;
import com.web.tk.common.tk_common.lock.NondistributedLockClient;
import com.web.tk.common.tk_common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * kryo序列化客户端
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-4-28
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class SerializeClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializeClient.class);

    private static ISerialize serialize = null;

    private static NondistributedLockClient lockClient = NondistributedLockClient.getInstance();

    private static final Map<String, ISerialize> STORAGEMAP = new ConcurrentHashMap<>(16);

    //这里的classPath将长久保存在NondistributedLockClient的ConcurrentHashMap中，不影响业务
    public static void init(String classPackage) {

        if (Assert.isEmpty(classPackage))
            throw new CommonException("invalid classPackage：" + classPackage);

        if (STORAGEMAP.get(classPackage) != null) {
            return;
        }

        if (lockClient.lockWithoutBlock(classPackage)) {
            serialize = new KryoSerialize(classPackage);
            STORAGEMAP.put(classPackage, serialize);
        } else {
            LOGGER.warn("The classPackage has been inited previously：{}", classPackage);
        }
    }

    public static ISerialize getSerialize(String classPackage) {
        ISerialize serialize = STORAGEMAP.get(classPackage);
        try {
            return serialize == null ? null : serialize;
        } catch (Exception e) {
            LOGGER.error("getSerialize, Exception：{}", e.getMessage(), e);
            throw new CommonException(String.format("getSerialize, Exception：{}", e.getMessage()), e);
        }
    }
}
