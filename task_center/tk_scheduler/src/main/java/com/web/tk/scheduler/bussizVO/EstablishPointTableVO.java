package com.web.tk.scheduler.bussizVO;

import com.web.tk.mq.vo.ConsumerVO;
import com.web.tk.mq.vo.ProducerVO;

import java.io.Serializable;
import java.util.Map;

/**
 * 生产者-消费者类
 * <br/>========================================
 * <br/>公司：xxx
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>开发时间：2019-3-14
 * <br/>版本：1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class EstablishPointTableVO implements Serializable {

    private String point;
    private ProducerVO producerVO;
    private Map<String, ConsumerVO> consumers;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public ProducerVO getProducerVO() {
        return producerVO;
    }

    public void setProducerVO(ProducerVO producerVO) {
        this.producerVO = producerVO;
    }

    public Map<String, ConsumerVO> getConsumers() {
        return consumers;
    }

    public void setConsumers(Map<String, ConsumerVO> consumers) {
        this.consumers = consumers;
    }

    @Override
    public String toString() {
        return "SubscriberTableVO [point=" + point + ", producerVO=" + producerVO + ", consumers="
                + consumers + "]";
    }
}