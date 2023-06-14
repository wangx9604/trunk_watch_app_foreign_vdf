package com.xiaoxun.xun.activitys;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.telecom.websdk.Callback;
import com.telecom.websdk.NormalWebView;
import com.telecom.websdk.WebConfig;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.BitmapUtilities;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MioAsyncTask;
import com.xiaoxun.xun.utils.StrUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

public class RepairCommitActivity extends NormalActivity {

    private NormalWebView wv_commit;
    private String commit_url = "http://xxkj.ewei.com/client/?provider_id=13283&uid=3nardhUc9g4fweHYkATxhR4YECZi9yhK";
    private String imei;
    private String uid;
    private String bDate ;
    private String deviceName ;
    private String phone;
    private final String appKey = "MTA5NA==";
    private final String appSectet = "3bddgfd215525f24901dff8f1gfdf925";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapair_commit);

        phone = getIntent().getStringExtra("phone");
        imei = getIntent().getStringExtra("imei");
        uid = getIntent().getStringExtra("uid");
        bDate = getIntent().getStringExtra("bdate");
        deviceName = getIntent().getStringExtra("deviceName");
        commit_url+="&externalId="+uid+"&ticket_customField_139734="+deviceName+
        "&ticket_customField_99494="+imei+"&ticket_customField_100383="+phone+"&ticket_customField_144246="+bDate
        +"&hidden_fields=ticket_customField_144246";
        LogUtil.e("loadUrl"+commit_url);
        wv_commit = findViewById(R.id.wv_repair_commit);
        initWebSettings();
        dealJavascriptLeak();
        initWebConfig();
        findViewById(R.id.iv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.repair_track_commit);
        syncHttpGet();
    }

    private void syncHttpGet(){
        new MioAsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try{
                    Date date = new Date();
                    String timeStamp = String.valueOf(date.getTime());
                    String getCustomIdUrl = "http://xxkj.ewei.com/api/v1/get_user_token.json";
                    String getCustomParms = "account="+myApp.getCurUser().getEid();
                    String encodeSectet = AESUtil.decryptKaiSa(appSectet);
                    String parms = AESUtil.getCustomIdSign(encodeSectet,timeStamp,getCustomParms);

                    String sign = AESUtil.calcTransAllParmsSign("Get",getCustomIdUrl,parms);
                    LogUtil.e("repair query sign11111:"+sign+":"+
                            timeStamp+":"+encodeSectet+":"+getCustomParms+":"+getCustomIdUrl);

                    String responseData = ImibabyApp.HttpGetJsonData(getCustomIdUrl+"?"+getCustomParms,appKey,timeStamp,sign);
                    LogUtil.e("repair custom id Data11111:"+responseData);

                    return responseData;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(s);
                    int statue = (int)jsonObject.get("status");
                    if(statue == 0){
                        JSONObject jsonObject1 = (JSONObject)jsonObject.get("result");
                        String _token = (String)jsonObject1.get("token");
                        commit_url+="&_token="+_token;
                    }else{
                        LogUtil.e("statue:"+statue);
                    }
                    LogUtil.e("here commoit:"+commit_url);
                }catch(Exception e){
                    e.printStackTrace();
                }
                wv_commit.loadUrl(commit_url);
            }
        }.execute();

    }

    private void initWebSettings(){
        WebSettings settings = wv_commit.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dealJavascriptLeak() {
        wv_commit.removeJavascriptInterface("searchBoxJavaBridge_");
        wv_commit.removeJavascriptInterface("accessibility");
        wv_commit.removeJavascriptInterface("accessibilityTraversal");
    }

    private void initWebConfig(){
        WebConfig.configureNormalWebViewByAD(RepairCommitActivity.this, wv_commit, new WebViewClient()
                , new WebChromeClient() {
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                    }

                    // For Android < 3.0
                    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                        mUploadMessage = uploadMsg;
                        getImageFilePath();
                    }

                    // For Android > 4.1.1
                    public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                                String acceptType, String capture) {
                        mUploadMessage = uploadMsg;
                        getImageFilePath();
                    }

                    // For Android > 5.0支持多张上传
                    @Override
                    public boolean onShowFileChooser(WebView webView,
                                                     ValueCallback<Uri[]> uploadMsg,
                                                     FileChooserParams fileChooserParams) {
                        mUploadCallbackAboveL = uploadMsg;
                        getImageFilePath();
                        return true;
                    }

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                    }
                }, new Callback() {
                    @Override
                    public void closeWindow() {
                        finish();
                    }

                    @Override
                    public void backClose() {
                        finish();
                    }
                });
    }

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private final int IMAGE_CODE = 0; // 这里的IMAGE_CODE是自己任意定义的
    private final int IMAGE_CODE_1 = 1;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadMessage;

    private void getImageFilePath(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("requestcode:"+requestCode+":"+resultCode);
        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK){
            try {
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
            } catch (Exception e) {
                e.printStackTrace();
                cancleValueCallBack();
            }
        } else if (requestCode == IMAGE_CODE_1 && resultCode == RESULT_OK) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPicToView(data);
                }
            }, 100);
        } else if (resultCode == RESULT_CANCELED) {
            cancleValueCallBack();
        }
    }

    private void cancleValueCallBack(){
        if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        } else if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
            mUploadMessage = null;
        }
    }

    private File cropTemp = null;
    public void startPhotoZoom(Uri uri) {

        File temp = new File(ImibabyApp.getIconCacheDir(), "tempcrop" + ".jpg");
        if (temp.exists()) {
            temp.delete();
        }
        cropTemp = temp;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 720);
        intent.putExtra("outputY", 720);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropTemp));
        intent.putExtra("return-data", false);//设置为不返回数据
        startActivityForResult(intent, IMAGE_CODE_1);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void setPicToView(Intent picdata) {

        Bitmap photo = null;
        try {
            photo = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(cropTemp));
            if (photo.getWidth() > 720 || photo.getHeight() > 720) {
                photo = BitmapUtilities.getBitmapThumbnail(cropTemp.getPath(), 720, 720);
            }
            FileOutputStream fos = new FileOutputStream(cropTemp);
            photo.compress(Bitmap.CompressFormat.JPEG, 70, fos);

            //创建文件输出流
            OutputStream os;
            byte[] bitmapArray = StrUtil.getBytesFromFile(cropTemp);

            File destFile = new File(ImibabyApp.getIconCacheDir(), TimeUtil.getTimeStampLocal() + ".png");
            cropTemp.renameTo(destFile);
            os = new FileOutputStream(destFile);
            os.write(bitmapArray);
            os.flush();

            Uri originalUri = Uri.fromFile(destFile); // 获得图片的uri

            if (mUploadCallbackAboveL != null) {
                Uri[] uris = new Uri[]{originalUri};
                mUploadCallbackAboveL.onReceiveValue(uris);
                mUploadCallbackAboveL = null;
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(originalUri);
                mUploadMessage = null;
            } else {
            }

        } catch (FileNotFoundException e) {
            cancleValueCallBack();
        } catch (Exception e) {
            cancleValueCallBack();
        }
    }

}
