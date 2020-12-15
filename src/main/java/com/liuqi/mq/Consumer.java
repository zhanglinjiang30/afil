package com.liuqi.mq;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.async.AsyncTask;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.constant.MqConstant;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.WalletDoEnum;
import com.liuqi.business.model.BlockModelDto;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.service.ChargeAwardService;
import com.liuqi.business.service.KDataService;
import com.liuqi.business.service.TradeRecordService;
import com.liuqi.business.service.TradeService;
import com.liuqi.business.websocket.ActivePushHandle;
import com.liuqi.business.websocket.BlockPushHandle;
import com.liuqi.mq.dto.*;
import com.liuqi.redis.RedisRepository;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.third.email.AliEmailSender;
import com.liuqi.third.sms.SmsSender;
import com.liuqi.utils.DateTimeUtils;
import io.shardingsphere.api.HintManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 消费者处理消息
 */
@Component
@Slf4j
public class Consumer {

    @Autowired
    private AliEmailSender aliEmailSender;
    @Autowired
    private SmsSender smsSender;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private KDataService kDataService;
    @Autowired
    private ChargeAwardService chargeAwardService;
    @Autowired
    private TradeRecordService tradeRecordService;
    @Autowired
    private BlockPushHandle blockPushHandle;
    @Autowired
    private ActivePushHandle activePushHandle;

    @Lazy
    @Autowired
    private AsyncTask asyncTask;
   /*****邮件***************************************************************************/
   @JmsListener(destination = MqConstant.MQ_DESTINATION_EMAIL)
    public void receiveEmail(String text){
        EmailDto dto= JSONObject.parseObject(text,EmailDto.class);
        try {
            aliEmailSender.sendSimpleMail(dto.getEmail(), dto.getTitle(),dto.getMessage(),dto.getSign());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*****短信***************************************************************************/
    @JmsListener(destination = MqConstant.MQ_DESTINATION_SMS)
    public void receiveSms(String text){
        SmsDto dto= JSONObject.parseObject(text,SmsDto.class);
        try {
            smsSender.sendSms(dto.isChain(), dto.getPhone(),dto.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*****区块信息发送***************************************************************************/
    @JmsListener(destination = MqConstant.MQ_SEND_BLOCK, containerFactory = "jmsListenerContainerTopic")
    public void sendBlock(String text) {
        BlockModelDto blockModelDto = JSONObject.parseObject(text, BlockModelDto.class);
        blockPushHandle.optionsPush(blockModelDto);
    }

    /*****激活地址***************************************************************************/
    @JmsListener(destination = MqConstant.MQ_SEND_ACTIVE, containerFactory = "jmsListenerContainerTopic")
    public void sendActive(String text) {
        activePushHandle.optionsPush(Long.parseLong(text));
    }

    /*****清除Token***************************************************************************/
    @JmsListener(destination = MqConstant.MQ_CLEAR_TOKEN, containerFactory = "jmsListenerContainerTopic")
    public void clearToken(String text) {
        activePushHandle.remove(text);
    }


    /***********钱包修改********************************************************************/
    @JmsListener(destination = MqConstant.MQ_DESTINATION_TRADEWALLETB)
    public void receiveTradeWalletB(String text) {
        //强制使用主库
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        String errorKey = "";
        String errorValue = "";
        TradeWalletDto dto = JSONObject.parseObject(text, TradeWalletDto.class);
        String key = LockConstant.LOCK_TRADERECODE_ID + dto.getRecordId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            tradeService.tradeBuyWallet(dto.getRecordId(), hintManager);
        } catch (Exception e) {
            e.printStackTrace();
            errorKey = KeyConstant.KEY_RECORD_ERROR_TIME + DateTimeUtils.currentDate("MMdd") + dto.getRecordId() + "_" + BuySellEnum.BUY.getCode();
            errorValue = redisRepository.getString(errorKey);
            int time = StringUtils.isNotEmpty(errorValue) ? Integer.valueOf(errorValue) : 0;
            if (time >= 3) {
                TradeRecordModelDto record = tradeRecordService.getById(dto.getRecordId());
                record.setBuyWalletStatus(WalletDoEnum.FAIL.getCode());
                tradeRecordService.update(record);
                redisRepository.del(errorKey);
            }else{
                time++;
                redisRepository.set(errorKey, time + "", 1L, TimeUnit.DAYS);
            }
        } finally {
            RedissonLockUtil.unlock(lock);
            //清除处理标记
            redisRepository.del(KeyConstant.KEY_RECORD_RUN_TIME + dto.getRecordId() + "_" + BuySellEnum.BUY.getCode());
            hintManager.close();
        }
    }

    @JmsListener(destination = MqConstant.MQ_DESTINATION_TRADEWALLETS)
    public void receiveTradeWalletS(String text) {
        String errorKey = "";
        String errorValue = "";
        TradeWalletDto dto = JSONObject.parseObject(text, TradeWalletDto.class);
        String key = LockConstant.LOCK_TRADERECODE_ID + dto.getRecordId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            tradeService.tradeSellWallet(dto.getRecordId());
        } catch (Exception e) {
            e.printStackTrace();
            errorKey = KeyConstant.KEY_RECORD_ERROR_TIME + DateTimeUtils.currentDate("MMdd") + dto.getRecordId() + "_" + BuySellEnum.SELL.getCode();
            errorValue = redisRepository.getString(errorKey);
            int time = StringUtils.isNotEmpty(errorValue) ? Integer.valueOf(errorValue) : 0;
            if (time >= 3) {
                TradeRecordModelDto record = tradeRecordService.getById(dto.getRecordId());
                record.setSellWalletStatus(WalletDoEnum.FAIL.getCode());
                tradeRecordService.update(record);
                redisRepository.del(errorKey);
            }else{
                time++;
                redisRepository.set(errorKey, time + "", 1L, TimeUnit.DAYS);
            }
        } finally {
            RedissonLockUtil.unlock(lock);
            //清除处理标记
            redisRepository.del(KeyConstant.KEY_RECORD_RUN_TIME + dto.getRecordId() + "_" + BuySellEnum.SELL.getCode());
        }
    }

    /***********K线********************************************************************/
    @JmsListener(destination = MqConstant.MQ_DESTINATION_KDATA)
    public void receiveKData(String text) {
        KDataDto dto = JSONObject.parseObject(text, KDataDto.class);
        //实时K线
        kDataService.addCacheCurK(dto.getTradeId(),dto.getTradePrice(),dto.getTradeQuantity());
    }

    /***********买释放********************************************************************/
    @JmsListener(destination = MqConstant.MQ_DESTINATION_BUY_RELEASE)
    public void receiveBuyRelease(String text) {
        ReleaseDto dto = JSONObject.parseObject(text, ReleaseDto.class);
        String key = LockConstant.LOCK_BUY_RELEASE + dto.getTradeId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            asyncTask.receiveLockRelease(dto.getRecordId(), BuySellEnum.BUY.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    /***********卖释放********************************************************************/
    @JmsListener(destination = MqConstant.MQ_DESTINATION_SELL_RELEASE)
    public void receiveSellRelease(String text) {
        ReleaseDto dto = JSONObject.parseObject(text, ReleaseDto.class);
        String key = LockConstant.LOCK_SELL_RELEASE + dto.getTradeId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            asyncTask.receiveLockRelease(dto.getRecordId(), BuySellEnum.SELL.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    /***********上级手续费奖励********************************************************************/
    @JmsListener(destination = MqConstant.MQ_DESTINATION_CHARGE_AWARD)
    public void receiveCharge(String text) {
        ChargeDto dto = JSONObject.parseObject(text, ChargeDto.class);
        String key = LockConstant.LOCK_CHARGE_AWARD_ID + dto.getRecordId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            chargeAwardService.createRecord(dto.getRecordId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }
}
