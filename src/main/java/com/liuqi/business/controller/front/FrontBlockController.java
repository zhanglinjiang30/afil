package com.liuqi.business.controller.front;

import com.liuqi.base.BaseFrontController;
import com.liuqi.business.service.ActiveRecordService;
import com.liuqi.business.service.BlockService;
import com.liuqi.business.service.PassphraseDeviceService;
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

@Api(description = "块相关")
@RequestMapping("/front/block")
@RestController
public class FrontBlockController extends BaseFrontController {

    @Autowired
    private BlockService blockService;

    @ApiOperation(value = "搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType ="String",name="content" ,value = "搜索内容",paramType = "query")
    })
    @PostMapping("/search/content")
    public ReturnResponse doContentSearch(String content) {
        return blockService.doContentSearch(content);
    }

}

