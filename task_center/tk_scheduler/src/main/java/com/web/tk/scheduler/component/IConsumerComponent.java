package com.web.tk.scheduler.component;

import com.web.tk.common.tk_common.exception.BusinessException;
import com.web.tk.mq.vo.ConsumerVO;

/**
 * 消费者操作相关接口
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface IConsumerComponent {

    boolean establishPoint(String point, String consumerName, String callback_url) throws BusinessException;

    ConsumerVO unEstablishPoint(String point, String consumerName) throws BusinessException;
}
