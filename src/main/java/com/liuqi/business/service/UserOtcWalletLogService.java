package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserOtcWalletLogModel;
import com.liuqi.business.model.UserOtcWalletLogModelDto;
import com.liuqi.business.model.UserOtcWalletModel;
import com.liuqi.business.model.UserPoolWalletModel;

import java.math.BigDecimal;

public interface UserOtcWalletLogService extends BaseService<UserOtcWalletLogModel,UserOtcWalletLogModelDto>{

    /**
     ** 添加日志
     * @param userId
     * @param currencyId
     * @param money  操作金额
     * @param type   UserWalletLogModel 常量1交易  2转账  3充值  4提现
     * @param remarks   备注
     */
    void addLog(Long userId, Long currencyId, BigDecimal money , Integer type, Long orderId, String remarks,
                UserOtcWalletModel model);

}
