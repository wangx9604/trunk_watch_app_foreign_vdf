package com.xiaoxun.xun.utils.alipayLoginUtil;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author cuiyufeng
 * @Description: AuthTokenBean
 * @date 2018/11/23 21:33
 */
public class AuthTokenBean {
    private AuthToken alipay_system_oauth_token_response;
    private String sign;

    public AuthToken getAlipay_system_oauth_token_response() {
        return alipay_system_oauth_token_response;
    }

    public void setAlipay_system_oauth_token_response(AuthToken alipay_system_oauth_token_response) {
        this.alipay_system_oauth_token_response = alipay_system_oauth_token_response;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public class AuthToken{
        private String access_token;
        private String alipay_user_id;
        private String expires_in;
        private String refresh_token;
        private String user_id;

        //错误
        private String code;
        private String msg;
        private String sub_code;
        private String sub_msg;


        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getAlipay_user_id() {
            return alipay_user_id;
        }

        public void setAlipay_user_id(String alipay_user_id) {
            this.alipay_user_id = alipay_user_id;
        }

        public String getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(String expires_in) {
            this.expires_in = expires_in;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getSub_code() {
            return sub_code;
        }

        public void setSub_code(String sub_code) {
            this.sub_code = sub_code;
        }

        public String getSub_msg() {
            return sub_msg;
        }

        public void setSub_msg(String sub_msg) {
            this.sub_msg = sub_msg;
        }
    }

}
