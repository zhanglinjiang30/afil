package com.liuqi.business.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liuqi.business.enums.CrowdfundStatusEnum;
import com.liuqi.business.model.CrowdfundInfoModelDto;
import com.liuqi.business.service.CrowdfundInfoService;
import com.liuqi.business.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.CrowdfundModel;
import com.liuqi.business.model.CrowdfundModelDto;


import com.liuqi.business.service.CrowdfundService;
import com.liuqi.business.mapper.CrowdfundMapper;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CrowdfundServiceImpl extends BaseServiceImpl<CrowdfundModel, CrowdfundModelDto> implements CrowdfundService {

    @Autowired
    private CrowdfundMapper crowdfundMapper;

    @Autowired
    private CrowdfundInfoService crowdfundInfoService;

    @Autowired
    private CurrencyService currencyService;

    @Override
    public BaseMapper<CrowdfundModel, CrowdfundModelDto> getBaseMapper() {
        return this.crowdfundMapper;
    }


    @Override
    protected void doMode(CrowdfundModelDto dto) {
        super.doMode(dto);
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));

    }

    public PageInfo<CrowdfundModelDto> getCrowdfundList(Integer pageNum, Integer pageSize,CrowdfundModelDto crowdfundModelDto) {


        PageInfo<CrowdfundModelDto> page = this.queryFrontPageByDto(crowdfundModelDto, pageNum, pageSize);
        List<CrowdfundModelDto> list = page.getList();
        for (CrowdfundModelDto dto : list) {
            //查询当前投票第几期信息
            CrowdfundInfoModelDto crowdfundInfoModelDto = crowdfundInfoService.getEnableInfoByFundId(dto.getId());
            if (crowdfundInfoModelDto == null){
                crowdfundInfoModelDto = crowdfundInfoService.getLastInfoByFundId(dto.getId());
            }
            dto.setInfoModelDto(crowdfundInfoModelDto);
        }
        return new PageInfo<>(list);
    }

}
