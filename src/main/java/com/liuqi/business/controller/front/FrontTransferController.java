package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.model.AddressHoldingRecordModelDto;
import com.liuqi.business.model.CurrencyConfigModelDto;
import com.liuqi.business.model.CurrencyModelDto;
import com.liuqi.business.service.AddressHoldingRecordService;
import com.liuqi.business.service.CurrencyConfigService;
import com.liuqi.business.service.PassphraseDeviceService;
import com.liuqi.business.service.TransferRecordService;
import com.liuqi.exception.NoLoginException;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "转账")
@RequestMapping("/front/transfer")
@RestController
public class FrontTransferController extends BaseFrontController {

    @Autowired
    private TransferRecordService transferRecordService;
    @Autowired
    private CurrencyConfigService currencyConfigService;

    @ApiOperation(value = "执行转账")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="fromAddressId" ,value = "发送方地址id",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="toAddress" ,value = "接收方地址",paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "转账币种",paramType = "query"),
            @ApiImplicitParam(dataType ="BigDecimal",name="amount" ,value = "转账数量",paramType = "query"),
            @ApiImplicitParam(dataType ="String",name="tradePwd" ,value = "交易密码",paramType = "query")
    })
    @PostMapping("/do/tx")
    public ReturnResponse getPassphrase(HttpServletRequest request, String toAddress, Long currencyId, BigDecimal amount, String tradePwd) throws NoLoginException {
        LoginDto loginDto = getLoginDto(request);
        String key = LockConstant.LOCK_TRANSFER + loginDto.getDeviceId();
        RLock lock = null;
        try {
            lock = RedissonLockUtil.lock(key);
            return transferRecordService.doTransfer(loginDto, toAddress, currencyId, amount, tradePwd);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail("error");
        } finally {
            RedissonLockUtil.unlock(lock);
        }
    }

    @ApiOperation(value = "转账记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="addressId" ,value = "发送方地址id",paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "页码",paramType = "query"),
            @ApiImplicitParam(dataType ="Integer",name="pageSize" ,value = "每页条数",paramType = "query")
    })
    @PostMapping("/tx/list")
    public ReturnResponse importAddressByPassphrase(Integer pageNum, Integer pageSize, HttpServletRequest request) throws NoLoginException {
        return transferRecordService.getTxList(getLoginDto(request), pageNum, pageSize);
    }

    @ApiOperation(value = "转账记录详情")
    @PostMapping("/tx/transferRecordDetail")
    public ReturnResponse transferRecordDetail(HttpServletRequest request,Long id){
        Long addressId = this.getAddressId(request);
        return ReturnResponse.backSuccess(transferRecordService.getRecordDetail(addressId,id));
    }

    @ApiOperation(value = "转账矿工费")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="currencyId" ,value = "转账币种",paramType = "query"),
    })
    @PostMapping("/tx/fee")
    public ReturnResponse getTransferFee(Long currencyId) throws NoLoginException {
        CurrencyConfigModelDto currencyConfigModelDto = currencyConfigService.getByCurrencyId(currencyId);
        return ReturnResponse.backSuccess(currencyConfigModelDto.getMine());
    }


}

