package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.PassphraseDeviceModel;
import com.liuqi.business.model.PassphraseDeviceModelDto;
import org.springframework.stereotype.Repository;

@Repository
public interface PassphraseDeviceMapper extends BaseMapper<PassphraseDeviceModel,PassphraseDeviceModelDto>{

    PassphraseDeviceModelDto getByPassphrase(String passphrase);

}
