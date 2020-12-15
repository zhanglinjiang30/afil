package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserWalletLogModel;
import com.liuqi.business.model.UserWalletLogModelDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface UserWalletLogMapper extends BaseMapper<UserWalletLogModel,UserWalletLogModelDto>{

    BigDecimal getMoneyByOrderId(Long orderId);
}
