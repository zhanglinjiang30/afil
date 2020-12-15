package com.liuqi.business.controller.front;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.enums.HelpStatusEnum;
import com.liuqi.business.model.HelpModelDto;
import com.liuqi.business.model.HelpTypeModelDto;
import com.liuqi.business.service.ActiveRecordService;
import com.liuqi.business.service.HelpService;
import com.liuqi.business.service.HelpTypeService;
import com.liuqi.business.service.PassphraseDeviceService;
import com.liuqi.exception.NoLoginException;
import com.liuqi.response.ReturnResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(description = "矿工相关")
@RequestMapping("/front/miner")
@RestController
public class FrontMinerController extends BaseFrontController {

    @Autowired
    private ActiveRecordService activeRecordService;
    @Autowired
    private PassphraseDeviceService passphraseDeviceService;

    @ApiOperation(value = "激活数量和激活设备")
    @PostMapping("/acvite/count")
    public ReturnResponse getActiveCount(HttpServletRequest request) throws NoLoginException {
        return passphraseDeviceService.getActiveCount(getLoginDto(request));
    }

    @ApiOperation(value = "激活记录")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "currencyId", value = "币种id", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageNum", value = "当前页", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(dataType = "Integer", name = "pageSize", value = "条数", paramType = "query", defaultValue = "20"),
    })
    @PostMapping("/active/list")
    public ReturnResponse getActiveList(HttpServletRequest request, Long currencyId, Integer pageNum, Integer pageSize) throws NoLoginException {
        return activeRecordService.getActiveList(getLoginDto(request), currencyId, pageNum, pageSize);
    }

    @ApiOperation(value = "修改被激活地址名称")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Long", name = "id", value = "激活记录id", paramType = "query"),
            @ApiImplicitParam(dataType = "String", name = "name", value = "名称", paramType = "query")
    })
    @PostMapping("/modify/name")
    public ReturnResponse modifyName(Long id, String name, HttpServletRequest request) throws NoLoginException {
        return activeRecordService.modifyName(id, name);
    }


}

