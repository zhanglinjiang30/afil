package com.liuqi.token;

import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.UserModel;

public interface TokenManager {

    /**
     * 创建用户token
     * @return
     */
    String getToken(Long passphraseId, String deviceId);

    /**
     * 刷新用户
     * @param token
     * @return
     */
    void refreshUserToken(String token);
    /**
     * 用户退出登录
     * @param token
     */
    void loginOff(String token);

    /**
     * 获取登录信息
     * @param token
     * @return
     */
    LoginDto getDeviceIdByToken(String token);

    /**
     * 获取用户utoken
     * @return
     */
    String getUserTokenByUserId(String deviceId);

}
