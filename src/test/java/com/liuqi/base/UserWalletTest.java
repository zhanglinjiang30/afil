package com.liuqi.base;

import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.service.UserWalletService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class UserWalletTest extends BaseTest {


    @Autowired
    private UserWalletService userWalletService;


    @Test
    public void test01(){
        Long userId=1L;
        Long currencyId=1L;
        UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId,currencyId);
        System.out.println(wallet.getUsing()+"---"+wallet.getFreeze());

        UserWalletModel wallet1=userWalletService.modifyWallet(userId,currencyId,new BigDecimal("-10"),new BigDecimal("10"));
        System.out.println(wallet1.getUsing()+"---"+wallet1.getFreeze());
    }
}
