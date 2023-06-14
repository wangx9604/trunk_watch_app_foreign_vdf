package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONValue;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AgreementAndPrivacyActivity extends NormalActivity implements View.OnClickListener {

    private TextView agreementAndPrivacyView;
    private Button disagreeButton;
    private Button agreeButton;
    private TextView titleView;
    private ImageView backView;
    private String qrCodeString;
    private String privacyUrl = Const.DEFAULT_PRIVACY_URL;
    private String agreementUrl = Const.DEFAULT_AGREEMENT_URL;
    private String deviceType;
    private int isBind = 1;
    private String unikey;

    private boolean hasSend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_and_privacy);
        qrCodeString = getIntent().getStringExtra("qrcode");
        initViews();
        initDatas();
    }

    private void initViews() {
        titleView = (TextView) findViewById(R.id.tv_title);
        titleView.setText(R.string.reminder);
        backView = (ImageView) findViewById(R.id.iv_back);
        backView.setOnClickListener(this);
        agreementAndPrivacyView = (TextView) findViewById(R.id.tv_agreement_and_privacy);
        disagreeButton = (Button) findViewById(R.id.btn_disagree);
        disagreeButton.setOnClickListener(this);
        agreeButton = (Button) findViewById(R.id.btn_agree);
        agreeButton.setOnClickListener(this);
    }

    private void initDatas() {
        getAgreementAndPrivacyUrl();
        showAgreementAndPrivacy();
    }

    private void showAgreementAndPrivacy() {
        String agreement = "《" + getString(R.string.agreement) + "》";
        String privacy = "《" + getString(R.string.privacy) + "》";
        String content = getString(R.string.agreement_and_privacy);
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ClickableSpan agreementSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(AgreementAndPrivacyActivity.this, HelpWebActivity.class);
                intent.putExtra(Const.KEY_WEB_TYPE, Constants.KEY_WEB_TYPE_AGREEMENT);
                intent.putExtra(Const.KEY_HELP_URL, agreementUrl);
                AgreementAndPrivacyActivity.this.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.hyperlink));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        };

        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(AgreementAndPrivacyActivity.this, HelpWebActivity.class);
                intent.putExtra(Const.KEY_WEB_TYPE, Constants.KEY_WEB_TYPE_PRIVACY_POLICY);
                intent.putExtra(Const.KEY_HELP_URL, privacyUrl);
                AgreementAndPrivacyActivity.this.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.hyperlink));
                ds.setUnderlineText(false);
                ds.clearShadowLayer();
            }
        };

        int start = content.indexOf(agreement);
        if (start >= 0) {
            builder.setSpan(agreementSpan, start, start + agreement.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        start = content.indexOf(privacy);
        if (start >= 0) {
            builder.setSpan(privacySpan, start, start + privacy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        agreementAndPrivacyView.setText(builder);
        agreementAndPrivacyView.setClickable(true);
        agreementAndPrivacyView.setMovementMethod(LinkMovementMethod.getInstance());
        agreementAndPrivacyView.setHighlightColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void onClick(View v) {
        if (v == disagreeButton) {
            finish();
        } else if (v == agreeButton) {
            sendAgreeMessage();
        } else if (v == backView) {
            finish();
        }
    }

    private void sendBindRequest() {
        Intent intent = new Intent();
        intent.setClass(AgreementAndPrivacyActivity.this, BindResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Const.KEY_RESULT_CODE, 1);
        intent.putExtra(CloudBridgeUtil.KEY_NAME_SERINALNO, qrCodeString);
        intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_req_send));
        startActivity(intent);
        finish();
    }

    private void showBindHelp() {
        Intent intent = new Intent(AgreementAndPrivacyActivity.this, BindHelpActivity.class);
        intent.putExtra("qrcode", qrCodeString);
        startActivity(intent);
        finish();
    }

    private void getAgreementAndPrivacyUrl() {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject json = new JSONObject();
            json.put("packageName", getPackageName());
            json.put("qrUrl", qrCodeString);
            json.put("language", Locale.getDefault().getLanguage());
            json.put("countryCode", Locale.getDefault().getCountry());
            RequestBody requestBody = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder().url(FunctionUrl.GET_AGREEMENT_AND_PRIVACY_URL).post(requestBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    handleUrlResult(result);
                }
            });
        } catch (Exception e) {

        }
    }

    private void handleUrlResult(String result) {
        try {
            JSONObject resutlJson = new JSONObject(result);
            int rc = resutlJson.getInt("RC");
            if (rc == 1) {
                JSONObject pl = resutlJson.getJSONObject("PL");
                privacyUrl = pl.getString("privacyUrl");
                agreementUrl = pl.getString("agreementUrl");
                deviceType = pl.getString("deviceType");
                isBind = pl.getInt("isbound");
                unikey = pl.getString("unikey");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAgreementAndPrivacy();
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    public void sendAgreeMessage() {
        if(!hasSend) {
            hasSend = true;
        }else{
            return;
        }
        try {
            OkHttpClient client = new OkHttpClient();
            String url = FunctionUrl.USER_PRIVACY_AGREE_URL;
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject reqJson = new JSONObject();
            reqJson.put("uid", myApp.getCurUser().getEid());
            reqJson.put("unikey", unikey);
            String reqBody = Base64.encodeToString(AESUtil.encryptAESCBC(reqJson.toString(), myApp.getNetService().getAESKEY(), myApp.getNetService().getAESKEY()), Base64.NO_WRAP) + myApp.getToken();
            RequestBody body = RequestBody.create(JSON, reqBody);

            final Request request = new Request.Builder().url(url).post(body).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    hasSend = false;
                    AgreementAndPrivacyActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(AgreementAndPrivacyActivity.this, getString(R.string.network_error_prompt));
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String results = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("SW206_A02".equals(deviceType) && isBind == 0) {
                                showBindHelp();
                            } else {
                                sendBindRequest();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {

        }
    }
}
