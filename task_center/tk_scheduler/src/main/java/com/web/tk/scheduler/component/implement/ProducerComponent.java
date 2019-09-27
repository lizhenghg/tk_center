package com.web.tk.scheduler.component.implement;

import com.web.tk.common.tk_common.exception.BusinessException;
import com.web.tk.scheduler.component.IProducerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生产者操作接口实现
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ProducerComponent implements IProducerComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerComponent.class);

    @Override
    public boolean produceMessage(String message, String producer, String point) throws BusinessException {
        //具体实现过程有待跟业务商量
        return false;
    }
}