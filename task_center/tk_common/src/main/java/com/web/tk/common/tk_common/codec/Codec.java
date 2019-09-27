package com.web.tk.common.tk_common.codec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * 随机数/id/加减密算法/编码解码 实现类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0.0
 * <br/>创建时间：2019-4-12
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class Codec {


    public static String getRandomByUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getRandom() {
        return randomString(36);
    }

    public static String randomString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static String encodeBase64(String value)
            throws UnsupportedEncodingException {
        try {
            return new String(Base64.encodeBase64(value.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw ex;
        }
    }

    public static String encodeBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }


    public static byte[] decodeBase64(String value) throws UnsupportedEncodingException {
        try {
            return Base64.decodeBase64(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    public static String decodeBase64(byte[] bytes) {
        return new String(Base64.decodeBase64(bytes));
    }


    public static String getHexMD5(String value) throws Exception {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(value.getBytes("UTF-8"));
            byte[] digest = messageDigest.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static String getHexSHA1(String value) throws Exception {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(value.getBytes("UTF-8"));
            byte[] digest = messageDigest.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw ex;
        }
    }


    public static String byteToHexString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] hexStringToByte(String hexString) throws DecoderException {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            throw e;
        }
    }
}
