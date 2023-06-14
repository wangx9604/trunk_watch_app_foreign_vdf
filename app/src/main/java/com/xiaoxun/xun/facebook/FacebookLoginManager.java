package com.xiaoxun.xun.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;

public class FacebookLoginManager {

    private static CallbackManager mFaceBookCallBack;
    private volatile static FacebookLoginManager sInstance;

    private FacebookLoginManager() {

    }

    /**
     * 单例
     *
     * @return
     */
    public static FacebookLoginManager getInstance() {
        if (sInstance == null) {
            synchronized (FacebookLoginManager.class) {
                if (sInstance == null) {
                    sInstance = new FacebookLoginManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 开始
     *
     * @param context
     */
    public void faceBookLogin(Context context) {
        LoginManager.getInstance()
                .logInWithReadPermissions((Activity) context,
                        Arrays.asList("public_profile"));
    }

    public void facebookKeyHash(final Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.xiaoxun.xunoversea", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }


    /**
     * 初始化
     */
    public void initFaceBook(final Context context,
                             final OnLoginSuccessListener listener) {
        mFaceBookCallBack = CallbackManager.Factory.create();
        if (mFaceBookCallBack != null) {
            LoginManager.getInstance().registerCallback(mFaceBookCallBack,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e("LoginResult: onSuccess ", loginResult.toString());
                            if (loginResult != null) {
                                if (listener != null) {
                                    listener.OnSuccess(loginResult);
                                }

                            }

                        }

                        @Override
                        public void onCancel() {
                            Log.e("LoginResult: onCancel ", "onCancel");
                            if (listener != null) {
                                listener.onCancel();
                            }
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.e("LoginResult: onError ", error.toString());
                            if (listener != null) {
                                listener.onError(error);
                            }
                        }
                    });
        }
    }


    /**
     * 设置登录结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    public void setOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFaceBookCallBack != null) {
            mFaceBookCallBack.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void getFacebookInfo(AccessToken accessToken, GraphRequest.GraphJSONObjectCallback callback) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, callback);
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void onDestroy() {
        LoginManager.getInstance().logOut();
    }
}