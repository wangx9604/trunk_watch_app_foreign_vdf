package com.xiaoxun.xun.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/*
 * 该类用于计算字符串或者文件的MD5值
 */
public class MD5 {
    /*
     * 计算给定字符串的md5
     */
    public static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    static public String md5_string(String s) {
        byte[] unencodedPassword;


        MessageDigest md = null;

        try {
            unencodedPassword = s.getBytes(StandardCharsets.UTF_8);
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(unencodedPassword);
        } catch (Exception e) {
            return s;
        }


//		byte[] encodedPassword = md.digest();
//		StringBuffer buf = new StringBuffer();
//
//		for (int i = 0; i < encodedPassword.length; i++) {
//			if ((encodedPassword[i] & 0xff) < 0x10) {
//				buf.append("0");
//			}
//			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
//		}

//		return buf.toString();
        return toHexString(md.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /*
     * 对给定的文件进行MD5计算
     *
     * @param strFileName 文件的名称
     *
     * @param iBeginOffset 起始偏移
     *
     * @param iEndOffset 终止偏移
     */
    static public String md5_file(String fileName)
            throws Exception {
        InputStream fis;
        fis = new FileInputStream(fileName);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }

    static public String md5_bytes(byte[] input)
            throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        md5.update(input);

        return toHexString(md5.digest());
    }

    static public byte[] getFileMD5(String fileName) throws Exception {
        InputStream fis;
        fis = new FileInputStream(fileName);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return md5.digest();
    }
}
