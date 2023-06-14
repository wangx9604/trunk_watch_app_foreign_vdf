package com.xiaoxun.xun.views;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NewMainActivity;
import com.xiaoxun.xun.activitys.StoreActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.AndroidUtil;
import com.xiaoxun.xun.utils.BASE64Encoder;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

import java.net.URLEncoder;

/**
 * @author cuiyufeng
 * @Description: StoreFragment
 * @date 2018/7/12 16:29
 */
@SuppressLint("ValidFragment")
public class StoreFragment extends Fragment {
    private String KEY_HELP_URL ="https://shop.xunkids.com/mobile/xunlogin.htm?vs=1";
    private String Main_Url = "mobile/main.html";
    //http://mshop.xunkids.com/mobile/main.html
    //private String KEY_HELP_URL = "http://52.80.189.34:8000/mobile/xunlogin.htm";

    private MyWebView mWebView;
    private ProgressBar pg_progress;
    private View iv_title_back, iv_title_menu;
    SwipeRefreshLayout mSwipeRefreshView;
    String encryptData;
    ImibabyApp mApp;
    NewMainActivity activity;
    View viewRoot;
    String nikname;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewMainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewRoot= inflater.inflate(R.layout.activity_help_web, container, false);
        Log.i("cui","StoreFragment==================================onCreateView");
        return viewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("cui","StoreFragment==================================onActivityCreated");
        if(mApp==null){
            mApp = (ImibabyApp)getActivity().getApplication();
        }
        if(activity==null){
            activity = (NewMainActivity)getActivity();
        }
        nikname=mApp.getStringValueNoDecrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME,"");
        initViews(viewRoot);
        setData();
    }

    private void initViews(View viewRoot) {
        ((TextView) viewRoot.findViewById(R.id.tv_title)).setText(getResources().getString(R.string.store));
        iv_title_back = viewRoot.findViewById(R.id.iv_title_back);
        iv_title_back.setVisibility(View.GONE);
        iv_title_menu = viewRoot.findViewById(R.id.iv_title_menu);
        iv_title_menu.setVisibility(View.GONE);
        mWebView = viewRoot.findViewById(R.id.webcontent);
        pg_progress = viewRoot.findViewById(R.id.pg_progres_store);

        mSwipeRefreshView = viewRoot.findViewById(R.id.refresh);
        mSwipeRefreshView.setColorSchemeResources(R.color.bg_color_orange);
        mWebView.setViewGroup(mSwipeRefreshView);
        initListener();
    }

    private void initListener() {
        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadFinish = true;
                loadPostUrl();
                mSwipeRefreshView.setRefreshing(false);
            }
        });
        mSwipeRefreshView.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return mWebView.getScrollY() > 0;
            }
        });
    }

    private void setData() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + " Packagename/" + BuildConfig.APPLICATION_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dealJavascriptLeak();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setWebViewClientListeners();
    }

    private void setWebViewClientListeners() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //Log.i("cui","newProgress=="+newProgress);
                if (newProgress == 100) {
                    pg_progress.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
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
                    if ("mqqwpa".equals(scheme) && !AndroidUtil.isQQClientAvailable(getActivity())) {
                        ToastUtil.show(getActivity(), getString(R.string.qq_not_installed));
                        return true;
                    }
                    if ("alipays".equals(scheme) && !AndroidUtil.checkAliPayInstalled(getActivity())) {
                        ToastUtil.show(getActivity(), getString(R.string.alipay_not_installed));
                        return true;
                    }
                    if ("weixin".equals(scheme) && !AndroidUtil.isWeixinAvilible(getActivity())) {
                        ToastUtil.show(getActivity(), getString(R.string.weixin_not_installed));
                        return true;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                    startActivity(intent);
                    return true;
                }
                Log.i("cui", "" + request.getUrl().toString());
                if (request.getUrl().toString().contains(Main_Url) || request.getUrl().toString().equals(KEY_HELP_URL)) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
                Intent _intent = new Intent(getActivity(), StoreActivity.class);
                _intent.putExtra("targetUrl", request.getUrl().toString());
                _intent.putExtra("encryptData", encryptData);
                startActivity(_intent);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                String scheme = uri.getScheme();
                LogUtil.e("XunStoreActivity scheme: " + scheme);
                if ("mqqwpa".equals(scheme) || "alipays".equals(scheme)|| "weixin".equals(scheme)) {
                    if ("mqqwpa".equals(scheme) && !AndroidUtil.isQQClientAvailable(getActivity())) {
                        ToastUtil.show(getActivity(), getString(R.string.qq_not_installed));
                        return true;
                    }
                    if ("alipays".equals(scheme) && !AndroidUtil.checkAliPayInstalled(getActivity())) {
                        ToastUtil.show(getActivity(), getString(R.string.alipay_not_installed));
                        return true;
                    }
                    if ("weixin".equals(scheme) && !AndroidUtil.isWeixinAvilible(getActivity())) {
                        ToastUtil.show(getActivity(), getString(R.string.weixin_not_installed));
                        return true;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }

                Log.i("cui", "" + url);
                if (url.contains(Main_Url) || url.equals(KEY_HELP_URL)) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                Intent _intent = new Intent(getActivity(), StoreActivity.class);
                _intent.putExtra("targetUrl", url);
                _intent.putExtra("encryptData", encryptData);
                startActivity(_intent);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isLoadFinish = false;
                if(isAdded()){
                    String errorHtml = "<html><body><div style=\"position:relative;height:" +
                            "100%; width:100%; display:table; *position:absolute; *top:50%; " +
                            "*left:0;\"><p style=\"font-size:50px;position:absolute; top:40%; left:0; text" +
                            "-align:center; width:100%; *top:0;\">" + getString(R.string.adview_net_error) + "</p>" +
                            "</div></body></html>";
                    mWebView.loadDataWithBaseURL("about:blank", errorHtml, "text/html", "UTF-8", null);
                }
            }
        });

        /*if(isLoadFinish&&!isLoadUrl){
            if(mApp!=null){
                if(mWebView!=null){
                    loadPostUrl();
                    isLoadUrl=true;
                }
            }
        }*/
        loadPostUrl();
    }

    private void dealJavascriptLeak() {
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
    }

    private void loadPostUrl() {
        Log.i("cui","loadPostUrl----");
        if (viewRoot == null) {
            return;
        }
        isLoadUrl=true;
        isLoadFinish = true;
        if (encryptData != null && mWebView!=null) {
            try {
                mWebView.postUrl(KEY_HELP_URL, URLEncoder.encode(encryptData, "utf-8").getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(mWebView!=null){
            if(mApp!=null){
                if(mApp.needAutoLogin() && !TextUtils.isEmpty(mApp.getNetService().getAESKEY())){
                    JSONObject postData = new JSONObject();
                    postData.put(CloudBridgeUtil.KEY_NAME_EID, mApp.getCurUser().getEid());
                    postData.put("openId", mApp.getLoginId());
                    postData.put("uuid", mApp.getLastUnionId());
                    postData.put("nickName", mApp.getCurUser().getNickname());
                    postData.put("cellPhone", mApp.getCurUser().getCellNum());
                    postData.put("headImg", mApp.getCurUser().getHeadPath());
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
        }
    }

    public boolean isLoadFinish = false;//判断页面加载成功或者失败
    private boolean isLoadUrl;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("cui","StoreFragment==================================setUserVisibleHint");
        if (viewRoot == null) {
            return;
        }
        if (getUserVisibleHint() && !isLoadFinish) {
            loadPostUrl();
        }
        String newNikname=mApp.getStringValueNoDecrypt(Const.SHARE_PREF_FIELD_MY_NICKNAME,"");
        //修改一个bug 先进入商城加载了一次，在登录，然后再到商城，解决已经登录了URL，在加载商城就不加载带参数的链接了导致没办法购买。
        if(!TextUtils.isEmpty(newNikname) && !newNikname.equals(nikname)  ){
            nikname=newNikname;
            loadPostUrl();
        }
        if (isVisibleToUser && mApp != null) {
            Log.e("visible", "storeFragment");
            WatchData focusWatch = mApp.getCurUser().getFocusWatch();
        }
    }

}