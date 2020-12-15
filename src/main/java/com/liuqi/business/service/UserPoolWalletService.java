package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.AssetDto;
import com.liuqi.business.model.UserPoolWalletModel;
import com.liuqi.business.model.UserPoolWalletModelDto;
import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.model.UserWalletModelDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserPoolWalletService extends BaseService<UserPoolWalletModel,UserPoolWalletModelDto>{

    /**
     * 查询用户钱包信息
     * @param userId
     * @param currencyId
     * @return
     */
    UserPoolWalletModelDto getByUserAndCurrencyId(Long userId, Long currencyId);


    /**
     * 查询用户钱包 按钱包模糊查询
     * @param userId
     * @param currencyName
     * @return
     */
    List<UserPoolWalletModelDto> getByUserId(Long userId, String currencyName);

    /**
     * 初始化用户钱包
     * @param userId
     */
    void  insertUserWallet(Long userId);

    /**
     * 添加钱包
     * @param userId
     * @param currencyId
     */
    UserPoolWalletModelDto addWallet(Long userId, Long currencyId);


    void initUserWalletByCurrency(Long currencyId);

    /**
     * 初始化用户钱包
     * @param userId
     */
    void  adminUpdate(UserPoolWalletModel model, Long userId);

    /**
     * 交易买钱包修改
     * @param userId
     * @param currencyId
     * @param tradeCurrencyId
     * @param buyTotal
     * @param price
     * @param rateMoney
     * @param buyWhite
     * @param recordId
     */
    void doBuyWallet(Long userId, Long currencyId, Long tradeCurrencyId, BigDecimal buyTotal, BigDecimal price, BigDecimal rateMoney, boolean buyWhite, Long recordId);
    /**
     * 操作卖家钱包
     *
     * @param userId
     * @param currencyId
     * @param tradeCurrencyId
     * @param tradeQuantity   交易数量
     * @param price           价格
     * @param rateMoney       手续费
     */
    void doSellWallet(Long userId, Long currencyId, Long tradeCurrencyId, BigDecimal tradeQuantity, BigDecimal price, BigDecimal rateMoney, boolean sellWhite, Long recordId);

    /**
     * 修改资产 返回修改后的数据
     * @param userId
     * @param currencyId
     * @param changeUsing   修改的可用值
     * @param changeFreeze  修改的冻结值
     */
    UserPoolWalletModelDto modifyWallet(Long userId,Long currencyId,BigDecimal changeUsing,BigDecimal changeFreeze);

    /**
     * 修改资产 返回修改后的数据
     * @param userId
     * @param currencyId
     * @param changeUsing   修改的可用值
     */
    UserPoolWalletModelDto modifyWalletUsing(Long userId,Long currencyId,BigDecimal changeUsing);

    /**
     * 修改资产 返回修改后的数据
     * @param userId
     * @param currencyId
     * @param changeFreeze  修改的冻结值
     */
    UserPoolWalletModelDto modifyWalletFreeze(Long userId,Long currencyId,BigDecimal changeFreeze);

    List<UserPoolWalletModelDto> getByUserId(Long userId);

    /**
     * @Description 获取某用户团队【包括自己】的某币种的持币量总和
     * @Date 11:17 2020/8/20
     */
    BigDecimal getTeamCoinHold(String treeInfo, Integer treeLevel, Long currencyId);

    AssetDto getTotalWallet(Long userId);

}
