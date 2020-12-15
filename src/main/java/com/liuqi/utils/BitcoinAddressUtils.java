package com.liuqi.utils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 *  根据IP地址获取详细的地域信息
 *  @project:personGocheck
 *  @class:AddressUtils.java
 *  @author:heguanhua E-mail:37809893@qq.com
 *  @date：Nov 14, 2012 6:38:25 PM
 */
public class BitcoinAddressUtils {

    private static DeterministicKey getNewAddress(List<String> mnemonicCode, int childNumber) {
        NetworkParameters mainNet = MainNetParams.get();
        byte[] seeds = MnemonicCode.toSeed(mnemonicCode, "");
        DeterministicKey key = HDKeyDerivation.createMasterPrivateKey(seeds);
        String BIP32RootKey = key.serializePrivB58(mainNet);
        DeterministicKey accountKey = key.derive(44).derive(0).derive(0);
        int change = 0;// 自定义Change类型(找零。一般使用0对外收款，1接受每次交易的找零)
        DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(change));// 生成对应账号下的对应交易类型的确定性密钥
        return HDKeyDerivation.deriveChildKey(changeKey, childNumber);
    }

    public static String[] getAddress(List<String> mnemonicCode, int childNumber) {
        DeterministicKey childKey = getNewAddress(mnemonicCode, childNumber);
        String address = Address.fromKey(MainNetParams.get(), childKey, Script.ScriptType.P2PKH).toString();
        // 生成对应钱包的私钥(比特币)
        String privateKey = Base58.encode(childKey.getPubKeyHash());
        return new String[] {"S" + address, "C" + privateKey};
    }

    public static String getHash(String content) {
        Sha256Hash sha256Hash = Sha256Hash.twiceOf(content.getBytes());
        return sha256Hash.toString();
    }

    // 测试
    public static void main(String[] args) {
        String s = "" + System.currentTimeMillis();

    }
}
