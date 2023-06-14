package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;

/**
 * Created by huangyouyang on 2017/3/20.
 */

public class ErrorPromptActivity extends NormalActivity {

    private ImageButton btnBack;
    private TextView tvTitle;
    private TextView tvErrorTitle;
    private TextView tvErrorDesc;
    private String mDeviceType;
    private String mErrorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_prompt);

        initView();
        initData(getIntent());
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewShow();
        if ("bindfail".equals(mErrorType))
            jumpToApnConfig();
    }

    private void initView() {

        btnBack = findViewById(R.id.iv_title_back);
        tvTitle = findViewById(R.id.tv_title);
        tvErrorTitle = findViewById(R.id.tv_error_title);
        tvErrorDesc = findViewById(R.id.tv_error_desc);
    }

    private void initData(Intent intent) {

        mErrorType = intent.getStringExtra("type");
        mDeviceType = intent.getStringExtra("deviceType");
    }

    private void updateViewShow() {

        if ("bindfail".equals(mErrorType)) {
            tvTitle.setText(getString(R.string.device_bind_fail_title));
            tvErrorTitle.setText(getString(R.string.device_bind_fail_method));
            if (Const.DEVICE_TYPE_302.equals(mDeviceType) || Const.DEVICE_TYPE_303.equals(mDeviceType) || Const.DEVICE_TYPE_501.equals(mDeviceType))
                tvErrorDesc.setText(getString(R.string.device_bind_fail_method_desc_105) + "\n" + getString(R.string.device_bind_fail_method_desc_302_more));
            else
                tvErrorDesc.setText(getString(R.string.device_bind_fail_method_desc_105));
        } else if ("neterror".equals(mErrorType)) {
            tvTitle.setText(getString(R.string.network_error_prompt2));
            tvErrorTitle.setText(getString(R.string.network_error_title));
            tvErrorDesc.setText(getString(R.string.network_error_desc));
        }
    }

    private void initListener() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorPromptActivity.this.finish();
            }
        });
    }

    private void jumpToApnConfig(){
        String content = getString(R.string.device_bind_fail_method_desc_105) + "\n" + getString(R.string.device_bind_fail_method_desc_302_more);
        SpannableStringBuilder builder1 = new SpannableStringBuilder(content);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(ErrorPromptActivity.this, APNConfigActivity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.bg_color_orange));
            }
        };
        int start = content.indexOf(getString(R.string.apnconfig));
        builder1.setSpan(clickableSpan1, start, start + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvErrorDesc.setText(builder1);
        tvErrorDesc.setClickable(true);
        tvErrorDesc.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
