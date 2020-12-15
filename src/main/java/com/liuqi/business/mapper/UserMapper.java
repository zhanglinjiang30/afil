package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper extends BaseMapper<UserModel, UserModelDto> {

    /**
     * phone
     * email
     * name
     * inviteCode
     * @return
     */
    UserModelDto queryUnique(UserModelDto dto);

    int getTotal();

    List<Long> queryIdByLikeName(String name);
}
