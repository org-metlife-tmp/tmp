package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchRecvInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayBankService {
    private static final Logger logger = LoggerFactory.getLogger(PayBankService.class);

    public void send(Record record) {
        String request=record.get("request");
        IChannelInter channelInter = null;
        try {
            channelInter = ChannelManager.getInter("102", "BatchRecv");
        } catch (Exception e) {
            logger.error("获取银行原子接口失败！", e);
        }
        final SysBatchRecvInter sysInter = new SysBatchRecvInter();
        sysInter.setChannelInter(channelInter);
        Map<String,Object> request_map = (Map) JSON.parse(request);
        QueueBean bean = new QueueBean(sysInter, request_map, "102");
        ProductQueue productQueue = new ProductQueue(bean);
        new Thread(productQueue).start();
    }


}
