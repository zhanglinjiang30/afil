package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.mq.dto.ChargeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;

/**
 * Token清除
 */
@Service("tokenClearProducer")
public class TokenClearProducer {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "queueToken")
    private Destination queueToken;
    /**
     * 发布消息
     *
     */
    public void sendClearTokenMessage(String token) {
        jmsTemplate.convertAndSend(queueToken, token);
    }

}
