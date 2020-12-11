package com.imooc.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

/**
 * @author allycoding
 * @Date: 2020/6/13 15:10
 */
public class MD5Utils {
    /**
     * 对字符串进行md5加密
     * @param strValue
     * @return
     * @throws Exception
     */
    public static String getMD5Str(String strValue) throws Exception{
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(messageDigest.digest(strValue.getBytes()));
    }

    public static void main(String[] args) {
        try {
            String md5 = getMD5Str("imooc");
            System.out.println(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
