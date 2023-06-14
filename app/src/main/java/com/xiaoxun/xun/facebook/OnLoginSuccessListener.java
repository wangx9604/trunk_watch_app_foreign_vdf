package com.xiaoxun.xun.facebook;

import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

public interface OnLoginSuccessListener {
    void OnSuccess(LoginResult result);

    void onCancel();

    void onError(FacebookException error);
}
