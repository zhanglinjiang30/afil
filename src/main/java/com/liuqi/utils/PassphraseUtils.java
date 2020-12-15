package com.liuqi.utils;

import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;


/**
 * 助记词处理
 */
public class PassphraseUtils {


    /**
     * 处理助记词，最后使用空格
     * @return
     */
    public static String processPassphrase(String passphrase) {
        if (StringUtils.isEmpty(passphrase)) {
            return passphrase;
        }
        passphrase = passphrase.trim();
        if (passphrase.contains(",")) {
            passphrase = passphrase.replace(",", " ");
        }
        return passphrase;
    }

    public static void main(String[] args) {
        String s = "hundred,proof,explain,drum,scissors,fork,order,trend,insane,belt,urge,size";
        System.out.println(processPassphrase(s));
    }
}
