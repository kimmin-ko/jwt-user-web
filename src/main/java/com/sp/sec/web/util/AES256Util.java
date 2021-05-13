package com.sp.sec.web.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Slf4j
public final class AES256Util {

    private final String iv;
    private final Key keySpec;

    /**
     * 16자리의 키값을 입력하여 객체를 생성한다.
     *
     * @param key 암/복호화를 위한 키값
     */
    private AES256Util(String key) {
        this.iv = key.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes(StandardCharsets.UTF_8);

        int len = Math.min(b.length, keyBytes.length);
        System.arraycopy(b, 0, keyBytes, 0, len);

        this.keySpec = new SecretKeySpec(keyBytes, "AES");
    }

    private static AES256Util createByKey(String key) {
        return new AES256Util(key);
    }

    public static String encryptByKey(String key, String decVal) {
        return AES256Util.createByKey(key).encrypt(decVal);
    }

    public static String decryptByKey(String key, String encVal) {
        return AES256Util.createByKey(key).decrypt(encVal);
    }

    private String encrypt(String str) {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = c.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.encodeBase64(encrypted));
        } catch (NoSuchAlgorithmException e) {
            log.error("{} ==> 현재 환경에서 사용 불가능한 알고리즘입니다.", e.getMessage());
        } catch (GeneralSecurityException e) {
            log.error("{} ==> 시큐러티 관련 오류.", e.getMessage());
        }

        return null;
    }

    private String decrypt(String str) {
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            byte[] byteStr = Base64.decodeBase64(str.getBytes());
            return new String(c.doFinal(byteStr), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            log.error("{} ==> 현재 환경에서 사용 불가능한 알고리즘입니다.", e.getMessage());
        } catch (GeneralSecurityException e) {
            log.error("{} ==> 시큐러티 관련 오류.", e.getMessage());
        }

        return null;
    }
}
