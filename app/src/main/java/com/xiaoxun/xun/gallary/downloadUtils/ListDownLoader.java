package com.xiaoxun.xun.gallary.downloadUtils;

import android.os.AsyncTask;
import android.util.Log;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by xilvkang on 2017/3/24.
 */

public class ListDownLoader extends AsyncTask<String,Integer,String> {
    public static final int TYPE_LIST = 0;
    public static final int TYPE_FILE = 1;
    private URL url;
    private String post;
    private String path;
    public int type;

    private DownloadListener listener;


    public ListDownLoader(final DownloadListener listener){
        this.listener = listener;
    }

    public void HttpsDownloadList(String url, String post) {
        try {
            this.url = new URL(url);
            this.post = post;
            this.type = 0;

            this.execute(String.valueOf(type));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        listener.onStartDownload();

    }

    private String DoHttpsDowabload(){
        String result = "";
        HttpsURLConnection con = null;
        try {
            con = (HttpsURLConnection)url.openConnection();
            con.setConnectTimeout(20000);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setDoInput(true);
            con.setDoOutput(true);
//            JSONObject obj = (JSONObject) JSONValue.parse(post);
//            byte[] message = obj.toString().getBytes();
            int length = post.length();
            //con.setRequestProperty("Content-Length", "" + length);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
            out.write(post);
            out.flush();
            out.close();

            int rc = con.getResponseCode();
            if(rc == 200) {
                InputStream is = con.getInputStream();
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                    result += byteArrayToString(buffer);
                    if(is.available() < 1024){
                        buffer = null;
                        buffer = new byte[1024];
                    }
                }
                is.close();
            }else{
                listener.onError(String.valueOf(con.getResponseCode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            con.disconnect();
        }
        return result;
    }

    private void DoHttpsDowabloadToFile(String path){
        String line = "";
        HttpsURLConnection con = null;
        try {
            con = (HttpsURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setDoInput(true);
            con.setDoOutput(true);
            JSONObject obj = (JSONObject) JSONValue.parse(post);
            byte[] message = obj.toString().getBytes();
            int length = message.length;
            con.setRequestProperty("Content-Length", "" + length);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
            out.write(obj.toString());
            out.flush();
            out.close();

            int rc = con.getResponseCode();
            if(rc == 200){
                InputStream is = con.getInputStream();
                byte[] buffer = new byte[1024];
                File fp = new File(path);
                if (fp.exists()) {
                    fp.delete();
                }
                OutputStream fout = new FileOutputStream(fp);
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                }
                is.close();
                fout.close();

            }else{
                listener.onError(String.valueOf(con.getResponseCode()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = null;
        if(strings[0].equals("0")){
            result = DoHttpsDowabload();
            Log.e("xxxx","doInBackground : 0");
            return result;
        }else{
            DoHttpsDowabloadToFile(path);
            Log.e("xxxx","doInBackground : 1");
            return path;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        listener.onFinished(s);
    }

    private String byteArrayToString(byte[] array){
        String ret = "";
//        for(byte b : array){
//            if(b != 0){
//                ret += (char)b;
//            }
//        }
        ret = new String(array, StandardCharsets.UTF_8);
        return ret;
    }
}
