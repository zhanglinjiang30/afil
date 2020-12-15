package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.model.PublicOfferModelDto;
import com.liuqi.business.model.PublicOfferRecordModelDto;
import com.liuqi.business.service.CrowdfundInfoService;
import com.liuqi.business.service.CrowdfundService;
import com.liuqi.business.service.PublicOfferRecordService;
import com.liuqi.business.service.PublicOfferService;
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

@Api(description ="公募" )
@RequestMapping("/front/publicOffer")
@RestController
public class FrontPublicOfferController extends BaseFrontController {

    @Autowired
    private PublicOfferService publicOfferService;

    @Autowired
    private PublicOfferRecordService publicOfferRecordService;

    @ApiOperation(value = "购买")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Long",name="publicOfferId" ,value = "公募ID",required = true,paramType = "query"),
            @ApiImplicitParam(dataType ="BigDecimal",name="amount" ,value = "投票数量",required = true,paramType = "query"),
    })
    @PostMapping("buy")
    public ReturnResponse buy(HttpServletRequest request, Long publicOfferId, BigDecimal amount){
        Long addressId = this.getAddressId(request);
        RLock lock = null;
        String key = LockConstant.LOCK_PUBLIC_OFFER_BUY+addressId;
        try {
            lock = RedissonLockUtil.lock(key);
            publicOfferService.buy(addressId,publicOfferId,amount);
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }finally {
            RedissonLockUtil.unlock(lock);
        }
        return ReturnResponse.backSuccess();
    }

    @ApiOperation(value = "公募列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Integer",name="pageSize" ,value = "数量",required = false,paramType = "query",defaultValue = "20"),
    })
    @PostMapping("getList")
    public ReturnResponse getCrowdfundList(HttpServletRequest request,
                                           @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                           @RequestParam(value = "pageSize",defaultValue = "20")Integer pageSize,
                                           @RequestParam(value = "status") Integer status){
        PublicOfferModelDto search = new PublicOfferModelDto();
        search.setStatus(status);
        return ReturnResponse.backSuccess(publicOfferService.queryFrontPageByDto(search,pageNum,pageSize));
    }

    @ApiOperation(value = "公募记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="Integer",name="pageNum" ,value = "当前页",required = false,paramType = "query",defaultValue = "1"),
            @ApiImplicitParam(dataType ="Integer",name="pageSize" ,value = "数量",required = false,paramType = "query",defaultValue = "20"),
    })
    @PostMapping("getByRecord")
    public ReturnResponse getBuyRecord(HttpServletRequest request,
                                       @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                       @RequestParam(value = "pageSize",defaultValue = "20")Integer pageSize,
                                       Integer status){
        Long userId= this.getAddressId(request);

        PublicOfferRecordModelDto search = new PublicOfferRecordModelDto();
        search.setAddressId(userId);
        search.setPublicOfferStatus(status);
        return ReturnResponse.backSuccess(publicOfferRecordService.getRecordList(pageNum,pageSize,search));
    }
}
