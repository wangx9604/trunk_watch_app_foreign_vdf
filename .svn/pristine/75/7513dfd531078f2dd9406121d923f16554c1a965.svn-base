/**
 * Creation Date:2015-2-5
 * 
 * Copyright 
 */
package com.xiaoxun.xun.activitys;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.MD5;

import java.util.ArrayList;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-2-5
 * 
 */
public class DeviceQrActivity extends NormalActivity {

    private ImageButton mBtnBack;
    private ImageView ivQr;
    private TextView mTvImei;
    private TextView mTvSn;
    private TextView tvAdminAccount;

    private String imei;
    private String machSn;
    private WatchData watch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_qr);


        String eid = getIntent().getExtras().getString(Const.KEY_WATCH_ID);
        watch = myApp.getCurUser().queryWatchDataByEid(eid);
        if (watch.isWatch()) {
            ((TextView) findViewById(R.id.tv_title)).setText(R.string.watch_qr);
        } else {
            ((TextView) findViewById(R.id.tv_title)).setText(R.string.device_qr);
        }
        initView();
        initListener();
        refrushView();
    }

    private void initView() {

        mBtnBack = findViewById(R.id.iv_title_back);
        ivQr = findViewById(R.id.iv_device_qr);
        mTvImei = findViewById(R.id.tv_imei);
        mTvSn = findViewById(R.id.tv_machsn);
        tvAdminAccount = findViewById(R.id.tv_admin_account);
    }

    private void refrushView() {

        String qrstr;
        if (watch.isDevice102()) {
            String md5str = MD5.md5_string(watch.getWatchId()).substring(8, 24).toLowerCase();
            qrstr = Const.NEW_QR_START+Const.SN_TYPE_ICCID+md5str;
        }else{
            qrstr = watch.getQrStr();
        }
        ivQr.setImageBitmap(ImageUtil.createQRImage(qrstr));

        imei = watch.getImei();
        if (imei!=null&&imei.length()>0) {
            mTvImei.setText(getString(R.string.device_tv_imei, imei));
        }else{
            mTvImei.setVisibility(View.GONE);
        }

        machSn = watch.getMachSn();
        if (machSn!=null &&machSn.length()>0){
            mTvSn.setText(getString(R.string.device_tv_machsn, machSn));
        }else{
            mTvSn.setVisibility(View.GONE);
        }
        if(watch.isDevice707_H01()|| watch.isDevice709_H01()){
            mTvSn.setVisibility(View.GONE);
        }

        String admineid = myApp.getCurUser().getAdminEidByWatch(myApp.getCurUser().getFocusWatch());
        ArrayList<MemberUserData> list = myApp.getCurUser().getUserDataByFamily(myApp.getCurUser().getFocusWatch().getFamilyId());
        String xiaomiid = null;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getEid().equals(admineid)){
                xiaomiid = list.get(i).getXiaomiId();
                break;
            }
        }

        if(xiaomiid != null && xiaomiid.length() > 1){

        }else{
            tvAdminAccount.setVisibility(View.GONE);
        }
        //将管理员小米账号隐去
        tvAdminAccount.setVisibility(View.GONE);
    }

    private void initListener() {

        mBtnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceQrActivity.this.finish();
            }
        });
    }
}
