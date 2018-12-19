package com.weexbox.core.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author:leon.wen
 * Time:2018/10/10   11:23
 * Description:This is AES128Util
 */
public class AES128Util {

    private static final String UTF_8 = "UTF-8";

    /**
     * 加密
     *
     * @param src 需要加密的内容
     * @param key 加密密码
     * @return
     */
    public static String encrypt(String key, String src) {
        try {
            if (key == null || key.length() <= 0 || src == null || src.length() <= 0) {
                return null;
            }
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            byte[] byteContent = src.getBytes(UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, skey);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            String resultStr = byte2Base64(result);
            return resultStr;
        } catch (Exception ext) {
            ext.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     * * @param String src 解密字符串
     * * @param String key 密钥
     * * @return 解密后的字符串
     */
    public static String decrypt(String key, String src) {
        try {            // 判断Key是否正确
            if (key == null || key.length() <= 0 || src == null || src.length() <= 0) {
                return null;
            }
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(src, Base64.NO_WRAP);
            //byte[] encrypted1 = src.getBytes(UTF_8);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    private static String byte2Base64(byte[] encode) {
        try {
            return Base64.encodeToString(encode, Base64.NO_WRAP);
        } catch (Exception ext) {
            ext.printStackTrace();
        }
        return null;
    }

}
