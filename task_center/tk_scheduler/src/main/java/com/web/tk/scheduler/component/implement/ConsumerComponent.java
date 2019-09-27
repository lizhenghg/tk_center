package com.web.tk.scheduler.component.implement;

import com.web.distributed.cache.tk_cache.CacheServer;
import com.web.tk.common.tk_common.exception.BusinessException;
import com.web.tk.common.tk_common.lock.NondistributedLockClient;
import com.web.tk.mq.vo.ConsumerVO;
import com.web.tk.scheduler.bussizCache.EstablishPointTableCache;
import com.web.tk.scheduler.component.IConsumerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 消费者操作接口实现
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-3-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ConsumerComponent implements IConsumerComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerComponent.class);

    //缓存的使用
    private EstablishPointTableCache establishPointTableCache = (EstablishPointTableCache) CacheServer.getService(EstablishPointTableCache.class);

    //非分布式锁
    private NondistributedLockClient client = NondistributedLockClient.getInstance();

    @Override
    public boolean establishPoint(String point, String consumer_name, String callback_url) {
        //具体实现过程有待跟业务商量
        return true;
    }

    @Override
    public ConsumerVO unEstablishPoint(String point, String consumerName) throws BusinessException {
        //具体实现过程有待跟业务商量
        return null;
    }
}