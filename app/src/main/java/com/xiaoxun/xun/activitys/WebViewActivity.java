/**
 * Creation Date:2015-4-8
 * 
 * Copyright 
 */
package com.xiaoxun.xun.activitys;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-4-8
 * 
 */
public class WebViewActivity extends NormalActivity implements OnClickListener{
    private WebView mWebView;
    private String url = null;
    private String title = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        url = getIntent().getExtras().getString(Const.KEY_WEBVIEW_URL);
        title =getIntent().getExtras().getString(Const.KEY_WEBVIEW_TITLE);
        setContentView(R.layout.activity_faq);
        ((TextView)findViewById(R.id.tv_title)).setText(title);          

        mWebView = findViewById(R.id.faqwebview);
        //设置WebView属性，能够执行Javascript脚本  
        mWebView.getSettings().setJavaScriptEnabled(true);  
        
        //加载支付网页  
        mWebView.loadUrl(url); 
    
        
        //设置Web视图  
        mWebView.setWebViewClient(new MyWebViewClient());        
    }
    private class MyWebViewClient extends WebViewClient {  
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);  
            return true;  
        }  
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
         if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {  
                mWebView.goBack(); //goBack()表示返回WebView的上一页面  
                return true;  
            }  
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.iv_title_back:
            if(mWebView.canGoBack())
            {
                mWebView.goBack();
            }else{
                finish();
            }
            break;

        default:
            break;
        }
    }    
}
