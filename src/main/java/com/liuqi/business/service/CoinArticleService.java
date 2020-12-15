package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CoinArticleModel;
import com.liuqi.business.model.CoinArticleModelDto;

public interface CoinArticleService extends BaseService<CoinArticleModel, CoinArticleModelDto> {


    CoinArticleModelDto getLastId();

    CoinArticleModelDto getByAid(Long id);
}
