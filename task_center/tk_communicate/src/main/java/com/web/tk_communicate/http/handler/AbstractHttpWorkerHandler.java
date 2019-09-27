package com.web.tk_communicate.http.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.web.tk.common.tk_common.annotation.HttpMethod;
import com.web.tk.common.tk_common.annotation.HttpRouterInfo;
import com.web.tk.common.tk_common.classLoader.ClassClient;
import com.web.tk.common.tk_common.codec.BUSINESS_CODE;
import com.web.tk.common.tk_common.codec.HTTP_CODE;
import com.web.tk.common.tk_common.exception.BadRequestException;
import com.web.tk.common.tk_common.exception.BusinessException;
import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk_communicate.http.HttpServer;
import com.web.tk_communicate.http.context.HttpContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * 业务层处理器的抽象类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractHttpWorkerHandler extends AbstractHttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpWorkerHandler.class);

    private Map<String, List<String>> hsMethodParams = Maps.newLinkedHashMap();
    private Gson gson = new Gson();

    public AbstractHttpWorkerHandler() {
    }

    private void initMethod(Method method) {
        HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
        String funcKey = String.format("%s_%s_%s_%s", method.getName(),
                httpMethod.uri(), httpMethod.method(), method.getParameterCount());
        try {
            hsMethodParams.put(funcKey, ClassClient.getParameterNamesByAsm(method));
        } catch (NotFoundException e) {
            LOGGER.error("get parameter failed：" + e.getMessage(), e);
        }
    }

    /**
     * 把HttpWorkerHandler注册到HttpContainer容器中
     * 以少侵入方式处理注册：方法传参！
     * @param server
     */
    public void registerToServer(HttpServer server) {
        Method[] mts = this.getClass().getDeclaredMethods();
        if (!Assert.isNotNull(mts)) {
            LOGGER.warn("{}: method not exists", this.getClass().getName());
            return;
        }
        HttpRouterInfo httpRouterInfo = this.getClass().getAnnotation(HttpRouterInfo.class);
        String router = httpRouterInfo.router();
        for (Method method : mts) {
            if (method.isAnnotationPresent(HttpMethod.class)) {
                initHttpContainer(router, method, server);
                initMethod(method);
            }
        }
    }

    /**
     * 初始化httpContainer
     * @param router
     * @param method
     * @param server
     */
    private void initHttpContainer(String router, Method method, HttpServer server) {
        HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
        String uri = httpMethod.uri();
        String mt = httpMethod.method().toString();
        if (router.indexOf("/") != 0) {
            router = "/" + router;
        }
        if (router.lastIndexOf("/") != router.length() - 1) {
            router += "/";
        }
        String newURI = router + uri;
        newURI = newURI.replace("//", "/");
        if (newURI.lastIndexOf("/") == newURI.length() - 1) {
            newURI = newURI.substring(0, newURI.length() - 1);
        }
        server.registerHttpWorkerHandler(mt, newURI, this);
        server.registerHttpWorkerHandlerMethod(mt, newURI, method);
    }


    @Override
    protected void callFunction(Method func, HttpContext ctx)
            throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = ctx.getRequest();
        Map<String, String> postParamMap = null;
        if (request instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) request;
            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                String body = content.toString(CharsetUtil.UTF_8);
                if (!Assert.isEmpty(body)) {
                    LOGGER.debug("get map from json, json: {}", body);
                    Type type = new TypeToken<Map<String, String>>() {}.getType();
                    postParamMap = gson.fromJson(body, type);
                }
            }
        }
        String clientIP = (String) request.headers().get("X-Forwarded-For");
        if (clientIP == null || clientIP.isEmpty()) {
            InetSocketAddress address =(InetSocketAddress) ctx.getContext().channel().remoteAddress();
            clientIP = address.getAddress().getHostAddress();
        }
        LOGGER.debug("incoming request! process function={}, method={}, uri={}, remote Addr={}, userAgent={}",
                func.getName(), request.method().toString(), request.uri(), clientIP, request.headers().get("User-Agent"));
        //分析方法的参数
        String funcKey = String.format("%s_%s_%s_%s", func.getName(),
                func.getAnnotation(HttpMethod.class).uri(), request.method().toString(), func.getParameterCount());
        List<String> paramKeyList = hsMethodParams.get(funcKey);
        List<Object> paramValueList = Lists.newArrayList();

        if (Assert.isNotNull(paramKeyList)) {
            int i = 0;
            for (Class<?> paramType : func.getParameterTypes()) {
                String paramKey = paramKeyList.get(i++);
                //先判断是否在uri上
                List<String> uriValues = ctx.getUriAttribute(paramKey);
                String value = uriValues == null ? null : (uriValues.size() > 0 ? uriValues.get(0) : null);
                if (value == null) {
                    if (postParamMap != null) {
                        value = postParamMap.get(paramKey);
                    }
                }
                if (value == null) {
                    LOGGER.warn("WARN: missing parameter: {}, it might occurs unexpected excption", paramKey);
                }
                try {
                    Object ret = parseVal(paramType.getName(), value, ctx);
                    LOGGER.debug(String.format("get params: paramName=%s, paramType=%s, inputVal=%s, outVal=%s", paramKey, paramType.getName(), value, ret));
                    paramValueList.add(ret);
                } catch (Exception e) {
                    LOGGER.error("request parameter invalid! method={}, uri={}, parameter info:name={}, val={}, type={}",
                            request.method().toString(), request.uri(), paramKey, value, paramType.getName());
                    BusinessException badRequest = new BadRequestException(BUSINESS_CODE.PARAMER_INVAILD,
                            String.format("参数转换异常:请求参数=%s,送入参数=%s,参数类型=%s", paramKey, value, paramType.getName()));
                    throw new InvocationTargetException(badRequest);
                }
            }
        }
        //调用方法
        Object result = func.invoke(this, paramValueList.toArray());
        //调用成功时的返回值
        int httpCode;

        switch (func.getAnnotation(HttpMethod.class).status()) {
            case CREATED:
                httpCode = HTTP_CODE.CREATED;
                break;
            case ACCEPTED:
                httpCode = HTTP_CODE.ACCEPTED;
                break;
            case NO_CONTENT:
                httpCode = HTTP_CODE.NO_CONTENT;
                break;
            default:
                httpCode = HTTP_CODE.OK;
        }
        //请求处理成功
        renderJSON(ctx, result, httpCode);
    }

    /**
     * 使用两个ThreadLocal，这里假设客户端线程是从线程池中创建，就算最后线程不销毁，也不用remove之前set的属性
     * 相同线程就可以避免二次创建
     */

    private static ThreadLocal<DateFormat> dtFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private static ThreadLocal<DateFormat> dtTimeFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };


    /**
     * 根据参数Type，转换为指定的参数.
     * Notice：这里可以转换为自定义的类，不需要根据val的值！只需要根据paramType即可！
     * @param paramType
     * @param val
     * @param ctx
     * @return Object
     * @throws ParseException
     */
    private Object parseVal(String paramType, String val, HttpContext ctx)
            throws ParseException {
        Object ret;
        final Double db;
        switch (paramType) {
            case "short":
            case "java.lang.Short":
                db = Double.parseDouble(val);
                ret = db.shortValue();
                break;
            case "int":
            case "java.lang.Integer":
                db = Double.parseDouble(val);
                ret = db.intValue();
                break;
            case "long":
            case "java.lang.Long":
                db = Double.parseDouble(val);
                ret = db.longValue();
                break;
            case "float":
            case "java.lang.Float":
                db = Double.parseDouble(val);
                ret = db.floatValue();
                break;
            case "double":
            case "java.lang.Double":
                ret = Double.parseDouble(val);
                break;
            case "boolean":
            case "java.lang.Boolean":
                ret = Boolean.getBoolean(val);
                break;
            case "Date":
            case "java.util.Date":
                if (val.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    ret = dtTimeFormat.get().parse(val);
                } else {
                    ret = dtFormat.get().parse(val);
                }
                break;
            case "com.web.tk_communicate.http.context.HttpContext":
                ret = ctx;
                break;
            default:
                ret = val;
                break;
        }
        return ret;
    }
}