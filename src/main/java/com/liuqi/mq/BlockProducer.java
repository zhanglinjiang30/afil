package com.liuqi.mq;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.model.BlockModel;
import com.liuqi.business.model.BlockModelDto;
import com.liuqi.mq.dto.SmsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.Topic;

/**
 * 生产者 发送一个消息到消息队列
 */
@Service("blockProducer")
public class BlockProducer {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Resource(name = "queueBlock")
    private Topic queueBlock;
    @Resource(name = "queueActive")
    private Topic queueActive;

    /**
     * 发布消息
     */
    public void sendMessage(BlockModelDto blockModelDto) {
        jmsTemplate.convertAndSend(queueBlock, JSONObject.toJSONString(blockModelDto));
    }

    public void sendActiveMessage(Long id) {
        jmsTemplate.convertAndSend(queueActive, String.valueOf(id));
    }
}
