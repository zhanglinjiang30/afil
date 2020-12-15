package com.liuqi.external.api;

import cn.hutool.core.bean.BeanUtil;
import com.liuqi.base.BaseFrontController;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.model.ApiTransferConfigModel;
import com.liuqi.business.model.ApiTransferModelDto;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.service.ApiTransferConfigService;
import com.liuqi.business.service.ApiTransferService;
import com.liuqi.business.service.CurrencyService;
import com.liuqi.external.api.dto.TransferDto;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.MD5Util;
import com.liuqi.utils.MapSort;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 对外接口
 */
@RestController
@RequestMapping("/api/transfer")
public class ApiOutTransferController extends BaseFrontController{

    @Autowired
    private ApiTransferConfigService apiTransferConfigService;
    @Autowired
    private ApiTransferService apiTransferService;
    @Autowired
    private CurrencyService currencyService;
    @RequestMapping("/publish")
    public ReturnResponse publish(@Valid TransferDto transferDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ReturnResponse.backFail("参数有误:" + getErrorInfo(bindingResult));
        }
        if(transferDto.getQuantity().compareTo(BigDecimal.ZERO)<=0){
            return ReturnResponse.backFail("数量不能小于0");
        }
        ApiTransferConfigModel config=apiTransferConfigService.getByName(transferDto.getName());
        if(config==null){
            return ReturnResponse.backFail("未配置转入信息");
        }

        Map<String,Object> params=BeanUtil.beanToMap(transferDto);
        params.remove("sign");
        String waitSign=MapSort.toStringMap(params);
        System.out.println("-->"+waitSign);
        String sign= MD5Util.MD5Encode(waitSign+config.getKey());
        if(!transferDto.getSign().equalsIgnoreCase(sign)){
            return ReturnResponse.backFail("签名异常");
        }

        if(transferDto.getQuantity().compareTo(config.getTimesQuantity())>0){
            return ReturnResponse.backFail("每次最大数量" + config.getTimesQuantity());
        }
        CurrencyModel currency= currencyService.getByName(transferDto.getCurrencyName().toUpperCase());
        if(currency==null){
            return ReturnResponse.backFail("币种信息不存在");
        }
        boolean canPublish=apiTransferConfigService.canTransfer(config,currency.getId(),new Date());
        if(!canPublish){
            return ReturnResponse.backFail("暂未开放");
        }
        UserModel user= new UserModel();
//        UserModel user= userService.queryByName(transferDto.getUserName());
        if(user==null){
            return ReturnResponse.backFail("用户不存在");
        }

        Long id=-1L;
        String key= LockConstant.LOCK_TRUSTEE_ID+transferDto.getName()+"_"+user.getId()+"_"+currency.getId();
        RLock lock=null;
        try {
            lock=RedissonLockUtil.lock(key);
            id = apiTransferService.publish(user.getId(),currency.getId(),transferDto,config);
            return ReturnResponse.backSuccess(id);
        }catch (Exception e){
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }finally {
            RedissonLockUtil.unlock(lock);
        }
    }


    @RequestMapping("/search")
    public ReturnResponse search(@RequestParam(value = "name",defaultValue = "")String name,@RequestParam(value = "id",defaultValue = "-1")Long id){
        ApiTransferModelDto transfer= apiTransferService.getById(id);
        if(transfer!=null){
            return ReturnResponse.backSuccess(transfer.getStatusStr());
        }
        return ReturnResponse.backFail();
    }
}
