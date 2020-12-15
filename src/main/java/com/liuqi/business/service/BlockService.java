package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.BlockModel;
import com.liuqi.business.model.BlockModelDto;
import com.liuqi.response.ReturnResponse;

public interface BlockService extends BaseService<BlockModel,BlockModelDto>{

    /**
     * @Description 产生区块
     * @Date 12:21 2020/8/10
     */
    void generateBlock();

    ReturnResponse doContentSearch(String content);
}
