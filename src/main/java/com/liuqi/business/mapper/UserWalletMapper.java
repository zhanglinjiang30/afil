package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.model.UserWalletModelDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UserWalletMapper extends BaseMapper<UserWalletModel,UserWalletModelDto>{


    UserWalletModelDto getByUserAndCurrencyId(@Param("userId") Long userId, @Param("currencyId") Long currencyId);

    int modifyWallet(@Param("userId") Long userId, @Param("currencyId") Long currencyId, @Param("changeUsing") BigDecimal changeUsing, @Param("changeFreeze") BigDecimal changeFreeze);

    int modifyWalletUsing(@Param("userId") Long userId, @Param("currencyId") Long currencyId, @Param("changeUsing") BigDecimal changeUsing);

    int modifyWalletFreeze(@Param("userId") Long userId, @Param("currencyId") Long currencyId,@Param("changeFreeze") BigDecimal changeFreeze);

    List<UserWalletModelDto> getByUserId(Long userId);

    /**
     * @Description 获取某用户团队【包括自己】的某币种的持币量总和
     * @Date 11:17 2020/8/20
     */
    BigDecimal getTeamCoinHold(String treeInfo, Integer treeLevel, Long currencyId);
}
