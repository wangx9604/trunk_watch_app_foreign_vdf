package com.xiaoxun.xun.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by huangyouyang on 2016/12/12.
 */

public class HttpNetUtils {

    public static String httpPostJson(String postData, String postUrl, boolean needTimeout) {

        StringBuilder sData = new StringBuilder();
        HttpsURLConnection conn = null;
        String result = null;

        sData.append(postData);

        try {
            //发送POST请求
            URL url = new URL(postUrl);
            conn = (HttpsURLConnection) url.openConnection();

            // check for new app/watch version, set timeout - 3s
            if (needTimeout) {
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
            }

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            byte[] message = sData.toString().getBytes();
            int ll = sData.toString().length();
            int lngth = message.length;

            LogUtil.e("post json string length:" + ll + "bytes length" + lngth+":"+postData);
            Log.i("cui","post json string length:" + ll + "bytes length" + lngth + "  sData:" + sData);
            conn.setRequestProperty("Content-Length", "" + lngth);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            out.write(sData.toString());
            out.flush();
            out.close();

            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.e("connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    baos.write(buf, 0, readLen);
                }
                String responseJson = new String(baos.toByteArray());
                LogUtil.e("responseJson = " + responseJson);
                Log.i("cui","responseJson:" + responseJson);
                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }
}
