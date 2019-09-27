package com.web.tk.common.tk_common.annotation;

import java.lang.annotation.*;

/**
 * HTTP处理类路由信息，将指定URI的请求路由到符合匹配条件的处理类中
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-12
 * <br/>jdk版本：1.8
 * <br/>=================================
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpRouterInfo {

    /**
     * 路由名称，可以作为JS代理插件KEY使用
     * @return
     */
    String name() default "";

    /**
     * 是否生成JS代理类，默认为false，设置为true则会生成各个http接口的代理类，可通过JS接口获取
     * @return
     */
    boolean proxy() default false;

    /**
     * 路由信息
     * @return
     */
    String router() default "";

    /**
     * 允许的HTTP请求方法，默认为GET
     * @return
     */
    String method() default "GET";

}
