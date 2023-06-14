/**
 * Creation Date:2015-2-3
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.CloudBridgeUtil;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-2-3
 */
public class BindNewActivity extends NormalActivity {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private ImageView mBindCode;
    private ImageView mBindNumber;
    private BroadcastReceiver mReceiver;
    private ImageButton ivTitleBack;
    private TextView tvTitle;
    private ImageButton mBtnHelpWeb;

    public static final int Request_Code_Bind = 2001;
    public static final int Result_Code_Bind = 2002;
    String goBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_guide);
        setTintColor(getResources().getColor(R.color.welcome_bg_color));
        goBind = getIntent().getStringExtra(NewLoginActivity.GOBIND);
        initReceivers();

        mBindCode = findViewById(R.id.iv_bind_code);
        mBindCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(BindNewActivity.this, Manifest.permission.CAMERA) == PERMISSION_GRANTED){
                    Intent intent = new Intent();
                    intent.putExtra(MipcaActivityCapture.SCAN_TYPE, "bind");
                    intent.setClass(BindNewActivity.this, MipcaActivityCapture.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                }else {
                    ActivityCompat.requestPermissions(BindNewActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
            }
        });
        myApp.setBindAutoLogin(true);
        mBindNumber = findViewById(R.id.iv_bind_num);
        mBindNumber.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(BindNewActivity.this, BindInputImsiActivity.class);
                startActivity(intent);
            }
        });

        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setVisibility(View.GONE);
        ivTitleBack = findViewById(R.id.iv_title_back);
        ivTitleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(goBind)){
                    //Intent intent = new Intent(BindNewActivity.this, NewMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent intent = new Intent(BindNewActivity.this, NewMainActivity.class);
                    startActivity(intent);
                }else{
                    setResult(Result_Code_Bind);
                }
                finish();
            }
        });

        //绑定页用户帮助按钮先关掉
        mBtnHelpWeb = findViewById(R.id.ib_help_web);
        mBtnHelpWeb.setVisibility(View.GONE);
        mBtnHelpWeb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(myApp.getHelpCenterIntent(BindNewActivity.this, "bindWatch"));
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(1 == requestCode && grantResults.length == 1){
            if(grantResults[0] == PERMISSION_GRANTED){
                Intent intent = new Intent();
                intent.putExtra(MipcaActivityCapture.SCAN_TYPE, "bind");
                intent.setClass(BindNewActivity.this, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            }else {
                Toast.makeText(BindNewActivity.this,getString(R.string.camera_premission_tips),Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initReceivers() {
        // TODO Auto-generated method stub
        mReceiver = new BroadcastReceiver() {
            //wifi状态广播接收，连接变化，扫描结果等
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Const.ACTION_BIND_RESULT_END)) {
                    finish();
                } else {

                }
            }

        };

        IntentFilter baseFilter = new IntentFilter();
        baseFilter.addAction(Const.ACTION_BIND_RESULT_END);
        registerReceiver(mReceiver, baseFilter);
    }

    private void clearReceivers() {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
       /* if (myApp.getCurUser().getWatchList() == null || myApp.getCurUser().getWatchList().size() == 0) {
            myApp.doLogout("bind not end quit login");
        }*/
       finish();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        clearReceivers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();

                    String sn = bundle.getString("result");
                    if (sn != null) {
                        if (getMyApp().getCurUser().isWatchSNBinded(sn)) {
                            Intent intent = new Intent();
                            intent.setClass(BindNewActivity.this, BindResultActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(Const.KEY_RESULT_CODE, 0);
                            intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_binded));
                            startActivity(intent);
                        } else {
                            //set wati step;
                            Intent intent = new Intent();
                            intent.setClass(BindNewActivity.this, BindResultActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(Const.KEY_RESULT_CODE, 1);
                            intent.putExtra(CloudBridgeUtil.KEY_NAME_SERINALNO, sn);
                            intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_req_send));
                            startActivity(intent);
                            // }
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(BindNewActivity.this, BindResultActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        intent.putExtra(Const.KEY_RESULT_CODE, 0);
                        intent.putExtra(Const.KEY_MSG_CONTENT, getText(R.string.bind_result_wrong));
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!TextUtils.isEmpty(goBind)) {
                //Intent intent = new Intent(BindNewActivity.this, NewMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent intent = new Intent(BindNewActivity.this, NewMainActivity.class);
                startActivity(intent);
            }else{
                setResult(Result_Code_Bind);
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
