package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.LogUtil;

public class HelpWebActivity extends NormalActivity implements View.OnClickListener {

    private static final String TAG = "HelpWebActivity";

    private WebView webcontent;
    private View mTitleBack;
    private TextView tvTitle;
    private ImageButton mBtnMenu;
    private int webType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_web);

        initView();
        initWebSettings();
        initData();
        dealJavascriptLeak();
        setWebViewClientListener();
    }

    private void initView() {

        tvTitle = findViewById(R.id.tv_title);
        webcontent = findViewById(R.id.webcontent);
        mTitleBack = findViewById(R.id.iv_title_back);
        mTitleBack.setOnClickListener(this);
        mBtnMenu = findViewById(R.id.iv_title_menu);
        mBtnMenu.setOnClickListener(this);
        SwipeRefreshLayout mSwipeRefreshView = findViewById(R.id.refresh);
        mSwipeRefreshView.setEnabled(false);
    }

    private void initData() {

        String url = getIntent().getStringExtra(Const.KEY_HELP_URL);
        String params = getIntent().getStringExtra(Const.KEY_PARAMS);
        webType = getIntent().getIntExtra(Const.KEY_WEB_TYPE, 0);
        loadUrl(url, params);
    }

    private void updateTitle() {

        switch (webType) {
            case Const.KEY_WEB_TYPE_HELP:
                tvTitle.setText(getString(R.string.setting_faq));
                break;
            case Const.KEY_WEB_TYPE_PRIVACY:
                tvTitle.setText(getString(R.string.app_privacy));
                break;
            case Const.KEY_WEB_TYPE_AIVOICE:
                int aiType = getIntent().getIntExtra(Const.KEY_AITYPE, 0);
                if (aiType == 1) {
                    tvTitle.setText(getString(R.string.function_control_aivoice));
                } else if (aiType == 2) {
                    tvTitle.setText(getString(R.string.function_control_aivoice_710));
                }
                break;
            case Const.KEY_WEB_TYPE_AISTATISTICS:
                tvTitle.setText(getString(R.string.function_control_aivoice));
                mBtnMenu.setVisibility(View.VISIBLE);
                mBtnMenu.setBackgroundResource(R.drawable.ai_voice_skill);
                break;
            case Const.KEY_WEB_TYPE_ENGSTATISTICS:
                tvTitle.setText(getString(R.string.function_control_eng));
                break;
            case Constants.KEY_WEB_TYPE_DISCLAIMER:
                tvTitle.setText(R.string.disclaimer_title);
                break;
            case Constants.KEY_WEB_TYPE_PRIVACY_POLICY:
                tvTitle.setText(R.string.privacy);
                break;
            case Constants.KEY_WEB_TYPE_AGREEMENT:
                tvTitle.setText(R.string.agreement);
                break;
            default:
                tvTitle.setText(getString(R.string.setting_faq));
                break;
        }
    }

    private void loadUrl(String url,String params) {

        LogUtil.i(TAG + " url:" + url);
        if (url == null)
            url = FunctionUrl.APP_HELP_CENTER_URL;
        if (params == null)
            webcontent.loadUrl(url);
        else {
            LogUtil.i(TAG + " params:" + params);
            webcontent.postUrl(url, params.getBytes());
        }
    }

    private void setWebViewClientListener() {

        webcontent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                LogUtil.i(TAG + " override url:" + url);
                if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else if(url.contains("bbs.xiaomi.cn")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        webcontent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title == null || title.equals("")) {
                    updateTitle();
                } else {
                    tvTitle.setText(title);
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings() {

        WebSettings settings = webcontent.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // 设置userAgent
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent+" Packagename/"+ BuildConfig.APPLICATION_ID);
        userAgent = settings.getUserAgentString();
        LogUtil.i(TAG + " User Agent:" + userAgent);
    }

    private void dealJavascriptLeak() {
        webcontent.removeJavascriptInterface("searchBoxJavaBridge_");
        webcontent.removeJavascriptInterface("accessibility");
        webcontent.removeJavascriptInterface("accessibilityTraversal");
    }

    @Override
    public void onClick(View view) {

        if(view == mTitleBack){
            if(webcontent.canGoBack()){
                webcontent.goBack();
            }else{
                finish();
            }
        } else if (view == mBtnMenu) {
            // 直接再起HelpWebActivity
            Intent aiIntent = new Intent(HelpWebActivity.this, HelpWebActivity.class);
            aiIntent.putExtra(Const.KEY_WEB_TYPE, Const.KEY_WEB_TYPE_AIVOICE);
            aiIntent.putExtra(Const.KEY_AITYPE, 1);
            aiIntent.putExtra(Const.KEY_HELP_URL, FunctionUrl.APP_AI_VOICE_DISPLAY_URL);
            aiIntent.putExtra(Const.KEY_PARAMS, myApp.getAiVoiceParams());
            startActivity(aiIntent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(webcontent.canGoBack()){
                webcontent.goBack();
                return true;
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
