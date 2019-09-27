package com.web.tk_communicate.http.result;

import com.google.gson.Gson;
import com.web.tk.common.tk_common.validate.Assert;
import com.web.tk_communicate.http.context.HttpContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 简单返回结果封装类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-5-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RenderJson extends Result {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenderJson.class);

    private String sendData;

    private HttpResponseStatus responseCode = HttpResponseStatus.OK;

    public RenderJson(Object src) {
        this(src, 200);
    }

    public RenderJson(String jsonString) {
        this.sendData = jsonString;
    }

    public RenderJson(Object src, int httpCode) {
        this.sendData = new Gson().toJson(src);
        responseCode = HttpResponseStatus.valueOf(httpCode);
    }

    public RenderJson(Object src, Type type) {
        this.sendData = new Gson().toJson(src, type);
    }

    @Override
    public void apply(HttpContext ctx) {
        setContentType(ctx.getResponse());
        ctx.getResponse().setStatus(responseCode);
        //支持jsonp返回
        List<String> jsonpVal = ctx.getUriAttribute("jsonp");
        String jsonp = jsonpVal == null ? null : (jsonpVal.size() > 0 ? jsonpVal.get(0) : null);
        if (jsonp == null) {
            List<String> callbackVal = ctx.getUriAttribute("callback");
            jsonp = callbackVal != null ? (callbackVal.size() > 0 ? callbackVal.get(0) : null) : null;
        }
        if (!Assert.isEmpty(jsonp)) {
            sendData = String.format("%s(%s)", jsonp, sendData);
        }
        LOGGER.debug("http-service response info: method={}, uri={}, rtnJson={}", ctx.getRequest().method().toString(),
                ctx.getRequest().uri(), sendData);
        ctx.getResponse().content().writeBytes(sendData.getBytes());
        ctx.writeResponse();
    }
}