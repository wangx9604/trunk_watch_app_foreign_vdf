package com.xiaoxun.xun.webview;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.OVERSEAURL;
import com.xiaoxun.xun.R;

import com.xiaoxun.xun.activitys.NewMainActivity;

import com.xiaoxun.xun.motion.activity.MotionSportRecordActivity;
import com.xiaoxun.xun.region.XunKidsDomain;
import com.xiaoxun.xun.utils.CommonUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.utils.WebViewUtil;

public class MultFunWebViewClient extends WebViewClient {
    private Context mContext;
    private ImibabyApp myApp;
    private String mFilterUrl = FunctionUrl.KEY_STORE_MAIN_URL;
    private String mChannel;

    public MultFunWebViewClient(Context context, ImibabyApp mApp, String Channel){
        mContext = context;
        myApp = mApp;
        mChannel = Channel;
        if(myApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0){
            mFilterUrl = FunctionUrl.KEY_STORE_MAIN_URL_TEST;
        } else {
            mFilterUrl = FunctionUrl.KEY_STORE_MAIN_URL;
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String scheme = request.getUrl().getScheme();
        String url = request.getUrl().toString();
        final Uri uri = Uri.parse(url);
        LogUtil.e("MultFunWebClient scheme request: " + scheme+":"+request.getUrl().toString());
        if ("mqqwpa".equals(scheme) || "alipays".equals(scheme)|| "weixin".equals(scheme)) {
            return CommonUtil.openURIView(mContext, request.getUrl(), scheme);
        }else if("xunshopshare".equals(scheme)){
            DialogUtil.shareDialog(mContext, url);
            return  true;
        }else if("xiaoxunapp".equals(scheme)){
            if(request.getUrl().getAuthority().equals("open")){
                //该链接直接打开url
                int indexOf= url.indexOf("?url=");
                String toUrl = url.substring(indexOf+5);

                boolean isTestPlatfrom = myApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0;
                String Channel = WebViewUtil.onGetChannalByUrl(toUrl, isTestPlatfrom, mChannel);
                WebViewUtil.onNextPage(mContext, toUrl, Channel);
            }

            if(request.getUrl().getHost().equals("opennative")){
                String eid = request.getUrl().getQueryParameter("eid");
                String path = request.getUrl().getPath();
                toNativeActivity(path,eid);
            }

            return true;
        }else if(url.contains(Constants.to_Url)){
            String str=url.substring(21);
            Log.i("CUI","url2:="+str);
            //登录
            Intent intent=new Intent(mContext, myApp.getLoginClass());
            intent.putExtra(NewMainActivity.isShowBack,"true");
            intent.putExtra(Constants.store_url,str);
            intent.putExtra(Constants.login_channel, mChannel);
            mContext.startActivity(intent);
            return  true;
        }

        //商城连接为重定向连接，会重定向到该链接地址，该处做下处理
        if (request.getUrl().toString().contains(mFilterUrl)) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        if (!url.startsWith("http") && !url.startsWith("https")) {
            Dialog dlg = DialogUtil.CustomNormalDialog(mContext,
                    mContext.getString(R.string.open_thire_app_info), null, new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    }, mContext.getString(R.string.cancel), new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }catch (Exception e){
                                ToastUtil.showMyToast(mContext,
                                        mContext.getString(R.string.open_thire_app_error),
                                        Toast.LENGTH_SHORT);
                                e.printStackTrace();
                            }
                        }
                    }, mContext.getString(R.string.confirm));
            try {
                dlg.show();
            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }else {
            boolean isTestPlatfrom = myApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0;
            String Channel = WebViewUtil.onGetChannalByUrl(request.getUrl().toString(), isTestPlatfrom, mChannel);
            if(Channel.equals(mChannel)){
                return super.shouldOverrideUrlLoading(view, url);
            }else {
                WebViewUtil.onNextPage(mContext, request.getUrl().toString(), Channel);
            }
        }
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        LogUtil.e("MultFunWebClient scheme url: " + scheme+":"+url);

        if ("mqqwpa".equals(scheme) || "alipays".equals(scheme)|| "weixin".equals(scheme)) {
            return CommonUtil.openURIView(mContext, uri, scheme);
        }else if("xunshopshare".equals(scheme)){
            DialogUtil.shareDialog(mContext, url);
            return  true;
        }else if("xiaoxunapp".equals(scheme)){
            if(uri.getAuthority().equals("open")){
                //该链接直接打开url
                int indexOf= url.indexOf("?url=");
                String toUrl = url.substring(indexOf+5);
                boolean isTestPlatfrom = myApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0;
                String Channel = WebViewUtil.onGetChannalByUrl(toUrl, isTestPlatfrom,mChannel);
                WebViewUtil.onNextPage(mContext, toUrl, Channel);
            }
            if(uri.getHost().equals("opennative")){
                String eid = uri.getQueryParameter("eid");
                String path = uri.getPath();
                toNativeActivity(path,eid);
            }
            return true;
        }else if(url.contains(Constants.to_Url)){
            String str=url.substring(21);
            Log.i("CUI","url2:="+str);
            //登录
            Intent intent=new Intent(mContext, myApp.getLoginClass());
            intent.putExtra(NewMainActivity.isShowBack,"true");
            intent.putExtra(Constants.store_url,str);
            intent.putExtra(Constants.login_channel,"yuermain");
            mContext.startActivity(intent);
            return  true;
        }

        //商城连接为重定向连接，会重定向到该链接地址，该处做下处理
        if (uri.toString().contains(mFilterUrl)) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        if (!url.startsWith("http") && !url.startsWith("https")) {
            Dialog dlg = DialogUtil.CustomNormalDialog(mContext,
                    mContext.getString(R.string.open_thire_app_info), null, new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {

                        }
                    }, mContext.getString(R.string.cancel), new DialogUtil.OnCustomDialogListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }catch (Exception e){
                                ToastUtil.showMyToast(mContext,
                                        mContext.getString(R.string.open_thire_app_error),
                                        Toast.LENGTH_SHORT);
                                e.printStackTrace();
                            }
                        }
                    }, mContext.getString(R.string.confirm));
            try {
                dlg.show();
            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }else {
            boolean isTestPlatfrom = myApp.getIntValue(Const.SHARE_PREF_FIELD_DEV_SERVER_FLAG, 0) > 0;
            String Channel = WebViewUtil.onGetChannalByUrl(url, isTestPlatfrom,mChannel);
            if(Channel.equals(mChannel)){
                return super.shouldOverrideUrlLoading(view, url);
            }else {
                WebViewUtil.onNextPage(mContext, url, Channel);
            }
        }
        return true;
    }

    private void toNativeActivity(String path,String eid){
        Intent it = new Intent();
        if(path.contains("sportcircle")){

        }else if(path.contains("sportrecord")){
            it.setClass(mContext, MotionSportRecordActivity.class);
        }
        it.putExtra("eid",eid);
        mContext.startActivity(it);
    }
}
