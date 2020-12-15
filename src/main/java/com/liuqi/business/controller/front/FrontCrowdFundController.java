package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.model.CrowdfundModelDto;
import com.liuqi.business.model.CrowdfundRecordModelDto;
import com.liuqi.business.service.CrowdfundInfoService;
import com.liuqi.business.service.CrowdfundRecordService;
import com.liuqi.business.service.CrowdfundService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Api(description ="投票" )
@RequestMapping("/front/crowdFund")
@RestController
public class FrontCrowdFundController extends BaseFrontController {

    @Autowired
    private CrowdfundInfoService crowdfundInfoService;

    @Autowired
    private CrowdfundService crowdfundService;

    @Autowired
    private CrowdfundRecordService crowdfundRecordService;

    @ApiOperation(value = "购买")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="infoId" ,value = "投票期限Id",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="BigDecimal",name="amount" ,value = "投票数量",required = true,paramType = "query"),
    })
    @PostMapping("buy")
    public ReturnResponse buy(HttpServletRequest request, Long infoId, BigDecimal amount){
        Long addressId = this.getAddressId(request);
        RLock lock = null;
        String key = LockConstant.LOCK_CROWDFUND_BUY+addressId;
        try {
            lock = RedissonLockUtil.lock(key);
            crowdfundInfoService.buy(addressId,infoId,amount);
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }finally {
            RedissonLockUtil.unlock(lock);
        }
        return ReturnResponse.backSuccess();
    }

    @ApiOperation(value = "投票列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Integer",name="pageSize" ,value = "数量",required = false,paramType = "query",defaultValue = "20"),
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型 1 共享 2 实时",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Integer",name="status" ,value = "状态 0 暂未开启 1 进行中 2 已结束",required = false,paramType = "query",defaultValue = "1"),
    })
    @PostMapping("getCrowdfundList")
    public ReturnResponse getCrowdfundList(HttpServletRequest request,
                                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "20")Integer pageSize,
                                           CrowdfundModelDto crowdfundModelDto){
        return ReturnResponse.backSuccess(crowdfundService.getCrowdfundList(pageNum,pageSize,crowdfundModelDto));
    }

    @ApiOperation(value = "投票记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Integer",name="pageSize" ,value = "数量",required = false,paramType = "query",defaultValue = "20"),
            @ApiImplicitParam(dataType ="Integer",name="type" ,value = "类型 1 共享，2 实时",required = false,paramType = "query"),
            @ApiImplicitParam(dataType ="Long",name="crowdfundInfoId" ,value = "投票详情ID",required = false,paramType = "query"),
    })
    @PostMapping("getCrowdfundRecord")
    public ReturnResponse getCrowdfundRecord(HttpServletRequest request,
                                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "20")Integer pageSize,
                                             Integer type,Long crowdfundInfoId,Integer status){

        Long userId = this.getAddressId(request);
        CrowdfundRecordModelDto search = new CrowdfundRecordModelDto();
        search.setAddressId(userId);
        search.setCrowdfundType(type);
        search.setCrowdfundInfoId(crowdfundInfoId);
        search.setInfoStatus(status);
        return ReturnResponse.backSuccess(crowdfundRecordService.getRecordList(pageNum,pageSize,search));
    }


}
