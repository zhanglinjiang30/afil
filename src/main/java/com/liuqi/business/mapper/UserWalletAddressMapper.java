package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserWalletAddressModel;
import com.liuqi.business.model.UserWalletAddressModelDto;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWalletAddressMapper extends BaseMapper<UserWalletAddressModel,UserWalletAddressModelDto>{


    int removeById(Long id);
}
