package com.xiaoxun.xun.utils;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by liutianxiang on 2015/12/11.
 */
public class DESUtil {



    private  static DESUtil instance = null;
    // 加密算法可用DES, DESede, Blowfish
    private static String Algorithm = "DES";
    private static String privKey = "";

    public static DESUtil getInstance(){
        if (instance ==null){
            instance = new DESUtil();
        }
        return  instance;
    }
    public void init(String key){
       privKey = key;
    }
    /**
     * 加密
     *
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptData(byte[] input, byte[] key) throws Exception
    {
     //  SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key, Algorithm);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        DESKeySpec keySpec = new DESKeySpec(key);
        Key deskey = keyFactory.generateSecret(keySpec);
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] cipherByte = c1.doFinal(input);
        return cipherByte;
    }
    /**
     * 加密并返回字符串(密钥使用默认密钥)
     * @param input 明文
     * @return
     * @throws Exception
     */
    public static String encryptDataStr(String input) throws Exception{
      //  return bytes2HexString(encryptData(input.getBytes(), getDefaultKey()));
        if (input != null && input.length() > 0) {
            try {
                return Base64.encodeToString(encryptData(input.getBytes(), getDefaultKey()), Base64.NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
                return input;
            }
        } else {
            return input;
        }
    }
    /**
     * 解密
     *
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptData(byte[] input, byte[] key) throws Exception
    {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        DESKeySpec keySpec = new DESKeySpec(key);
        Key deskey = keyFactory.generateSecret(keySpec);

        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.DECRYPT_MODE, deskey);
        byte[] clearByte = c1.doFinal(input);
        return clearByte;
    }
    /**
     * 解密并返回字符串(密钥使用默认密钥)
     * @param input 密文
     * @return
     * @throws Exception
     */
    public static String decryptDataStr(String input) throws Exception{
    //    return new String(decryptData(hexString2Bytes(input), getDefaultKey()));
        if (input != null && input.length() > 0) {
            try {
                return new String(decryptData(Base64.decode(input, Base64.NO_WRAP), getDefaultKey()));
            } catch (Exception e) {
                e.printStackTrace();
                return input;
            }
        } else {
            return input;
        }
    }
    /**
     * 默认key
     *
     * @return
     */
    public static byte[] getDefaultKey() {
       return  privKey.getBytes();
    }

    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    // 从字节数组到十六进制字符串转换
    public static String bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    // 从十六进制字符串到字节数组转换
    public static byte[] hexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    public static void testDes() {
        try {
            String minwen = "13585949890";
            String miwen = encryptDataStr(minwen);
            System.out.println(minwen+"---->"+miwen);
            System.out.println(minwen+"---->"+miwen+"---->"+decryptDataStr(miwen));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
