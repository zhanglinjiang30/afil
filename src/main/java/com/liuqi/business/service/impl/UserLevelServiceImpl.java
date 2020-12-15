package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.UserTreeDto;
import com.liuqi.business.mapper.UserLevelMapper;
import com.liuqi.business.model.UserLevelModel;
import com.liuqi.business.model.UserLevelModelDto;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.UserAuthService;
import com.liuqi.business.service.UserLevelService;
import com.liuqi.exception.BusinessException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.redis.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class UserLevelServiceImpl extends BaseServiceImpl<UserLevelModel, UserLevelModelDto> implements UserLevelService {

    @Autowired
    private UserLevelMapper userLevelMapper;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private AddressRecordService addressRecordService;

    @Override
    public BaseMapper<UserLevelModel, UserLevelModelDto> getBaseMapper() {
        return this.userLevelMapper;
    }

    @Override
    public UserLevelModelDto getByUserId(Long userId) {
        String key = KeyConstant.KEY_USER_LEVEL + userId;
        UserLevelModelDto dto = redisRepository.getModel(key);
        if (dto == null) {
            dto = userLevelMapper.getByUserId(userId);
            if (dto != null) {
                //缓存2天
                redisRepository.set(key, dto, 2L, TimeUnit.DAYS);
            }
        }
        return dto;
    }

    @Override
    @Transactional
    public void initLevel(Long userId, Long inviteAddressId) {
        UserLevelModel level = new UserLevelModel();
        level.setUserId(userId);
        level.setParentId(0L);
        level.setTreeLevel(1);
        level.setTreeInfo("," + userId + ",");
        if (inviteAddressId != null) {
            //查询用户
            UserLevelModel pLevel = this.getByUserId(inviteAddressId);
            level.setParentId(inviteAddressId);
            level.setTreeLevel(pLevel.getTreeLevel() + 1);
            level.setTreeInfo(pLevel.getTreeInfo() + userId + ",");
        }
        this.insert(level);
    }

    @Override
    public List<Long> getAllSubIdList(Long userId) {
        UserLevelModel level = this.getByUserId(userId);
        return userLevelMapper.getAllSubIdList(level.getTreeInfo());
    }

    @Override
    public List<Long> getAssignSubIdList(Long userId, Integer nextLevel) {
        if (nextLevel <= 0) {
            nextLevel = 1;
        }
        UserLevelModel level = this.getByUserId(userId);
        return userLevelMapper.getAssignSubIdList(level.getTreeInfo(), level.getTreeLevel() + nextLevel);
    }

    @Override
    public List<String> getParent(Long userId) {
        UserLevelModel level = this.getByUserId(userId);
        System.out.println(userId + "----" + level);
        String userTreeInfo = level.getTreeInfo();
        List<String> treeInfoList = Arrays.asList(userTreeInfo.substring(1, userTreeInfo.length() - 1).split(","));
        treeInfoList = new ArrayList<>(treeInfoList);
        Collections.reverse(treeInfoList);
        //移除第一个自己
        treeInfoList.remove(0);
        return treeInfoList;
    }

    @Override
    public void cleanCacheByModel(UserLevelModel userLevelModel) {
        String key = KeyConstant.KEY_USER_LEVEL + userLevelModel.getUserId();
        redisRepository.del(key);
    }

    @Override
    protected void doMode(UserLevelModelDto dto) {
        super.doMode(dto);
        dto.setSucAddress(addressRecordService.getById(dto.getUserId(), false).getAddress());
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            dto.setParentName(addressRecordService.getById(dto.getParentId(), false).getAddress());
        }
    }

    @Override
    @Transactional
    public void changeLevel(UserLevelModelDto m, Long parentId) {
        //层级未改变时  不做处理
        if (m.getParentId().equals(parentId)) {
            return;
        }
        List<Long> subList = this.getAllSubIdList(m.getUserId());
        if (subList.contains(parentId)) {
            throw new BusinessException(MessageSourceHolder.getMessage("message111"));
        }
        //改变的层级
        int changeTreeLevel = 0;
        String baseTreeInfo = "";
        if (parentId > 0) {
            UserLevelModelDto parent = this.getByUserId(parentId);
            changeTreeLevel = parent.getTreeLevel() - m.getTreeLevel() + 1;
            baseTreeInfo = parent.getTreeInfo() + m.getUserId() + ",";
        } else {
            changeTreeLevel = 0 - m.getTreeLevel() + 1;
            baseTreeInfo = "," + m.getUserId() + ",";
        }
        //修改领导人字段
        m.setParentId(parentId);
        this.update(m);
        //修改语句
        userLevelMapper.changeLevel(m.getTreeInfo(), changeTreeLevel, baseTreeInfo);


        //清理缓存
        List<Long> list = this.getAllSubIdList(m.getUserId());
        for (Long uid : list) {
            cleanCacheById(uid);
        }
    }

    @Override
    public List<UserTreeDto> getTreeByParentId(Long parentId) {
        List<UserTreeDto> list = userLevelMapper.getTreeByParentId(parentId);
        String name = "";
        if (list != null && list.size() > 0) {
            for (UserTreeDto dto : list) {
                if (dto != null) {
                    List cList = this.getAllSubIdList(dto.getId());
                    name = addressRecordService.getById(dto.getId()).getAddress() + "(" + userAuthService.getByUserId(dto.getId()).getRealName() + ")(团队" + cList.size() + "人)";
                    dto.setName(name);
                }
            }
        }
        return list;
    }

    @Override
    public List<UserTreeDto> getTreeByUserId(Long userId) {
        List<UserTreeDto> list = userLevelMapper.getTreeByUserId(userId);
        String name = "";
        if (list != null && list.size() > 0) {
            for (UserTreeDto dto : list) {
                if (dto != null) {
                    List cList = this.getAllSubIdList(dto.getId());
                    name = addressRecordService.getById(dto.getId()).getAddress() + "(" + userAuthService.getByUserId(dto.getId()).getRealName() + ")(团队" + cList.size() + "人)";
                    dto.setName(name);
                }
            }
        }
        return list;
    }

    private void cleanCacheById(Long id) {
        String key = KeyConstant.KEY_USER_LEVEL + id;
        redisRepository.del(key);
    }
}
