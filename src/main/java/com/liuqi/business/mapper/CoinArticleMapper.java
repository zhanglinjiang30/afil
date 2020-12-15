package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CoinArticleModel;
import com.liuqi.business.model.CoinArticleModelDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinArticleMapper extends BaseMapper<CoinArticleModel, CoinArticleModelDto> {


    CoinArticleModelDto getLastRecord();

    CoinArticleModelDto getByAid(@Param("aId") Long aId);
}
