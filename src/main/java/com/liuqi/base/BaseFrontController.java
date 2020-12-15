package com.liuqi.base;

import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.model.AddressHoldingRecordModelDto;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.service.AddressHoldingRecordService;
import com.liuqi.exception.NoLoginException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


public abstract class BaseFrontController extends BaseController {

    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;

    /**
     * 获取登录用户id
     *
     * @param request
     * @return
     * @throws NoLoginException
     */
    public LoginDto getLoginDto(HttpServletRequest request) throws NoLoginException {
        return LoginUserTokenHelper.getLoginDto(request);
    }


    public Long getAddressId(HttpServletRequest request) throws NoLoginException{
        LoginDto loginDto = this.getLoginDto(request);
        AddressHoldingRecordModelDto dto = addressHoldingRecordService.getDisplayAddress(loginDto);
        return dto.getAddressId();
    }
}
