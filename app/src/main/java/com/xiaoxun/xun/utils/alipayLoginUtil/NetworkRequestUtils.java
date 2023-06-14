package com.xiaoxun.xun.utils.alipayLoginUtil;

import android.content.Context;
import android.util.Base64;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.beans.MyMsgData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhangjun5 on 2019/7/3.
 */

public class NetworkRequestUtils {

    private String TAG = "NetworkRequestUtils";
    public interface OperationCallback{
        void onSuccess(String result);
        void onFail(String error);
    }

    private static NetworkRequestUtils instance;
    private Context ctxt;

    private NetworkRequestUtils(Context context) {
        ctxt = context;
    }

    public static synchronized NetworkRequestUtils getInstance(Context context) {
        if (instance == null)
            instance = new NetworkRequestUtils(context);
        return instance;
    }

    public void getNetWorkRequest(String dataStr, String AES_KEY, String sid, String httpUrl, final OperationCallback callback){
        OkHttpClient client = new OkHttpClient();
        String url = httpUrl;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(dataStr, AES_KEY, AES_KEY), Base64.NO_WRAP) + sid;
        RequestBody body = RequestBody.create(JSON, reqBody);
        LogUtil.e("workrequest:"+dataStr+":"+httpUrl);
        final Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(TAG + " IOException = " + e.toString());
                callback.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String results = response.body().string();
                LogUtil.i(TAG + "results = " + results);
                callback.onSuccess(results);
            }
        });
    }

    public static void getMAiResponse(ImibabyApp myApp, String url, final OperationCallback callback){
        LogUtil.e("MAI response:"+url);
        myApp.sdcardLog("MAI Info:"+url);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).addHeader("X-Checkless-Auth", "EAKYEMGII4RFU").get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.i(" IOException = " + e.toString());
                callback.onFail(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String results = response.body().string();
                LogUtil.i("results = " + results);
                callback.onSuccess(results);
            }
        });
    }

    //android上传文件到服务器
    public static String uploadFile(String RequestURL, String filePath,final String appKey,final String timeStamp,final String sign) {

        LogUtil.e("file:"+RequestURL+":"+filePath);
        int TIME_OUT = 100000; //超时时间
        String CHARSET = "utf-8"; //编码格式
        String FAILURE = "FAILURE";
        //生成一个文件
        File file = new File(filePath);
        //边界标识 随机生成，这个作为boundary的主体内容
        String BOUNDARY = UUID.randomUUID().toString();

        String PREFIX = "--";
        //回车换行，用于调整协议头的格式
        String LINE_END = "\r\n";
        //格式的内容信息
        String CONTENT_TYPE = "multipart/form-data";

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign",sign);

            //设置超时时间
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            //允许输入流
            conn.setDoInput(true);
            //允许输出流
            conn.setDoOutput(true);
            //不允许使用缓存
            conn.setUseCaches(false);
            //请求方式
            conn.setRequestMethod("POST");
            //设置编码 utf-8
            conn.setRequestProperty("Charset", CHARSET);
            //设置为长连接
            conn.setRequestProperty("connection", "keep-alive");

            //这里设置请求方式以及boundary的内容，即上面生成的随机字符串
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if (file != null) {
                //当文件不为空，把文件包装并且上传
                //这里定义输出流，用于之后向服务器发起请求
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);

                //这里的StringBuffer 用来拼接我们的协议头
                StringBuffer sb = new StringBuffer();

                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + LINE_END);

                //这里Content-Type 传给后台一个mime类型的编码字段，用于识别扩展名
                sb.append("Content-Type: image/png" + LINE_END);
                sb.append(LINE_END);

                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                //获取后台返回的数据
                if (res == 200) {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuffer sb_result = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb_result.append(line);
                    }
                    return sb_result.toString();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }

    public static String getTrackReplyJsonString(boolean open, String userId, String trackId,
                                          ArrayList<String> photoIdList, String trackStatus, String content) {
        JSONObject user = new JSONObject();
        user.put("id", Integer.valueOf(userId));
        JSONObject track = new JSONObject();
        track.put("id", Integer.valueOf(trackId));
        track.put("status",trackStatus);
        JSONArray attachments = new JSONArray();
        for(int i=0;i<photoIdList.size();i++){
            JSONObject phoneId = new JSONObject();
            phoneId.put("id", Integer.valueOf(photoIdList.get(i)));
            attachments.add(phoneId);
        }
        JSONObject replyJson = new JSONObject();
        replyJson.put("open",open);
        replyJson.put("user",user);
        replyJson.put("ticket",track);
        replyJson.put("content",content);
        replyJson.put("attachments",attachments);
        return replyJson.toJSONString();
    }

    public static String getPutReplySign(String sectet,String timestamp,String parmsUrl){
        String parms;
        parms= "_app_secret="+sectet+"_timestamp="+timestamp+"requestBody="+parmsUrl;
        return parms;
    }

    public static String HttpGetFileData(String getUrl, File file,String appKey, String timeStamp, String sign) {
        HttpURLConnection conn = null;
        String result = null;
        LogUtil.e("http url:"+getUrl);
        try {
            //发送get请求
            URL url = new URL(getUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign",sign);
            conn.connect();
            //获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LogUtil.e("connect failed!");

            } else {
                //获取响应内容体
                InputStream is = new BufferedInputStream(conn.getInputStream());
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file, true)));

                byte[] buf = new byte[2048];
                int readLen;
                while ((readLen = is.read(buf)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    dos.write(buf, 0, readLen);
                }   //回复结果
                result = "1";

                if(dos != null){
                    dos.close();
                }
                if(is != null){
                    is.close();
                }
            }
        } catch (Exception e) {
            result = "0";
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public static String HttpGetJsonData(String getUrl, String appKey, String timeStamp, String sign) {
        HttpURLConnection conn = null;
        String result = null;
        LogUtil.e("http url:"+getUrl);
        try {
            //发送get请求
            URL url = new URL(getUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign",sign);
            conn.connect();
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
                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public static String HttpPostJsonData(String postData, String postUrl, String appKey, String timeStamp, String sign) {
        StringBuilder sData = new StringBuilder();
        HttpURLConnection conn = null;
        String result = null;

        sData.append(postData);

        try {
            //发送POST请求
            URL url = new URL(postUrl);
            conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("_app_key", appKey);
            conn.setRequestProperty("_timestamp", timeStamp);
            conn.setRequestProperty("_sign",sign);
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            byte[] message = sData.toString().getBytes();
            int ll = sData.toString().length();
            int lngth = message.length;

            LogUtil.e("post json string length:" + ll + "bytes length" + lngth + "  sData:" + sData);
            conn.setRequestProperty("Content-Length", "" + lngth);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
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
                result = responseJson;    //回复结果
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }


    public static void sendDeviceSet(ImibabyApp myApp, MsgCallback callback, String eid, String key, Object value) {
        MyMsgData setMsg = new MyMsgData();
        setMsg.setCallback(callback);

        JSONObject pl = new JSONObject();
        pl.put(key, value);
        pl.put(CloudBridgeUtil.KEY_NAME_EID, eid);
        setMsg.setReqMsg(myApp.obtainCloudMsgContent(CloudBridgeUtil.CID_DEVICE_SET, pl));
        if (myApp.getNetService() != null)
            myApp.getNetService().sendNetMsg(setMsg);
    }

}
