package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.business.service.*;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api(description = "挖矿相关")
@RequestMapping("/front/mining")
@RestController
public class FrontMiningController extends BaseFrontController {

    @Autowired
    private CurrencyHoldingService currencyHoldingService;
    @Autowired
    private PoolStatisticService poolStatisticService;
    @Autowired
    private PoolProfitRecordService poolProfitRecordService;
    @Autowired
    private ComputePowerRecordService computePowerRecordService;

    @ApiOperation(value = "首页信息【今日收益】/最佳持币/最低持币")
    @PostMapping("/index/info")
    public ReturnResponse getIndexInfo(HttpServletRequest request) throws NoLoginException {
        return currencyHoldingService.getIndexInfo(getLoginDto(request));
    }

    @ApiOperation(value = "矿池列表")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/coin/list")
    public ReturnResponse getCoinList(HttpServletRequest request, Integer pageNum, Integer pageSize) throws NoLoginException {
        return poolStatisticService.getCoinList(getLoginDto(request), pageNum, pageSize);
    }

    @ApiOperation(value = "收益详情")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", paramType = "query"),
    })
    @PostMapping("/profit/detail")
    public ReturnResponse getProfitDetail(Long currencyId, HttpServletRequest request) throws NoLoginException {
        return poolProfitRecordService.getProfitSummary(getLoginDto(request), currencyId);
    }

    @ApiOperation(value = "矿工邀请")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/invite/list")
    public ReturnResponse getProfitDetail(Long currencyId, Integer pageNum, Integer pageSize, HttpServletRequest request) throws NoLoginException {
        return computePowerRecordService.getInviteList(getLoginDto(request), currencyId, pageNum, pageSize);
    }

    @ApiOperation(value = "收益记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", paramType = "query"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/profit/list")
    public ReturnResponse getProfitList(Long currencyId, Integer pageNum, Integer pageSize, HttpServletRequest request) throws NoLoginException {
        return poolProfitRecordService.getProfitList(getLoginDto(request), currencyId, pageNum, pageSize);
    }
}

