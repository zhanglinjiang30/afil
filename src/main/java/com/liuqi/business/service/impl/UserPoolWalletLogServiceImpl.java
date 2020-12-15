package com.liuqi.business.service.impl;


import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.business.service.TableIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.service.UserPoolWalletLogService;
import com.liuqi.business.mapper.UserPoolWalletLogMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class UserPoolWalletLogServiceImpl extends BaseServiceImpl<UserPoolWalletLogModel, UserPoolWalletLogModelDto> implements UserPoolWalletLogService {

    @Autowired
    private UserPoolWalletLogMapper userPoolWalletLogMapper;

    @Override
    public BaseMapper<UserPoolWalletLogModel, UserPoolWalletLogModelDto> getBaseMapper() {
        return this.userPoolWalletLogMapper;
    }

    @Autowired
    private AddressRecordService addressRecordService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private TableIdService tableIdService;

    @Override
    public void insert(UserPoolWalletLogModel userPoolWalletLogModel) {
        super.insert(userPoolWalletLogModel);
    }

    @Override
    @Transactional
    public void addLog(Long userId, Long currencyId, BigDecimal money, Integer type, Long orderId,
                       String remarks, UserPoolWalletModel wallet) {
        UserPoolWalletLogModelDto log = new UserPoolWalletLogModelDto();
        log.setCurrencyId(currencyId);
        log.setMoney(money);
        log.setUserId(userId);
        log.setType(type);
        log.setRemark(remarks);
        log.setOrderId(orderId);
        log.setBalance(wallet.getUsing());
        log.setSnapshot("可用：" + wallet.getUsing() + ",冻结：" + wallet.getFreeze());
        this.insert(log);
    }


    @Override
    protected void doMode(UserPoolWalletLogModelDto dto) {
        super.doMode(dto);
        dto.setUserName(addressRecordService.getById(dto.getUserId()).getAddress());
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
    }

    @Override
    public BigDecimal getMoneyByLogType(Integer type, Long currencyId) {
        BigDecimal money = userPoolWalletLogMapper.getMoneyByLogType(type, currencyId);
        return money != null ? money : BigDecimal.ZERO;
    }
}
