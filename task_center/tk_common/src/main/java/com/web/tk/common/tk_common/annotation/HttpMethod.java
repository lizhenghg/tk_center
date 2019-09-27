package com.web.tk.common.tk_common.annotation;

import java.lang.annotation.*;

/**
 * HTTP接口操作方法注解类，标注接口使用的HTTP方法，默认返回码及URI的匹配规则
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-12
 * <br/>jdk版本：1.8
 * <br/>=================================
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {

    public enum METHOD {GET, POST, HEAD, PUT, OPTIONS, DELETE, TRACE, CONNECT, PATCH}

    public enum STATUS_CODE {OK,  CREATED, ACCEPTED, NO_CONTENT}

    public enum RETURN_TYPE {JSON, TEXT, TEMPLATE}

    /**
     * 每个处理方法可匹配的URI路径，使用全局匹配操作
     * @return
     */
    String uri() default "";

    /**
     * 允许使用的HTTP请求方法，目前暂时支持HTTP操作
     * @return
     */
    METHOD method() default METHOD.GET;

    /**
     * 正常操作后返回的HTTP状态码
     * @return
     */
    STATUS_CODE status() default STATUS_CODE.OK;

    /**
     * 方法排序优先级，数值越小优先级越高
     * @return
     */
    int priority() default 10;

    /**
     * 路径是否正则表达式（使用全路径匹配）
     * @return
     */
    boolean isRegex() default false;

    /**
     * 是否检测请求的会话信息
     * @return
     */
    boolean isCheckSession() default false;

    /**
     * 返回数据类型
     * @return
     */
    RETURN_TYPE returnType() default RETURN_TYPE.JSON;

    /**
     * 模板页面
     * @return
     */
    String template() default "";

}
