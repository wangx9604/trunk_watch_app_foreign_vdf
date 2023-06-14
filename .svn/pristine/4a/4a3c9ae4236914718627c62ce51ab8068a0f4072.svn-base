package com.xiaoxun.xun.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.WatchData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-1-11
 */
public class StrUtil {
    /**
     * 获取字符串的相关校验码
     *
     * @param str
     * @return
     */
    public static long getStringCRC32(String str) {
        long lCode = 0;
        try {
            CRC32 crc32 = new CRC32();
            byte[] bufs = str.getBytes(StandardCharsets.UTF_8);
            if (null != bufs) {
                crc32.update(bufs);
            }
            lCode = crc32.getValue();
        } catch (Exception e) {
        }

        return lCode;
    }


    public static String getTimeLengthStr(long lastTransTimeLength, String minStr, String secStr) {
        float second = lastTransTimeLength / 1000;
        StringBuilder sFormat = new StringBuilder();
        long min = ((long) second) / 60;
        if (min > 0) {
            sFormat.append(min);
            sFormat.append(minStr);
            second = second % 60;
            if (second > 0) {
                sFormat.append((long) second);
                sFormat.append(secStr);
            }
        } else {
            sFormat.append(second);
            sFormat.append(secStr);
        }

        return sFormat.toString();
    }

    public static String getFileSizeStr(long fileSize) {
//		// TODO Auto-generated method stub
//		StringBuffer buf = new StringBuffer();
//		if (fileSize < 1024) {
//			buf.append(fileSize);
//			buf.append("B");
//		} else if (fileSize < 1024 * 1024) {
//			buf.append(fileSize / 1024);
//			buf.append("K");
//		} else {
//			buf.append(fileSize / (1024 * 1024));
//			buf.append("M");
//		}
//
//		return buf.toString();
        return SizeFormat(Long.valueOf(fileSize).toString());
    }

    public static String SizeFormat(String sSize) {

        Float nSize = Float.parseFloat(sSize);
        Object localObject = new DecimalFormat("0.0");
        StringBuilder sFormat = new StringBuilder();
        float nMB = (nSize / 1024 / 1024);
        String sMB;
        if (nMB > 1024) {
            float nGB = (nMB / 1024);
            sMB = ((DecimalFormat) localObject).format(nGB);
            sFormat.append(sMB).append("GB");
        } else if (nMB > 1) {
            sMB = ((DecimalFormat) localObject).format(nMB);
            sFormat.append(sMB).append("MB");
        } else {
            int nKB = (int) (nSize / 1024);
            if (nKB > 1)
                sFormat.append(nKB).append("KB");
            else
                sFormat.append(nSize.intValue()).append("B");
        }

        return sFormat.toString();
    }

    public static String getExtNameFromString(String str) {
        String szExtName = ".xxx";
        int nExtPos = str.lastIndexOf('.');
        if (nExtPos >= 0) {
            szExtName = str.substring(nExtPos + 1);
        }
        return szExtName;
    }

    public static String getFileNameFromPath(String path) {
        String fileName = null;
        int nNamePos = path.lastIndexOf('/');
        if (nNamePos >= 0) {
            fileName = path.substring(nNamePos + 1);
        }
        return fileName;
    }


    // 只去网关地址，去掉最后本机ip为1
    public static String IpIntToGatewayString(int intIp) {
        StringBuffer sb = new StringBuffer();

        // 将高24位置0
        sb.append((intIp & 0x000000FF));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(((intIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(((intIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 直接右移24位
        sb.append(1);
        return sb.toString();
    }

    // 只去网关地址，去掉最后本机ip为1
    public static String IpIntToBroadString(int intIp) {
        StringBuffer sb = new StringBuffer();

        // 将高24位置0
        sb.append((intIp & 0x000000FF));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(((intIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(((intIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 直接右移24位
        sb.append(255);
        return sb.toString();
    }


    // lixin add 20131203
    public static String md5(String data) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md5 = toHex(md.digest(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String toHex(byte[] bytes) {
        String hex = "";
        for (Byte b : bytes) {
            hex += String.format("%02x", b & 0xFF);
        }
        return hex;
    }

    /**
     * 检查字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return null != str && !str.matches("\\s*");
    }

    public static String getStringMD5(String strings) {
        StringBuilder szMD5Buf = new StringBuilder();
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.update(strings.getBytes());

            byte[] md5Value = digester.digest();
            for (byte b : md5Value) {
                if ((b & 0xff) < 0x10) {
                    szMD5Buf.append("0");
                }
                szMD5Buf.append(Long.toString(b & 0xff, 16));
            }
        } catch (Exception e) {
        }

        LogUtil.d("ProtoAdapter.getStringMD5() string=" + strings + " MD5=" + szMD5Buf.toString());

        return szMD5Buf.toString();
    }

    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.NO_WRAP);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static String bitmaptoString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return string;
    }


    public static String genFamilyDesc(FamilyData family, Context context) {
        // TODO Auto-generated method stub
        StringBuilder buff = new StringBuilder();
        if (family.getWatchlist() != null && family.getWatchlist().size() > 0)
            for (WatchData watch : family.getWatchlist()) {
                buff.append(watch.getNickname());
                buff.append(" ");
            }
        if (family.getMemberList() != null && family.getMemberList().size() > 0) {
            for (MemberUserData member : family.getMemberList()) {
                buff.append(member.getNickname());
                buff.append(" ");
            }
        }

        return buff.toString();
    }

    public static String genFamilyName(FamilyData family, Context context) {
        // TODO Auto-generated method stub
        StringBuilder buff = new StringBuilder();
        if (family.getWatchlist() != null && family.getWatchlist().size() > 0)
            buff.append(family.getWatchlist().get(0).getNickname());
        buff.append(context.getString(R.string.family_suffix));
        return buff.toString();
    }

    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(AESUtil.decrypt(buffer), Base64.NO_WRAP);
    }

    public static byte[] getBytesFromFile(File file) throws Exception {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return buffer;
    }

    public static void decoderBase64File(String base64Code, String savePath) throws Exception {
        //byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        byte[] buffer = Base64.decode(base64Code, Base64.NO_WRAP);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

    //public static final String salt = "123";
    public static String getXioaoMiPsw(String openId, String salt) {
        // TODO Auto-generated method stub
        //return(openId+salt);
        return MD5.md5_string(openId + salt);
    }

    public static boolean isDeviceImei(String imei){
        boolean result=false;
        Pattern p = Pattern.compile("^\\d{15}$");
        Matcher m = p.matcher(imei);
        result = m.matches();
        return result;
    }

    //号码是2-18位并且全是数字
    public static boolean isMobileNumber(String number, int minLength) {
        boolean resule = false;
//        Pattern p = Pattern.compile("^((\\+86)|(86))?\\d{2,18}$");
        Pattern p = Pattern.compile("^(\\+)?\\d{2,18}$");
        Matcher m = p.matcher(number);
        resule = m.matches();
        return resule && number.length() <= 18 && number.length() >= minLength;
    }

    //去掉号码里面的"空格"、"-"等
    public static String formatPhoneNumber(String number){
        if (number==null){
            return "";  //保护空指针
        }
//        if(number.length() >= 3 && number.substring(0,3).equals("+86")){
//            number = number.substring(3);
//        }
        number = number.replace(" ","");
        number = number.replace("-", "");
        number = number.replace("#", "");
        number = number.replace("*", "");
        return number;
    }

    public static String Byte2Unicode(byte[] abyte, int st, int bEnd) {
        StringBuffer sb = new StringBuffer();
        for (int j = st; j < bEnd; ) {
            int lw = abyte[j++];
            if (lw < 0) lw += 256;
            int hi = abyte[j++];
            if (hi < 0) hi += 256;
            char c = (char) (lw + (hi << 8));
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getLeftPwd() {

        int[] arr = MathUtils.prefectNumber();

        int index0 = arr[3] / 100 % 10;
        int index1 = arr[1] / 10;
        int index2 = index0 + index1;

        String leftPwd = Integer.toString(index0)+ index1 + index2;
        return leftPwd;
    }

    public static String getRightPwd() {

        int[] arr = MathUtils.prefectNumber();
        int index3 = arr[2] / 100;
        int index4 = arr[2] % 10 - 1;
        int index5 = arr[0];

        String rightPwd = Integer.toString(index3) + index4 + index5;
        return rightPwd;
    }

    /**
     * 检测String是否全是中文
     * @param name
     * @return
     */
    public static boolean checkNameChinese(String name) {
        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]*");
        if (p.matcher(name).matches()) {
            temp = true;
        }
        return temp;
    }

    public static String flowmeterChange(double data, boolean isDecimal) {

        String result = "";
        if (data >= 1024 * 1024) {
            if (isDecimal) {
                DecimalFormat numformat = new DecimalFormat("0.00");
                result = numformat.format(data / 1024 / 1024) + "G";
            } else {
                result = (int)data / 1024 / 1024 + "G";
            }
        } else if (data >= 1024) {
            if (isDecimal) {
                DecimalFormat numformat = new DecimalFormat("0.00");
                result = numformat.format(data / 1024) + "M";
            } else {
                result = (int)data / 1024 + "M";
            }
        } else {
            result = data + "K";
        }
        return result;
    }
}
