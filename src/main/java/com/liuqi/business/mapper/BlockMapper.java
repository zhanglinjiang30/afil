package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.BlockModel;
import com.liuqi.business.model.BlockModelDto;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockMapper extends BaseMapper<BlockModel,BlockModelDto>{

    BlockModelDto getByHeight(Long height);

}
