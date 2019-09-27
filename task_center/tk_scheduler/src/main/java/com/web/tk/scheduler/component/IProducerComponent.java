package com.web.tk.scheduler.component;

import com.web.tk.common.tk_common.exception.BusinessException;

/**
 * 生产者操作相关接口
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface IProducerComponent {
    boolean produceMessage(String message, String producer, String point) throws BusinessException;
}
