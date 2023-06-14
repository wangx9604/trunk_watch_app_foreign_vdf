package com.xiaoxun.xun.region;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.FunctionUrl;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.HelpWebActivity;
import com.xiaoxun.xun.activitys.NewLoginActivity;
import com.xiaoxun.xun.activitys.NewWelcomeActivity;
import com.xiaoxun.xun.region.bean.RegionBean;
import com.xiaoxun.xun.utils.RegionSelectUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

public class PrivacyAuthDialog extends Dialog {

    public PrivacyAuthDialog(@NonNull Context context) {
        super(context);
        Activity activity = (Activity) context;
        ImibabyApp myApp = (ImibabyApp) activity.getApplication();
        setContentView(R.layout.dialog_privacy_auth);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_disagree = findViewById(R.id.btn_disagree);
        btn_disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        Button btn_agree = findViewById(R.id.btn_agree);
        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApp.getIntValue(Constants.SHARE_PREF_FIELD_PRIVACY_AGREE, 0) == 0) {
                    ToastUtil.show(activity, activity.getString(R.string.privacy_prompt_toast_desc));
                    return;
                }
                myApp.setValue(Const.SHARE_PREF_PRIVACY_POLICY_AGREED, true);
                Intent it = new Intent(activity, RegionSelectActivity.class);
                it.putExtra("entry_type", 0);
                activity.startActivity(it);
//                RegionBean bean = new RegionBean();
//                bean.setRegionName("VI(S5,S6,S8,S88,Y2)");
//                bean.setOtaUrl("nupgrade.xunkids.com");
//                bean.setDialUrl("dial-shop.xunkids.com");
//                bean.setFileUrl("nfdsfile.xunkids.com");
//                bean.setSteps("steps.xunkids.com");
//                bean.setDcenter("dcenter.xunkids.com");
//                bean.setAppHostUrl("cmibro.xunkids.com:8555");
//                bean.setCou("couserver.xunkids.com");
//                bean.setRegionSimpleName(RegionSelectUtil.getNameFromSimple(myApp, bean.getRegionName()));
//                myApp.setValue("region", bean.getRegionSimpleName());
//                myApp.setValue(Constants.KEY_NAME_WATCH_SELECTED, bean.getRegionName());
//                //保存当前所选地区
//                myApp.setValue(
//                        Constants.KEY_NAME_COUNTRY_SELECTED,
//                        0
//                );
//                //设置服务器地址
//                XunKidsDomain.getInstance(myApp)
//                        .setDomainWebSocketAndHttpBaseUrl(bean, true);
//                Intent it = new Intent(
//                        activity,
//                        NewLoginActivity.class
//                );
//                activity.startActivity(it);
            }
        });

        ImageView btn_agreement_privacy = findViewById(R.id.btn_agreement_privacy);
        TextView tv_agreement_privacy = findViewById(R.id.tv_agreement_privacy);

        btn_agreement_privacy.setImageResource(myApp.getIntValue(Constants.SHARE_PREF_FIELD_PRIVACY_AGREE, 0) == 0 ?
                R.drawable.icon_agreement_privacy_unagree : R.drawable.icon_agreement_privacy_agree);

        String content = activity.getString(R.string.privacy_prompt_login_desc);
        SpannableString builder = new SpannableString(content);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showAgreementAndPrivacy("2", activity);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(activity.getResources().getColor(R.color.btn_agree_continue_color));
            }
        };
        String protectionPolicy = activity.getString(R.string.privacy_prompt_protection_policy);
        int start = content.indexOf(protectionPolicy);
        if (start > 0)
            builder.setSpan(clickableSpan1, start, start + protectionPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showAgreementAndPrivacy("1", activity);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(activity.getResources().getColor(R.color.btn_agree_continue_color));
            }
        };
        String userService = activity.getString(R.string.privacy_prompt_user_service);
        start = content.indexOf(userService);
        if (start > 0)
            builder.setSpan(clickableSpan2, start, start + userService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_agreement_privacy.setText(builder);
        tv_agreement_privacy.setHighlightColor(activity.getResources().getColor(R.color.transparent));
        tv_agreement_privacy.setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.ly_check_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myApp.getIntValue(Constants.SHARE_PREF_FIELD_PRIVACY_AGREE, 0) == 0) {
                    myApp.setValue(Constants.SHARE_PREF_FIELD_PRIVACY_AGREE, 1);
                    btn_agreement_privacy.setImageResource(R.drawable.icon_agreement_privacy_agree);
                } else {
                    myApp.setValue(Constants.SHARE_PREF_FIELD_PRIVACY_AGREE, 0);
                    btn_agreement_privacy.setImageResource(R.drawable.icon_agreement_privacy_unagree);
                }
            }
        });
        this.setCanceledOnTouchOutside(false);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        window.setAttributes(layoutParams);
    }

    private void showAgreementAndPrivacy(String type, Activity activity) {
        JSONObject params = new JSONObject();
        params.put("packageName", activity.getPackageName());
        params.put("type", type);
        Intent intent = new Intent(activity, HelpWebActivity.class);
        if ("1".equals(type)) {
            intent.putExtra(Const.KEY_WEB_TYPE, Constants.KEY_WEB_TYPE_AGREEMENT);
        } else {
            intent.putExtra(Const.KEY_WEB_TYPE, Constants.KEY_WEB_TYPE_PRIVACY_POLICY);
        }
        intent.putExtra(Const.KEY_HELP_URL,
                FunctionUrl.POST_AGREEMENT_AND_PRIVACY_URL
        );
        intent.putExtra(Const.KEY_PARAMS, params.toJSONString());
        activity.startActivity(intent);
    }


}
