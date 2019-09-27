package com.web.tk.common.tk_common.validate;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * String和Object value判断类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-12
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class Assert {

    private static final String QUOTES = "";

    public static boolean isEmpty(String strs) {
        return strs == null ? true : QUOTES.equals(strs.trim()) ? true : false;
    }

    public static boolean isNotNull(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Optional) {
            return ((Optional) object).isPresent(); //返回true，非null
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) != 0;
        } else if (object instanceof CharSequence) {
            return ((CharSequence) object).length() > 0;
        } else if (object instanceof Collection) {
            return !((Collection) object).isEmpty();
        } else {
            return object instanceof Map ? !((Map) object).isEmpty() : false;
        }
    }

    public static void notEmpty(String strs, String message) {
        if (strs == null || QUOTES.equals(strs.trim())) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> T requireNonEmpty(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        if (isNotNull(obj)) {
            return obj;
        }
        throw new IllegalArgumentException(message);
    }
}