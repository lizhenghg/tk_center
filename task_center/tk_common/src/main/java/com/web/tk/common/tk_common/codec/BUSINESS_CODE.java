package com.web.tk.common.tk_common.codec;

/**
 * 自定义内部errorCode
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class BUSINESS_CODE {

    /**
     * 用户ID无效
     */
    public static final String UID_INVAILD = "11000001";

    /**
     * 无效参数
     */
    public static final String PARAMER_INVAILD = "11000002";

    /**
     * HTTP请求方法无效
     */
    public static final String HTTP_METHOD_INVALID = "11000003";

    /**
     * 内部服务异常
     */
    public static final String INTERNAL_SERVER_ERROR = "11000004";

    /**
     * 未经过授权
     */
    public static final String UNAUTHORIZED = "11000005";
}
