package com.liuqi.business.service;

/**
 * @Description 持币算力收益
 * @Date 19:44 2020/8/19
 */
public interface CoinHoldProfitHandleService {

    /**
     * @Description 执行矿池挖矿
     * @Date 10:00 2020/8/24
     */
    void doMining(Long currencyId);

    void userPoolLevel();

}
