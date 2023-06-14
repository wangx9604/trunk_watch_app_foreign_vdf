package com.xiaoxun.xun.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoxun.xun.BuildConfig;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.AdWebViewActivity;
import com.xiaoxun.xun.beans.ADShowData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.LogUtil;
import com.telecom.websdk.Callback;
import com.telecom.websdk.LoginProgressInterface;
import com.telecom.websdk.LoginWebViewClient;
import com.telecom.websdk.NormalWebView;
import com.telecom.websdk.WebConfig;


@SuppressLint("ValidFragment")
public class adWebViewFragment extends Fragment {

    private final String URL = FunctionUrl.AD_DEFAULT_URL;
    private NormalWebView webcontent;
    private ProgressBar pg_progress;
    private SwipeRefreshLayout mSwipeRefreshView;
    private TextView tv_title;
    private View iv_title_back;
    private View iv_title_menu;
    private BroadcastReceiver broadcastReceiver;
    String targetUrl = null;
    private ImibabyApp myApp;
    public boolean isLoadFinish = false;//判断页面加载成功或者失败
    Activity activity;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(myApp==null){
            myApp = (ImibabyApp)getActivity().getApplication();
        }
        if(activity==null){
            activity = getActivity();
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    try{
                        LogUtil.e("ad cloud change:"+getUserVisibleHint()+":"+isLoadFinish);
                        myApp.sdcardLog("ADDOWNLOAD adwebview:"+getUserVisibleHint()+":"+isLoadFinish);

                    }catch (Exception e){
                        LogUtil.i(e.toString());
                    }
                }else if(intent.getAction().equals(Const.ACTION_BROAST_DISCOVERY_FIND)){
//                    String postData = "requestData=" + myApp.getAppAdReqJsonString("0",null);
//                    LogUtil.i("roddot refresh:" + postData);
//                    webcontent.postUrl(targetUrl, postData.getBytes());
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Const.ACTION_BROAST_DISCOVERY_FIND);
        if(activity!=null){
            activity.registerReceiver(broadcastReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ad_web_view, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_title = getView().findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.ad_title));
        iv_title_back = getView().findViewById(R.id.iv_title_back);
        iv_title_back.setVisibility(View.GONE);
        iv_title_menu = getView().findViewById(R.id.iv_title_menu);
        iv_title_menu.setVisibility(View.GONE);
        webcontent = getView().findViewById(R.id.webcontent);
        pg_progress = getView().findViewById(R.id.pg_progressf);
        mSwipeRefreshView= getView().findViewById(R.id.swipe_refresh);
        mSwipeRefreshView.setColorSchemeResources(R.color.bg_color_orange);
        //webcontent.setViewGroup(swipe_refresh);
        initListener();


        WebSettings settings = webcontent.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent+" Packagename/"+ BuildConfig.APPLICATION_ID);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        ADShowData taskData = new ADShowData();
        for(int i = 0;i < myApp.getAdShowList().size();i++){
            taskData = myApp.getAdShowList().get(i);
            if(taskData.adType == 1){
                break;
            }
        }
        if(taskData != null){
            targetUrl = taskData.adTarUrl;
        }
        if(targetUrl == null){
            targetUrl = URL;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dealJavascriptLeak();
        }

        if (WebConfig.isMiui(activity)) {
            WebConfig.configureNormalWebViewByAD(getActivity(), webcontent,
                    new LoginWebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            if(url.equals("http://miwifi.com/diagnosis/index.html")){
                                view.loadUrl(url);
                            }else {
                                Intent _intent = new Intent(activity,AdWebViewActivity.class);
                                _intent.putExtra("targetUrl", url);
                                _intent.putExtra("activityType", 0);
                                startActivity(_intent);
                            }
                            return true;
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);

                        }

                        @Override
                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            super.onReceivedError(view, errorCode, description, failingUrl);
                            isLoadFinish = false;
                            if(isAdded()) {
                                String errorHtml = "<html><body><div style=\"position:relative;height:" +
                                        "100%; width:100%; display:table; *position:absolute; *top:50%; " +
                                        "*left:0;\"><p style=\"font-size:50px;position:absolute; top:40%; left:0; text" +
                                        "-align:center; width:100%; *top:0;\">" + getString(R.string.adview_net_error) + "</p>" +
                                        "</div></body></html>";
                                webcontent.loadDataWithBaseURL("about:blank", errorHtml, "text/html", "UTF-8", null);
                            }
                        }
                    }, new WebChromeClient() {
                        public void onReceivedTitle(WebView view, String title) {
                            super.onReceivedTitle(view, title);
                        }
                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            super.onProgressChanged(view, newProgress);
                            Log.i("cui","newProgress=="+newProgress);
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

                        }

                        @Override
                        public void backClose() {

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
            WebConfig.configureNormalWebViewByAD(getActivity(), webcontent, new MyWebViewClient()
                    , new WebChromeClient() {
                        public void onReceivedTitle(WebView view, String title) {
                            super.onReceivedTitle(view, title);
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

                        }

                        @Override
                        public void backClose() {

                        }
                    });
        }
        if(isLoadFinish&&!isLoadUrl){
            if(myApp!=null){
                String postData = "requestData=" + myApp.getAppAdReqJsonString("0",null);
                if(webcontent!=null){
                    webcontent.postUrl(targetUrl, postData.getBytes());
                    isLoadUrl=true;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dealJavascriptLeak() {
        webcontent.removeJavascriptInterface("searchBoxJavaBridge_");
        webcontent.removeJavascriptInterface("accessibility");
        webcontent.removeJavascriptInterface("accessibilityTraversal");
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            isLoadFinish = false;
            if(isAdded()) {
                String errorHtml = "<html><body><div style=\"position:relative;height:" +
                        "100%; width:100%; display:table; *position:absolute; *top:50%; " +
                        "*left:0;\"><p style=\"font-size:50px;position:absolute; top:40%; left:0; text" +
                        "-align:center; width:100%; *top:0;\">" + getString(R.string.adview_net_error) + "</p>" +
                        "</div></body></html>";
                webcontent.loadDataWithBaseURL("about:blank", errorHtml, "text/html", "UTF-8", null);
            }
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
            LogUtil.i("webView is not Miui!");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
            }else{
                if(url.equals("http://miwifi.com/diagnosis/index.html")){
                    view.loadUrl(url);
                }else {
                    Intent _intent = new Intent(activity,AdWebViewActivity.class);
                    _intent.putExtra("targetUrl", url);
                    _intent.putExtra("activityType", 0);
                    startActivity(_intent);
                }
            }
            return true;
        }
    }

    private boolean isLoadUrl;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint() && !isLoadFinish) {
            isLoadFinish = true;
            if(myApp!=null){
                String postData = "requestData=" + myApp.getAppAdReqJsonString("0",null);
                if(webcontent!=null){
                    webcontent.postUrl(targetUrl, postData.getBytes());
                    isLoadUrl=true;
                }
            }
        }

        if(isVisibleToUser && myApp != null){
            Log.e("visible","adwebviewfragment");
            WatchData focusWatch=myApp.getCurUser().getFocusWatch();
        }
    }

    private void initListener() {
        mSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoadFinish = true;
                String postData = "requestData=" + myApp.getAppAdReqJsonString("0",null);
                webcontent.postUrl(targetUrl, postData.getBytes());
                mSwipeRefreshView.setRefreshing(false);
            }
        });
        mSwipeRefreshView.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return webcontent.getScrollY()>0;
            }
        });
    }
}
