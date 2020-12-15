package com.liuqi.business.websocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.WebSocketTypeEnum;
import com.liuqi.business.model.BlockModel;
import com.liuqi.business.model.BlockModelDto;
import com.liuqi.business.service.BlockService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yeauty.pojo.Session;

import javax.print.DocFlavor;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * description: OptionsPushHandle 期权相关推送处理
 * date: 2020/5/23 10:08 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Service
@Slf4j
public class BlockPushHandle {

    //期权交易K线连接房间
    private static Set<Session> optionsMap = new CopyOnWriteArraySet<>();

    @Autowired
    private BlockService blockService;
    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    public BlockPushHandle(BlockService blockService, RedisRepository redisRepository) {
        this.blockService = blockService;
        this.redisRepository = redisRepository;
    }

    public Set<Session> getOptionMap() {
        return optionsMap;
    }

    public void remove(Session session) {
        log.info("==移除session==" + session.hashCode());
        optionsMap.remove(session);
    }

    public void addOptionsMap(Session session) {
        log.info("==添加session==" + session.hashCode());
        optionsMap.remove(session);
        if (session != null) {
            optionsMap.add(session);
            sendMessage(session, initPushData(), WebSocketTypeEnum.BLOCK_INIT_DATA.getCode());
        }
    }

    private JSONObject initPushData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currencyHeight", redisRepository.getLong("lastHeight"));
        BigDecimal serviceCharge = redisRepository.getBigDecimal(ConfigConstant.CONFIG_SERVICE_CHARGE);
        jsonObject.put("seviceCharge", serviceCharge.compareTo(BigDecimal.ZERO) <= 0 ? 0.001 : serviceCharge);
        jsonObject.put("destruction", redisRepository.get(ConfigConstant.CONFIG_BLOCK_SUC));
        jsonObject.put("redidual", redisRepository.get(ConfigConstant.CONFIG_TOTAL_SUC));
        BlockModelDto params = new BlockModelDto();
        params.setSortName("height");
        params.setSortType(" desc limit 10");
        List<BlockModelDto> list = blockService.queryListByDto(params, true);
        jsonObject.put("blocks", list);
        return jsonObject;
    }

    private JSONObject wonBlockPush(BlockModelDto blockModelDto) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("currencyHeight", redisRepository.getLong("lastHeight"));
        BigDecimal serviceCharge = redisRepository.getBigDecimal(ConfigConstant.CONFIG_SERVICE_CHARGE);
        jsonObject.put("seviceCharge", serviceCharge.compareTo(BigDecimal.ZERO) <= 0 ? 0.001 : serviceCharge);
        jsonObject.put("destruction", redisRepository.get(ConfigConstant.CONFIG_BLOCK_SUC));
        jsonObject.put("redidual", redisRepository.get(ConfigConstant.CONFIG_TOTAL_SUC));
        jsonObject.put("block", blockModelDto);
        return jsonObject;
    }

    public void optionsPush(BlockModelDto blockModelDto) {
        Set<Session> delete = new CopyOnWriteArraySet<>();
        for (Session m : optionsMap) {
            if (m.isOpen() && m.isActive()) {
                JSONObject data = wonBlockPush(blockModelDto);
                sendMessage(m, data, WebSocketTypeEnum.BLOCK_WON_DATA.getCode());
            } else {
                log.info("会话失效1，移除,{}" + m.id());
                delete.add(m);
            }
        }
        optionsMap.removeAll(delete);
    }

    public void sendMessage(Session session, Object obj, int code) {
        if (session.isWritable()) {
            session.sendText(JSONObject.toJSONString(ReturnResponse.builder().code(code).obj(obj).time(System.currentTimeMillis()).build()));
        }
    }

//    public void sendAllMessage(Object obj, int code, long tradeId, OptionsTimeModelDto optionsTimeModelDto) {
//        CopyOnWriteArraySet<Session> sessions = getOptionMap().get(tradeId + "-" + optionsTimeModelDto.getId());
//        if (sessions != null && sessions.size() > 0) {
//            sessions.forEach(e -> sendMessage(e, obj, code));
//        }
//    }
//
//    public void sendAllMessage(Object obj, int code, String key) {
//        CopyOnWriteArraySet<Session> sessions = getOptionMap().get(key);
//        if (sessions != null && sessions.size() > 0) {
//            sessions.forEach(e -> sendMessage(e, obj, code));
//        }
//    }
//
//    public void delOptionsMap(long tradeId, Long optionsTimeId, Session session) {
//        String s = tradeId + "-" + optionsTimeId;
//        CopyOnWriteArraySet<Session> sessions = optionsMap.get(s);
//        if (sessions != null) {
//            sessions.remove(session);
//        }
//    }


}
