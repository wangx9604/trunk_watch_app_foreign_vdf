package com.xiaoxun.xun.activitys;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.AndroidUtil;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.MyWebView;

import net.minidev.json.JSONObject;

import java.net.URLEncoder;

/**
 * @author cuiyufeng
 * @Description: StoreActivity
 * @date 2018/7/13 15:36
 */
public class StoreActivity extends NormalActivity{
    private MyWebView mWebView;
    private ProgressBar pg_progress;
    private View iv_title_back;
    private String KEY_HELP_URL;
    private ImageButton ib_help_web;
    SwipeRefreshLayout mSwipeRefreshView;

    private String to_Url = "xunshoplogin://toUrl=";
    ImibabyApp mApp;
    String encryptData;
    public static final String store_url="store_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_web);
        if(getIntent()!=null){
            KEY_HELP_URL= getIntent().getStringExtra("targetUrl");
        }
        if(mApp==null){
            mApp = (ImibabyApp)getApplication();
        }
        initViews();
        setData();
    }

    private void initViews(){
        ((TextView)findViewById(R.id.tv_title)).setText(getString(R.string.store));
        ib_help_web = findViewById(R.id.ib_help_web);
        ib_help_web.setVisibility(View.VISIBLE);
        ib_help_web.setBackgroundResource(R.drawable.btn_home_selector);
        ib_help_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, NewMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        iv_title_back = findViewById(R.id.iv_title_back);
        iv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }else{
                    Intent intent = new Intent(StoreActivity.this, NewMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
        mWebView = findViewById(R.id.webcontent);
        pg_progress = findViewById(R.id.pg_progres_store);

        mSwipeRefreshView = findViewById(R.id.refresh);
        mSwipeRefreshView.setEnabled(false);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
    private void setData(){
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent+" Packagename/"+ BuildConfig.APPLICATION_ID);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkImage(false);

        dealJavascriptLeak();
        setWebViewClientListeners();
    }

    private void setWebViewClientListeners() {
        mWebView.setWebChromeClient(new WebChromeClient(){
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //Log.i("cui","newProgress=="+newProgress);
                if(newProgress==100){
                    pg_progress.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pg_progress.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg_progress.setProgress(newProgress);//设置进度值
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme();
                LogUtil.e("XunStoreActivity scheme: " + scheme);
                if ("mqqwpa".equals(scheme) || "alipays".equals(scheme)|| "weixin".equals(scheme)) {
                    if ("mqqwpa".equals(scheme) && !AndroidUtil.isQQClientAvailable(StoreActivity.this)) {
                        ToastUtil.show(StoreActivity.this, getString(R.string.qq_not_installed));
                        return true;
                    }
                    if ("alipays".equals(scheme) && !AndroidUtil.checkAliPayInstalled(StoreActivity.this)) {
                        ToastUtil.show(StoreActivity.this, getString(R.string.alipay_not_installed));
                        return true;
                    }
                    if ("weixin".equals(scheme) && !AndroidUtil.isWeixinAvilible(StoreActivity.this)) {
                        ToastUtil.show(StoreActivity.this, getString(R.string.weixin_not_installed));
                        return true;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                    startActivity(intent);
                    return true;
                }

                String toUrl=request.getUrl().toString();
                if(toUrl.contains(to_Url)){
                    String url=toUrl.substring(21);
                    Log.i("CUI","url1:="+url);
                    //denglu 登录
                    Intent intent=new Intent(StoreActivity.this,NewLoginActivity.class);
                    intent.putExtra(NewMainActivity.isShowBack,"true");
                    intent.putExtra(store_url,url);
                    startActivity(intent);
                    return  true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                String scheme = uri.getScheme();
                LogUtil.e("XunStoreActivity scheme: " + scheme);
                if ("mqqwpa".equals(scheme) || "alipays".equals(scheme) || "weixin".equals(scheme)) {

                    if ("mqqwpa".equals(scheme) && !AndroidUtil.isQQClientAvailable(StoreActivity.this)) {
                        ToastUtil.show(StoreActivity.this, getString(R.string.qq_not_installed));
                        return true;
                    }
                    if ("alipays".equals(scheme) && !AndroidUtil.checkAliPayInstalled(StoreActivity.this)) {
                        ToastUtil.show(StoreActivity.this, getString(R.string.alipay_not_installed));
                        return true;
                    }
                    if ("weixin".equals(scheme) && !AndroidUtil.isWeixinAvilible(StoreActivity.this)) {
                        ToastUtil.show(StoreActivity.this, getString(R.string.weixin_not_installed));
                        return true;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }

                if(url.contains(to_Url)){
                    String str=url.substring(21);
                    Log.i("CUI","url2:="+str);
                    //denglu 登录
                    Intent intent=new Intent(StoreActivity.this,NewLoginActivity.class);
                    intent.putExtra(NewMainActivity.isShowBack,"true");
                    intent.putExtra(store_url,str);
                    startActivity(intent);
                    return  true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        loadUrl();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // wherego 从登录来的就加载带参数的url
        if(intent!=null && !TextUtils.isEmpty(intent.getStringExtra("wherego"))){
            KEY_HELP_URL= intent.getStringExtra("targetUrl");
            loadPostUrl();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dealJavascriptLeak() {
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private void loadUrl() {
        mWebView.loadUrl(KEY_HELP_URL);
    }

    private void loadPostUrl(){
        if(mApp.needAutoLogin() && !TextUtils.isEmpty(mApp.getNetService().getAESKEY())){
            JSONObject postData = new JSONObject();
            String eid= mApp.getCurUser().getEid();
            postData.put(CloudBridgeUtil.KEY_NAME_EID, mApp.getCurUser().getEid());
            String loginId= mApp.getLoginId();
            postData.put("openId", mApp.getLoginId());
            String lastUnionId= mApp.getLastUnionId();
            postData.put("uuid", mApp.getLastUnionId());
            String nickname= mApp.getCurUser().getNickname();
            postData.put("nickName", mApp.getCurUser().getNickname());
            String cellNum= mApp.getCurUser().getCellNum();
            postData.put("cellPhone", mApp.getCurUser().getCellNum());
            String headPath = mApp.getCurUser().getHeadPath();
            postData.put("headImg", mApp.getCurUser().getHeadPath());
            String key=mApp.getNetService().getAESKEY();
            String token=mApp.getToken();
            String data= postData.toString();
            encryptData = BASE64Encoder.encode(AESUtil.encryptAESCBC(postData.toString(), mApp.getNetService().getAESKEY(), mApp.getNetService().getAESKEY())) + mApp.getToken();
            try {
                mWebView.postUrl(KEY_HELP_URL, URLEncoder.encode(encryptData, "utf-8").getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            mWebView.loadUrl(KEY_HELP_URL);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                Intent intent = new Intent(StoreActivity.this, NewMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
