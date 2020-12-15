package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.UserTreeDto;
import com.liuqi.business.model.UserLevelModel;
import com.liuqi.business.model.UserLevelModelDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLevelMapper extends BaseMapper<UserLevelModel,UserLevelModelDto>{


    UserLevelModelDto getByUserId(@Param("userId") Long userId);

    List<Long> getAllSubIdList(@Param("treeInfo")String treeInfo);

    List<Long> getAssignSubIdList(@Param("treeInfo")String treeInfo, @Param("treeLevel")int treeLevel);

    void changeLevel(@Param("userTreeInfo") String userTreeInfo, @Param("changeTreeLevel")  int changeTreeLevel, @Param("replaceTreeInfo") String replaceTreeInfo);

    List<UserTreeDto> getTreeByParentId(@Param("parentId") Long parentId);

    List<UserTreeDto> getTreeByUserId(@Param("userId") Long userId);
}
