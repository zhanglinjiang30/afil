package com.liuqi.business.websocket;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.enums.ActiveStatusEnum;
import com.liuqi.business.enums.WebSocketTypeEnum;
import com.liuqi.business.model.AddressHoldingRecordModelDto;
import com.liuqi.business.model.AddressRecordModelDto;
import com.liuqi.business.model.BlockModelDto;
import com.liuqi.business.service.AddressHoldingRecordService;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.BlockService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yeauty.pojo.Session;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
public class ActivePushHandle {

    //期权交易K线连接房间
    private static ConcurrentHashMap<String, Session> optionsMap = new ConcurrentHashMap<>();

    @Autowired
    private AddressRecordService addressRecordService;
    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;

    @Autowired
    public ActivePushHandle(AddressRecordService addressRecordService, AddressHoldingRecordService addressHoldingRecordService) {
        this.addressRecordService = addressRecordService;
        this.addressHoldingRecordService = addressHoldingRecordService;
    }

    public ConcurrentHashMap<String, Session> getOptionMap() {
        return optionsMap;
    }

    public void remove(String token) {
        optionsMap.remove(token);
    }

    public void addOptionsMap(String token, Session session) {
        remove(token);
        if (StringUtils.isNotEmpty(token)) {
            optionsMap.put(token, session);
            LoginDto loginDto = LoginUserTokenHelper.getRedisTokenManager().getDeviceIdByToken(token);
            // 获取当前登录者的显示的地址信息
            AddressHoldingRecordModelDto displayAddress = addressHoldingRecordService.getDisplayAddress(loginDto);
            if (displayAddress != null) {
                AddressRecordModelDto a = addressRecordService.getById(displayAddress.getAddressId(), false);
                if (a != null && a.getActive().compareTo(ActiveStatusEnum.ACTIVING.getCode()) == 0) {
                    sendMessage(session, a.getId(), WebSocketTypeEnum.DO_ACTIVE.getCode());
                }
            }
        }
    }

    public void optionsPush(Long toAddressId) {
        Iterator<String> it = optionsMap.keySet().iterator();
        while (it.hasNext()) {
            String token = it.next();
            LoginDto loginDto = LoginUserTokenHelper.getRedisTokenManager().getDeviceIdByToken(token);
            Session session = optionsMap.get(token);
            if (session.isActive() && session.isOpen()) {
                if (loginDto != null) {
                    // 获取当前登录者的显示的地址信息
                    AddressHoldingRecordModelDto displayAddress = addressHoldingRecordService.getDisplayAddress(loginDto);
                    if (displayAddress != null && displayAddress.getAddressId().compareTo(toAddressId) == 0) {
                        log.info("激活，激活方=" + JSONObject.toJSONString(displayAddress) + "-被激活地址=" + toAddressId);
                        sendMessage(session, toAddressId, WebSocketTypeEnum.DO_ACTIVE.getCode());
                    }
                }
            } else {
                log.info("会话失效，移除,{}" + token);
                optionsMap.remove(token);
            }
        }
    }

    public void fundChangePush(Long addressId) {
        if (optionsMap.size() > 0) {
            optionsMap.forEach((k, v) -> {
                LoginDto loginDto = LoginUserTokenHelper.getRedisTokenManager().getDeviceIdByToken(k);
                if (loginDto != null) {
                    // 获取当前登录者的显示的地址信息
                    AddressHoldingRecordModelDto displayAddress = addressHoldingRecordService.getDisplayAddress(loginDto);
                    if (displayAddress != null && displayAddress.getAddressId().compareTo(addressId) == 0) {
                        sendMessage(v, 1, WebSocketTypeEnum.FUND_CHANGE.getCode());
                    }
                }
            });
        }
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
