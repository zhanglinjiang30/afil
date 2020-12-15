package com.liuqi.business.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.dto.LoginDto;
import com.liuqi.business.enums.ActiveStatusEnum;
import com.liuqi.business.enums.HelpStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.mapper.AddressHoldingRecordMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.response.ReturnResponse;
import com.liuqi.token.RedisTokenManager;
import com.liuqi.utils.BitcoinAddressUtils;
import com.liuqi.utils.PassphraseUtils;
import com.liuqi.utils.ShiroPasswdUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.PassphraseDeviceMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PassphraseDeviceServiceImpl extends BaseServiceImpl<PassphraseDeviceModel, PassphraseDeviceModelDto> implements PassphraseDeviceService {

    @Autowired
    private PassphraseDeviceMapper passphraseDeviceMapper;
    @Autowired
    private AddressRecordService addressRecordService;
    @Autowired
    private AddressHoldingRecordService addressHoldingRecordService;
    @Autowired
    private RedisTokenManager redisTokenManager;
    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private UserOtcWalletService userOtcWalletService;

    @Autowired
    private UserPoolWalletService userPoolWalletService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private UserAuthService userAuthService;


    @Override
    public BaseMapper<PassphraseDeviceModel, PassphraseDeviceModelDto> getBaseMapper() {
        return this.passphraseDeviceMapper;
    }

    @Override
    public ReturnResponse getNewPassphrase() {
        try {
            byte[] randomNumber = new byte[16];
            new Random().nextBytes(randomNumber);
            MnemonicCode mc = new MnemonicCode();
            // Step1.生成助记词
            StringBuilder sb = new StringBuilder();
            List<String> mnemonicCode = mc.toMnemonic(randomNumber);
            for (int i = 0, length = mnemonicCode.size(); i < length; i++) {
                System.out.println(mnemonicCode.get(i));
                sb.append(i > 0 ? " " : "").append(mnemonicCode.get(i));
            }
            return ReturnResponse.backSuccess(mnemonicCode);
        } catch (Exception e) {
            return ReturnResponse.backFail("error");
        }
    }

    @Override
    @Transactional
    public ReturnResponse doPassphraseBackup(String passphrase, String deviceId, String name, String tradePwd) {
        if (StringUtils.isEmpty(deviceId)) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message88"));
        }
        passphrase = PassphraseUtils.processPassphrase(passphrase);
        PassphraseDeviceModelDto exist = passphraseDeviceMapper.getByPassphrase(passphrase);
        if (exist != null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message89"));
        }

        List<String> list = Arrays.asList(passphrase.split(" "));
        String[] apInfo = BitcoinAddressUtils.getAddress(list, 0);
        String address = apInfo[0];
        String privateKey = apInfo[1];
        // 插入助记词记录
        PassphraseDeviceModel passphraseDeviceModel = new PassphraseDeviceModel();
        passphraseDeviceModel.setPassphrase(passphrase);
        passphraseDeviceModel.setDeviceId(deviceId);
        passphraseDeviceModel.setActiveDevice(0);
        passphraseDeviceModel.setActiveCount(0);
        insert(passphraseDeviceModel);
        // 插入首次生成的地址记录
        AddressRecordModel addressRecordModel = new AddressRecordModel();
        addressRecordModel.setPassphraseId(passphraseDeviceModel.getId());
        addressRecordModel.setIndex(0);
        addressRecordModel.setAddress(address);
        addressRecordModel.setPrivateKey(privateKey);
        addressRecordModel.setDeviceId(deviceId);
        addressRecordModel.setActive(ActiveStatusEnum.UNACTIVE.getCode());
        addressRecordModel.setSucAmount(BigDecimal.ZERO);
        addressRecordService.insert(addressRecordModel);
        // 初始化地址钱包
        this.initWallet(addressRecordModel);
        userAuthService.initAuth(addressRecordModel.getId());

        // 清除此设备持有的所有地址
        addressHoldingRecordService.deleteByDeviceId(deviceId);
//        addressHoldingRecordService.deleteByAddressId(ad);
        // 生成地址持有记录
        AddressHoldingRecordModel addressHoldingRecordModel = new AddressHoldingRecordModel();
        addressHoldingRecordModel.setDeviceId(deviceId);
        addressHoldingRecordModel.setAddressId(addressRecordModel.getId());
        addressHoldingRecordModel.setName(name);
        addressHoldingRecordModel.setMain(HelpStatusEnum.USING.getCode());
        addressHoldingRecordModel.setTradePwd(ShiroPasswdUtil.getUserPwd(tradePwd));
        addressHoldingRecordModel.setDisplay(YesNoEnum.YES.getCode());
        addressHoldingRecordService.insert(addressHoldingRecordModel);
        // 生成Token
        String token = redisTokenManager.getToken(passphraseDeviceModel.getId(), deviceId);
        Map<String,Object> result = new HashMap<>();
        result.put("token",token);
        result.put("userId",addressRecordModel.getId());
        return ReturnResponse.backSuccess(result);
    }

    @Override
    @Transactional
    public ReturnResponse importPassphrase(String passphrase, String deviceId, String name, String tradePwd) {
        if (StringUtils.isEmpty(deviceId)) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message90"));
        }
        PassphraseDeviceModelDto passphraseDeviceModelDto = passphraseDeviceMapper.getByPassphrase(PassphraseUtils.processPassphrase(passphrase));
        if (passphraseDeviceModelDto == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message91"));
        }
        // 清除此设备持有的所有地址
        addressHoldingRecordService.deleteByDeviceId(deviceId);
        // 获取此助记词的第一个地址
        AddressRecordModelDto addressRecordModelDto = addressRecordService.getByPassphraseIdAndIndex(passphraseDeviceModelDto.getId(), 0);

        //一个助记词只能同时一个设备在线
        addressHoldingRecordService.deleteByAddressId(addressRecordModelDto.getId());

        // 生成地址持有记录
        AddressHoldingRecordModel addressHoldingRecordModel = new AddressHoldingRecordModel();
        addressHoldingRecordModel.setDeviceId(deviceId);
        addressHoldingRecordModel.setAddressId(addressRecordModelDto.getId());
        addressHoldingRecordModel.setName(name);
        addressHoldingRecordModel.setMain(HelpStatusEnum.USING.getCode());
        addressHoldingRecordModel.setDisplay(YesNoEnum.YES.getCode());
        addressHoldingRecordModel.setTradePwd(ShiroPasswdUtil.getUserPwd(tradePwd));
        addressHoldingRecordService.insert(addressHoldingRecordModel);
        // 生成Token
        String token = redisTokenManager.getToken(passphraseDeviceModelDto.getId(), deviceId);
        Map<String,Object> result = new HashMap<>();

        result.put("token",token);
        result.put("userId",addressRecordModelDto.getId());
        return ReturnResponse.backSuccess(result);
    }

    @Override
    @Transactional
    public ReturnResponse importAddressByPassphrase(LoginDto loginDto, String passphrase, String name) {
        passphrase = PassphraseUtils.processPassphrase(passphrase);
        PassphraseDeviceModelDto m = passphraseDeviceMapper.getByPassphrase(passphrase);
        if (m == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message91"));
        }
        if (m.getId().compareTo(loginDto.getPassphraseId()) == 0) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message92"));
        }
        List<AddressHoldingRecordModelDto> list = addressHoldingRecordService.getAddressesByDeviceId(loginDto);
        list = list.stream().filter(pp ->
            addressRecordService.getById(pp.getAddressId(), false).getPassphraseId().compareTo(m.getId()) == 0
        ).collect(Collectors.toList());
        AddressRecordModelDto addressRecordModelDto = null;
        // 没有此助记词导入的地址
        if (list == null || list.isEmpty()) {
            addressRecordModelDto = addressRecordService.getByPassphraseIdAndIndex(m.getId(), 0);
        } else {
            // 先抓取通过此助记词导入的最后一个地址
            Collections.sort(list, Comparator.comparing(AddressHoldingRecordModel::getAddressId));
            AddressRecordModelDto a = addressRecordService.getById(list.get(list.size() - 1).getAddressId(), false);
            addressRecordModelDto = addressRecordService.getByPassphraseIdAndIndex(m.getId(), a.getIndex() + 1);
            // 新地址如果还没创建
            if (addressRecordModelDto == null) {
                // 插入首次生成的地址记录
                addressRecordModelDto = new AddressRecordModelDto();
                addressRecordModelDto.setPassphraseId(m.getId());
                addressRecordModelDto.setIndex(a.getIndex() + 1);
                List<String> mncode = Arrays.asList(passphrase.split(" "));
                String[] apInfo = BitcoinAddressUtils.getAddress(mncode, a.getIndex() + 1);
                String address = apInfo[0];
                String privateKey = apInfo[1];
                addressRecordModelDto.setAddress(address);
                addressRecordModelDto.setPrivateKey(privateKey);
                addressRecordModelDto.setDeviceId(loginDto.getDeviceId());
                addressRecordModelDto.setActive(ActiveStatusEnum.UNACTIVE.getCode());
                addressRecordModelDto.setActiveCount(0);
                addressRecordModelDto.setActiveDevice(0);
                addressRecordModelDto.setSucAmount(BigDecimal.ZERO);
                addressRecordService.insert(addressRecordModelDto);

                this.initWallet(addressRecordModelDto);
            }
        }
        addressHoldingRecordService.disableDisplay(loginDto.getDeviceId());
        // 生成地址持有记录
        AddressHoldingRecordModel addressHoldingRecordModel = new AddressHoldingRecordModel();
        addressHoldingRecordModel.setDeviceId(loginDto.getDeviceId());
        addressHoldingRecordModel.setAddressId(addressRecordModelDto.getId());
        addressHoldingRecordModel.setName(name);
        addressHoldingRecordModel.setMain(HelpStatusEnum.NOUSING.getCode());
        addressHoldingRecordModel.setDisplay(YesNoEnum.YES.getCode());
        addressHoldingRecordService.insert(addressHoldingRecordModel);
        return ReturnResponse.backSuccess();
    }

    @Override
    @Transactional
    public ReturnResponse importAddressByPrivateKey(LoginDto loginDto, String privateKey, String name) {
        AddressRecordModelDto addressRecordModelDto = addressRecordService.getByPrivateKey(privateKey);
        if (addressRecordModelDto == null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message93"));
        }
        AddressHoldingRecordModelDto a = addressHoldingRecordService.getByDeviceIdAndAddressId(loginDto.getDeviceId(), addressRecordModelDto.getId());
        if (a != null) {
            return ReturnResponse.backFail(MessageSourceHolder.getMessage("message94"));
        }
        addressHoldingRecordService.disableDisplay(loginDto.getDeviceId());
        // 生成地址持有记录
        AddressHoldingRecordModel addressHoldingRecordModel = new AddressHoldingRecordModel();
        addressHoldingRecordModel.setDeviceId(loginDto.getDeviceId());
        addressHoldingRecordModel.setAddressId(addressRecordModelDto.getId());
        addressHoldingRecordModel.setName(name);
        addressHoldingRecordModel.setMain(HelpStatusEnum.NOUSING.getCode());
        addressHoldingRecordModel.setDisplay(YesNoEnum.YES.getCode());
        addressHoldingRecordService.insert(addressHoldingRecordModel);
        return ReturnResponse.backSuccess();
    }

    @Override
    @Transactional
    public ReturnResponse createAddress(LoginDto loginDto, String name) {
        // 抓取当前助记词登录的所有在持地址
        List<AddressHoldingRecordModelDto> list = addressHoldingRecordService.getAddressesByDeviceId(loginDto);
        // 只保留登录助记词对应的地址
        list = list.stream().filter(m ->
                addressRecordService.getById(m.getAddressId(), false).getPassphraseId().compareTo(loginDto.getPassphraseId()) == 0
        ).collect(Collectors.toList());
        // 保留最后一个地址
        Collections.sort(list, Comparator.comparing(AddressHoldingRecordModel::getAddressId));
        AddressRecordModelDto lastHoldAddress = addressRecordService.getById(list.get(list.size() - 1).getAddressId(), false);
        AddressRecordModelDto newAddress = addressRecordService.getByPassphraseIdAndIndex(loginDto.getPassphraseId(), lastHoldAddress.getIndex() + 1);
        // 新地址如果还没创建
        if (newAddress == null) {
            // 插入首次生成的地址记录
            newAddress = new AddressRecordModelDto();
            newAddress.setPassphraseId(loginDto.getPassphraseId());
            newAddress.setIndex(lastHoldAddress.getIndex() + 1);
            List<String> mncode = Arrays.asList(passphraseDeviceMapper.getById(loginDto.getPassphraseId()).getPassphrase().split(" "));
            String[] apInfo = BitcoinAddressUtils.getAddress(mncode, lastHoldAddress.getIndex() + 1);
            String address = apInfo[0];
            String privateKey = apInfo[1];
            newAddress.setAddress(address);
            newAddress.setPrivateKey(privateKey);
            newAddress.setDeviceId(loginDto.getDeviceId());
            newAddress.setActive(ActiveStatusEnum.UNACTIVE.getCode());
            newAddress.setActiveDevice(0);
            newAddress.setActiveCount(0);
            newAddress.setSucAmount(BigDecimal.ZERO);
            addressRecordService.insert(newAddress);

            this.initWallet(newAddress);
        }
        addressHoldingRecordService.disableDisplay(loginDto.getDeviceId());
        // 生成地址持有记录
        AddressHoldingRecordModel addressHoldingRecordModel = new AddressHoldingRecordModel();
        addressHoldingRecordModel.setDeviceId(loginDto.getDeviceId());
        addressHoldingRecordModel.setAddressId(newAddress.getId());
        addressHoldingRecordModel.setName(name);
        addressHoldingRecordModel.setMain(HelpStatusEnum.NOUSING.getCode());
        addressHoldingRecordModel.setDisplay(YesNoEnum.YES.getCode());
        addressHoldingRecordService.insert(addressHoldingRecordModel);
        return ReturnResponse.backSuccess();
    }

    private void initWallet(AddressRecordModel addressRecordModelDto){
        // 初始化地址钱包
        userWalletService.insertUserWallet(addressRecordModelDto.getId());

        //初始化法币钱包
        userOtcWalletService.getByUserAndCurrencyId(addressRecordModelDto.getId(),currencyService.getUsdtId());
        //初始化矿池钱包
        userPoolWalletService.getByUserAndCurrencyId(addressRecordModelDto.getId(),currencyService.getPTId());

    }

    @Override
    public ReturnResponse getActiveCount(LoginDto loginDto) {
//        PassphraseDeviceModelDto p = passphraseDeviceMapper.getById(loginDto.getPassphraseId());
        JSONObject result = new JSONObject();

        AddressHoldingRecordModelDto a = addressHoldingRecordService.getDisplayAddress(loginDto);
        if (a == null) {
            result.put("activeCount", 0);
            result.put("activeDevice", 0);
            result.put("poolLevel", 0);
        } else {
            AddressRecordModelDto aa = addressRecordService.getById(a.getAddressId());
            result.put("activeCount", aa == null ? 0 : aa.getActiveCount());
            result.put("activeDevice", aa == null ? 0 : aa.getActiveDevice());
            result.put("poolLevel", aa == null ? 0 : aa. getPoolLevel());
        }
        return ReturnResponse.backSuccess(result);
    }
}
