package com.telecom.websdk;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class LoginWebViewClient extends WebViewClient{
    private static final String TAG = LoginWebViewClient.class.getSimpleName();

    public static final String XIAOMI_ACCOUNT_TYPE = "com.xiaomi";

    private static final int DELAY_TIME = 200;

    private enum LoginState {
        LOAD_ING, // 正在加载
        LOGIN_ING, // 正在登陆
        LOGIN_FINISHING, // 登陆结束中
        LOGIN_FINISHED, // 完成登陆
    }

    private LoginState mLoginState = LoginState.LOAD_ING;

    private LoginProgressInterface mLoginProgressInterface;

    private static Activity getActivity(View view){
        Activity activity = null;
        if (view.getContext().getClass().getName().contains("com.android.internal.policy.DecorContext")){
            Field field = null;
            try {
                field = view.getContext().getClass().getDeclaredField("mPhoneWindow");
                field.setAccessible(true);
                Object obj = field.get(view.getContext());
                java.lang.reflect.Method m1 = obj.getClass().getMethod("getContext");
                activity = (Activity) (m1.invoke(obj));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else{
            activity = (Activity) view.getContext();
        }

        return activity;
    }

    public void setLoginProgressInterface(LoginProgressInterface loginProgressInterface) {
        mLoginProgressInterface = loginProgressInterface;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.d(TAG, "onPageStarted: " + url + " " + mLoginState);
        if (mLoginState == LoginState.LOGIN_ING) {
            mLoginState = LoginState.LOGIN_FINISHING;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d(TAG, "onPageFinished: " + url + " " + mLoginState);
        if (mLoginState == LoginState.LOGIN_FINISHING) {
            mLoginState = LoginState.LOGIN_FINISHED;

            if (mLoginProgressInterface != null) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoginProgressInterface.hideLoginProgress();
                    }
                }, DELAY_TIME);
            }
        }
        WebConfig.isHomepageGoExit(url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        Log.d(TAG, "onReceivedError: " );
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        super.onTooManyRedirects(view, cancelMsg, continueMsg);
        Log.d(TAG, "onTooMany");
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
        Log.d(TAG, "on received http");
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        Log.d(TAG, "onLoadResource");
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        super.onFormResubmission(view, dontResend, resend);
        Log.d(TAG, "onFormResubmission(");
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        Log.d(TAG, "onReceivedSsl");
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        super.onUnhandledKeyEvent(view, event);
        Log.d(TAG, "onUnhandledKeyEvent");
    }

    @Override
    public void onReceivedLoginRequest(final WebView view, String realm, String accountName, String args) {
        Log.i(TAG, "onReceivedLoginRequest");
        final Activity context = getActivity(view.getRootView());//(Activity) view.getRootView().getContext()
        if (TextUtils.equals(realm, XIAOMI_ACCOUNT_TYPE)) {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccountsByType(XIAOMI_ACCOUNT_TYPE);

            if (accounts.length == 0) {
                return;
            }

            Account account = null;

            for (Account ac : accounts) {
                if (ac.name.equals(accountName)) {
                    account = ac;
                    break;
                }
            }

            if (account == null) {
                account = accounts[0];
            }

            String tokenType = "weblogin:" + args;
            AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    Log.d(TAG, "callback run");
                    String result = null;
                    try {
                        result = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                    } catch (OperationCanceledException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AuthenticatorException e) {
                        e.printStackTrace();
                    }
                    if (result == null) {
                        Log.d(TAG, "web sso failed.");
                        if (mLoginProgressInterface != null) {
                            mLoginProgressInterface.hideLoginProgress();
                        }
                    } else {
                        Log.d(TAG, "web sso succeed.");
                        view.loadUrl(result);
                    }
                }
            };
            am.getAuthToken(account, tokenType, null, context, callback, null);

            mLoginState = LoginState.LOGIN_ING;
            if (mLoginProgressInterface != null) {
                mLoginProgressInterface.showLoginProgress();
            }
        }
    }
}
