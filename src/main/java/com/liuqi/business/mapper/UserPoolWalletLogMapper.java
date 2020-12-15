package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserPoolWalletLogModel;
import com.liuqi.business.model.UserPoolWalletLogModelDto;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;


public interface UserPoolWalletLogMapper extends BaseMapper<UserPoolWalletLogModel,UserPoolWalletLogModelDto>{


    BigDecimal getMoneyByLogType(@Param("type") Integer type,@Param("currencyId") Long currencyId);

}
