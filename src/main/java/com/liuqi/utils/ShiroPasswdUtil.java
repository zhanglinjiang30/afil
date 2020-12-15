package com.liuqi.utils;

import com.liuqi.base.BaseConstant;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author tanyan
 * @create 2019-11=29
 * @description
 */
public class ShiroPasswdUtil {


    public static String getUserPwd(String orginPwd){
        String slat=BaseConstant.TYPE_USER+BaseConstant.BASE_PROJECT;
        return ShiroPasswdUtil.getBasePwd(orginPwd,slat);
    }

    public static String getSysPwd(String orginPwd){
        String slat=BaseConstant.TYPE_SYS+BaseConstant.BASE_PROJECT;
        return ShiroPasswdUtil.getBasePwd(orginPwd,slat);
    }

    public static String getAdminPwd(String orginPwd){
        String slat=BaseConstant.TYPE_ADMIN+BaseConstant.BASE_PROJECT;
        return ShiroPasswdUtil.getBasePwd(orginPwd,slat);
    }

    public static String getBasePwd(String orginPwd,String slat){
        String hashAlgorithmName = "MD5";//加密方式
        int hashIterations = BaseConstant.PWD_COUNT;//加密次
        return new SimpleHash(hashAlgorithmName,orginPwd,slat,hashIterations).toString();
    }

    public static void main(String[] args) {
        System.out.println("-1--"+getUserPwd("123456"));
    }
}
