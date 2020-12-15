package com.liuqi.base;

import com.liuqi.BaseDemoApplication;
import com.liuqi.business.model.UserOtcWalletModel;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.UserAuthService;
import com.liuqi.business.service.UserOtcWalletService;
import com.liuqi.business.service.UserPoolWalletService;
import com.liuqi.utils.ShiroPasswdUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BaseDemoApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest {

    @Autowired
    private UserPoolWalletService userPoolWalletService;

    @Autowired
    private AddressRecordService addressRecordService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserOtcWalletService userOtcWalletService;

    @Test
    public void test(){
        userOtcWalletService.initUserWalletByCurrency(1L);
    }

}
