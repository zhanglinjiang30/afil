package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserPoolWalletLogModel;
import com.liuqi.business.model.UserPoolWalletLogModelDto;
import com.liuqi.business.model.UserPoolWalletModel;
import com.liuqi.business.model.UserWalletModel;

import java.math.BigDecimal;

public interface UserPoolWalletLogService extends BaseService<UserPoolWalletLogModel,UserPoolWalletLogModelDto>{


    /**
     ** 添加日志
     * @param userId
     * @param currencyId
     * @param money  操作金额
     * @param type   UserWalletLogModel 常量1交易  2转账  3充值  4提现
     * @param remarks   备注
     */
    void addLog(Long userId, Long currencyId, BigDecimal money , Integer type, Long orderId, String remarks,
                UserPoolWalletModel model);

    BigDecimal getMoneyByLogType(Integer type,Long currencyId);
}
