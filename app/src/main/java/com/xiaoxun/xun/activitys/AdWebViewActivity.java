package com.xiaoxun.xun.activitys;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telecom.websdk.Callback;
import com.telecom.websdk.LoginProgressInterface;
import com.telecom.websdk.LoginWebViewClient;
import com.telecom.websdk.NormalWebView;
import com.telecom.websdk.WebConfig;
import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.LogUtil;

public class AdWebViewActivity extends NormalActivity implements View.OnClickListener {

    private final String URL = FunctionUrl.AD_DEFAULT_URL;
    private NormalWebView webcontent;
    private ProgressBar pg_progress;
    private TextView tv_title;
    private View iv_title_back;
    private int targetType;
    private String testImei;
    private String targetUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_web_view);

        initView();
        initWebSettings();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dealJavascriptLeak();
        }

        targetUrl = getIntent().getStringExtra("targetUrl");
        targetType = getIntent().getIntExtra("activityType", 0);
        testImei = getIntent().getStringExtra("testimei");
       if(targetUrl == null){
            targetUrl = URL;
       }
        new Thread(new Runnable() {
            @Override
            public void run() {
                runWebView();
            }
        }).run();

    }

    private void runWebView(){
        if (WebConfig.isMiui(this)) {
            WebConfig.configureNormalWebViewByAD(AdWebViewActivity.this, webcontent,
                    new LoginWebViewClient() {
                        public boolean isInstall(Intent intent) {
                            return getMyApp().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
                        }
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            }else if(url.startsWith("http:") || url.startsWith("https:")) {
                               /* view.loadUrl(url);
                                */
                                LogUtil.e(url);
                            }else {
                                LogUtil.e(url);
                                try {
                                    if (!url.startsWith("http") || !url.startsWith("https") || !url.startsWith("ftp")) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        if (isInstall(intent)) {
                                            getMyApp().startActivity(intent);
                                            return true;
                                        }
                                    }
                                } catch (Exception e) {
                                    return false;
                                }
                            }
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                        @Override
                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            super.onReceivedError(view, errorCode, description, failingUrl);
                            String errorHtml = "<html><head><title>"+getString(R.string.ad_title)+"</title></head><body><div style=\"position:relative;height:" +
                                    "100%; width:100%; display:table; *position:absolute; *top:50%; " +
                                    "*left:0;\"><p style=\"font-size:50px;position:absolute; top:40%; left:0; text" +
                                    "-align:center; width:100%; *top:0;\">"+getString(R.string.adview_net_error)+"</p>" +
                                    "</div></body></html>";
                            webcontent.loadDataWithBaseURL("about:blank",errorHtml,"text/html", "UTF-8",null);
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                        }
                    }, new WebChromeClient() {


                        public void onReceivedTitle(WebView view, String title) {
                            super.onReceivedTitle(view, title);
                            tv_title.setText(title);
                        }
                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            super.onProgressChanged(view, newProgress);
                            if(newProgress==100){
                                pg_progress.setVisibility(View.GONE);//加载完网页进度条消失
                            }
                            else{
                                pg_progress.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                                pg_progress.setProgress(newProgress);//设置进度值
                            }
                        }
                    }, new Callback() {
                        @Override
                        public void closeWindow() {
                            LogUtil.e("close window");
                            if(!myApp.getIsMainActivityOpen()){
                                goTOMain();
                            }
                            finish();
                            sendRefreshRobbotBroast();
                        }

                        @Override
                        public void backClose() {
                            if(!myApp.getIsMainActivityOpen()){
                                goTOMain();
                            }
                            finish();
                            sendRefreshRobbotBroast();
                        }
                    }, new LoginProgressInterface() {
                        @Override
                        public void showLoginProgress() {
//                            layerWebViewLoad.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void hideLoginProgress() {
//                            layerWebViewLoad.setVisibility(View.INVISIBLE);
                        }
                    }
            );
        }else {
            WebConfig.configureNormalWebViewByAD(AdWebViewActivity.this, webcontent, new MyWebViewClient()
                    , new WebChromeClient() {
                        public void onReceivedTitle(WebView view, String title) {
                            super.onReceivedTitle(view, title);
                            tv_title.setText(title);
                        }

                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            super.onProgressChanged(view, newProgress);                         
                            if(newProgress==100){
                                pg_progress.setVisibility(View.GONE);//加载完网页进度条消失
                            }
                            else{
                                pg_progress.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                                pg_progress.setProgress(newProgress);//设置进度值
                            }
                        }
                    }, new Callback() {
                        @Override
                        public void closeWindow() {
                            //
                            LogUtil.e("close window");
                            if(!myApp.getIsMainActivityOpen()){
                                goTOMain();
                            }
                            finish();
                            sendRefreshRobbotBroast();
                        }

                        @Override
                        public void backClose() {
                            if(!myApp.getIsMainActivityOpen()){
                                goTOMain();
                            }
                            finish();
                            sendRefreshRobbotBroast();
                        }
                    });
        }
        webcontent.loadUrl(targetUrl);
        //支持下载功能
        webcontent.setDownloadListener(new MyWebViewDownLoadListener());
    }

    private void initView(){
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ad_title));
        iv_title_back = findViewById(R.id.iv_title_back);
        iv_title_back.setOnClickListener(this);
        webcontent = findViewById(R.id.webcontent);
        pg_progress = findViewById(R.id.pg_progressa);

    }

    private void initWebSettings(){
        WebSettings settings = webcontent.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent+" Packagename/"+ BuildConfig.APPLICATION_ID);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dealJavascriptLeak() {
        webcontent.removeJavascriptInterface("searchBoxJavaBridge_");
        webcontent.removeJavascriptInterface("accessibility");
        webcontent.removeJavascriptInterface("accessibilityTraversal");
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            String errorHtml = "<html><head><title>"+getString(R.string.ad_title)+"</title></head><body><div style=\"position:relative;height:" +
                    "100%; width:100%; display:table; *position:absolute; *top:50%; " +
                    "*left:0;\"><p style=\"font-size:50px;position:absolute; top:40%; left:0; text" +
                    "-align:center; width:100%; *top:0;\">"+getString(R.string.adview_net_error)+"</p>" +
                    "</div></body></html>";
            view.loadDataWithBaseURL("about:blank", errorHtml, "text/html", "UTF-8", null);

        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }
        public boolean isInstall(Intent intent) {
            return getMyApp().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
            }else if(url.startsWith("http:") || url.startsWith("https:")) {
                   /* view.loadUrl(url);
                    */
                LogUtil.e(url);
            }else{
                try {
                    if (!url.startsWith("http") || !url.startsWith("https") || !url.startsWith("ftp")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        if (isInstall(intent)) {
                            getMyApp().startActivity(intent);
                            return true;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                LogUtil.e(url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    protected void onResume() {
        if(myApp.getCurUser().getFocusWatch() != null){
            StringBuilder recordTitle = new StringBuilder("广告活动界面");
            if(myApp.getCurUser().getFocusWatch().isDevice102()){
                recordTitle.append("_102");
            }else{
                recordTitle.append("_105");
            }
        }
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(webcontent.canGoBack()){
                webcontent.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webcontent.goBack();
                return true;
            }else{
                if(!myApp.getIsMainActivityOpen()){
                    goTOMain();
                }
                finish();
                sendRefreshRobbotBroast();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        if(view == iv_title_back){
            if(webcontent.canGoBack()){
                webcontent.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webcontent.goBack();
            }else{
                if(!myApp.getIsMainActivityOpen()){
                    goTOMain();
                }
                finish();
                sendRefreshRobbotBroast();
            }
        }
    }

    private void sendRefreshRobbotBroast(){
        Intent intent = new Intent(Const.ACTION_BROAST_DISCOVERY_FIND);
        sendBroadcast(intent);
    }

    private void goTOMain(){
        Intent intent =new Intent(AdWebViewActivity.this, NewMainActivity.class);
        intent.putExtra("adwebBack","true");
        startActivity(intent);
    }
}
