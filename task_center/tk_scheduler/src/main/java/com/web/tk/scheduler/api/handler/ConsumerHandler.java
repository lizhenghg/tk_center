package com.web.tk.scheduler.api.handler;

import com.web.tk.common.tk_common.annotation.HttpMethod;
import com.web.tk.common.tk_common.annotation.HttpRouterInfo;
import com.web.tk.common.tk_common.exception.BusinessException;
import com.web.tk.common.tk_common.exception.InternalServerException;
import com.web.tk.mq.vo.ConsumerVO;
import com.web.tk.scheduler.api.response.SimpleResponse;
import com.web.tk.scheduler.component.IConsumerComponent;
import com.web.tk.scheduler.component.implement.ConsumerComponent;
import com.web.tk_communicate.http.handler.AbstractHttpWorkerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者操作相关handler
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-20
 * <br/>jdk版本：1.8
 * <br/>=================================
 */

@HttpRouterInfo(router = "/api/v1/consumer")
public class ConsumerHandler extends AbstractHttpWorkerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHandler.class);
    private IConsumerComponent consumerComponent = new ConsumerComponent();

    @HttpMethod(uri = "/establishPoint", method = HttpMethod.METHOD.POST, status = HttpMethod.STATUS_CODE.OK)
    public SimpleResponse establishPoint(String point, String consumer_name, String callback_url)
            throws BusinessException {
        boolean result = consumerComponent.establishPoint(point, consumer_name, callback_url);
        return buildSimpleResponse(result);
    }

    @HttpMethod(uri = "", method = HttpMethod.METHOD.DELETE, status = HttpMethod.STATUS_CODE.OK)
    public SimpleResponse unEstablishPoint(String point, String consumer_name)
            throws BusinessException {
        ConsumerVO consumerVO = consumerComponent.unEstablishPoint(point, consumer_name);
        if (consumerVO == null) {
            LOGGER.error("consumerVO is null, point: {}, consumer_name: {}", point, consumer_name);
            throw new InternalServerException("10000000", "consumerVO is null");
        }
        return buildSimpleResponse(true);
    }

    private SimpleResponse buildSimpleResponse(boolean result) {
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setResult(result);
        return simpleResponse;
    }
}