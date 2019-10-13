package com.web.tk.scheduler.api.handler;

import com.web.tk.common.tk_common.annotation.HttpMethod;
import com.web.tk.common.tk_common.annotation.HttpRouterInfo;
import com.web.tk.common.tk_common.exception.BusinessException;
import com.web.tk.scheduler.api.response.SimpleResponse;
import com.web.tk.scheduler.component.IProducerComponent;
import com.web.tk.scheduler.component.implement.ProducerComponent;
import com.web.tk_communicate.http.context.HttpContext;
import com.web.tk_communicate.http.handler.AbstractHttpWorkerHandler;

/**
 * 生产者操作相关handler
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */

@HttpRouterInfo(router = "/api/v1/producer")
public class ProducerHandler extends AbstractHttpWorkerHandler {

    private IProducerComponent producerComponent = new ProducerComponent();

    @HttpMethod(uri = "/message", method = HttpMethod.METHOD.POST, status = HttpMethod.STATUS_CODE.OK)
    public SimpleResponse produceMessage(String message, String producer_name, String point, HttpContext context)
            throws BusinessException {
        boolean result = producerComponent.produceMessage(message, producer_name, point);
        return buildSimpleResponse(result);
    }

    private SimpleResponse buildSimpleResponse(boolean result) {
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setResult(result);
        return simpleResponse;
    }
}