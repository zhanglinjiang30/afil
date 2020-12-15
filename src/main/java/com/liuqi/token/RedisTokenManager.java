package com.liuqi.token;

import com.liuqi.base.BaseConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.UserModel;
import com.liuqi.mq.TokenClearProducer;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenManager implements TokenManager {


    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private TokenClearProducer tokenClearProducer;

    public String getBaseKey() {
        return KeyConstant.KEY_TOKEN + "_";
    }

    /**
     * 获取用户token  10分钟
     * @return
     */
    @Override
    public String getToken(Long passphraseId, String deviceId) {
        //判断用户是否有token
        String token = this.getUserTokenByUserId(passphraseId.toString());
        //存在token的  退出之前的
        if (StringUtils.isNotEmpty(token)) {
            this.loginOff(token);
        }
        //获取新的token
        token = MD5Util.MD5Encode(passphraseId + "_" + deviceId + "_" + System.currentTimeMillis());
        String key = getBaseKey() + token;
        while (redisRepository.hasKey(key)) {
            token = MD5Util.MD5Encode(passphraseId + "_" + deviceId + "_" + System.currentTimeMillis());
            key = getBaseKey() + token;
        }
        //token
        redisRepository.hset(key, "user", LoginDto.builder().passphraseId(passphraseId).deviceId(deviceId).build());
        //记录 userId对应token  不过期
        redisRepository.set(getBaseKey() + passphraseId, token);
        return token;
    }

    @Override
    public void refreshUserToken(String token) {
        String key = getBaseKey() + token;
//        if (redisRepository.hasKey(key)) {
//            redisRepository.expire(key, BaseConstant.TOKEN_SESSION_TIME, TimeUnit.MINUTES);
//        }
    }

    @Override
    public void loginOff(String token) {
        LoginDto loginDto = this.getDeviceIdByToken(token);
        String key = getBaseKey() + token;
        redisRepository.del(key);
        //删除对应信息
        if (loginDto != null) {
            redisRepository.del(getBaseKey() + loginDto.getPassphraseId());
            tokenClearProducer.sendClearTokenMessage(token);
        }
    }


    /**
     * 设置值到session中
     *
     * @param token
     * @param paramKey
     * @param paramValue
     * @return
     */
    public boolean setAttribute(String token, String paramKey, Object paramValue) {
        String key = getBaseKey() + token;
        return redisRepository.hset(key, paramKey, paramValue, BaseConstant.TOKEN_SESSION_TIME, TimeUnit.MINUTES);
    }

    /**
     * 在session中获取值
     *
     * @param token
     * @param paramKey
     * @return
     */
    public Object getAttribute(String token, String paramKey) {
        String key = getBaseKey() + token;
        if (redisRepository.hasKey(key)) {
            return redisRepository.hget(key, paramKey);
        }
        return null;
    }

    @Override
    public LoginDto getDeviceIdByToken(String token) {
        String key = getBaseKey() + token;
        if (redisRepository.hasKey(key)) {
            Object obj = redisRepository.hget(key, "user");
            if (obj != null) {
                return (LoginDto) obj;
            }
        }
        return null;
    }

    @Override
    public String getUserTokenByUserId(String deviceId) {
        String token = redisRepository.getString(getBaseKey() + deviceId);
        return StringUtils.isNotEmpty(token) ? token : "";
    }
}
