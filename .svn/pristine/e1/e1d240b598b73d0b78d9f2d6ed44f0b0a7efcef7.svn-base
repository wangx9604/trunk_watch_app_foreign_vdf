package com.xiaoxun.xun.securityarea.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.securityarea.adapter.AreaMapAdapter;
import com.xiaoxun.xun.securityarea.bean.EfencesAreaBean;
import com.xiaoxun.xun.securityarea.service.SecurityService;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.PermissionUtils;
import com.xiaoxun.xun.utils.StatusBarUtil;

import net.minidev.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DangerAreaActivity extends NormalActivity implements View.OnClickListener {
    private WatchData mCurWatch;
    private ImibabyApp myApp;

    private ImageButton mBtnBack;
    private TextView mTitle;
    private Button btSave;
    private ImageView ivTip;
    private TextView tvTipTitle;
    private TextView tvTip;
    private CardView cvTip;
    private ImageView ivTipVisible;
    private TextView tvTitleVisible;
    private TextView tvTipVisible;
    private RecyclerView rv;
    private AreaMapAdapter adapter;

    private String intentType;
    private String toDrawType;

    private List<EfencesAreaBean> mBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (ImibabyApp) getApplication();
        setContentView(R.layout.activity_danger_area);
//        StatusBarUtil.changeStatusBarColor(this, getResources().getColor(R.color.schedule_no_class));
        intentType = getIntent().getStringExtra(Constants.EXTRA_TYPE_ACTIVITY);
        if (intentType == null) {
            finish();
        }
        mCurWatch = getMyApp().getCurUser().getFocusWatch();
        initViews();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAreaList();
    }

    private void initViews() {
        mTitle = findViewById(R.id.tv_title);
        mBtnBack = findViewById(R.id.iv_title_back);
        btSave = findViewById(R.id.btn_save);
        rv = findViewById(R.id.rv_area);
        adapter = new AreaMapAdapter(mBeanList, this, mCurWatch.getEid(), myApp);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        View rvHead = getLayoutInflater().inflate(R.layout.adapter_danger_area_head, (ViewGroup) rv.getParent(), false);
        ivTip = rvHead.findViewById(R.id.iv_tip);
        tvTipTitle = rvHead.findViewById(R.id.tv_tip_title);
        tvTip = rvHead.findViewById(R.id.tv_tip);
        cvTip = rvHead.findViewById(R.id.cv_tip);
        ivTipVisible = rvHead.findViewById(R.id.iv_tip_visible);
        tvTitleVisible = rvHead.findViewById(R.id.tv_title_visible);
        tvTipVisible = rvHead.findViewById(R.id.tv_tip_visible);
        View rvFoot = getLayoutInflater().inflate(R.layout.adapter_danger_area_foot, (ViewGroup) rv.getParent(), false);
        adapter.addHeaderView(rvHead);
        adapter.addFooterView(rvFoot);

        btSave.setClickable(false);
    }

    private void initData() {
        if (intentType.equals(Constants.KEY_NAME_DANGER)) {
            mTitle.setText(R.string.danger_area);
            ivTip.setImageResource(R.drawable.danger_area);
            tvTipTitle.setText(R.string.danger_area);
            tvTip.setText(R.string.danger_area_set_danger_area_tip);
            btSave.setText(R.string.add_danger_area);
            ivTipVisible.setImageResource(R.drawable.icon_danger_visible);
            tvTitleVisible.setText(R.string.danger_area_danger_warning);
            tvTipVisible.setText(R.string.danger_area_danger_notice);
        } else if (intentType.equals(Constants.KEY_NAME_SAFE)) {
            mTitle.setText(R.string.safe_area);
            ivTip.setImageResource(R.drawable.safe_area);
            tvTipTitle.setText(R.string.safe_area);
            tvTip.setText(R.string.danger_area_set_safe_area_tip);
            btSave.setText(R.string.add_safe_area);
            ivTipVisible.setImageResource(R.drawable.icon_safe_visible);
            tvTitleVisible.setText(R.string.danger_area_safe_warning);
            tvTipVisible.setText(R.string.danger_area_safe_notice);
        }
    }

    private void initListener() {
        mBtnBack.setOnClickListener(this);
        btSave.setOnClickListener(this);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(DangerAreaActivity.this, MapDrawAreaActivity.class);
                intent.putExtra("drawType", intentType);
                intent.putExtra("efenceData", mBeanList.get(position));
                startActivity(intent);
            }
        });
    }

    //获取设置的安全/危险 区域
    private void getAreaList() {
        if (getMyApp().getNetService() != null) {
            SecurityService.getInstance(myApp).sendAreaGetMsg(mCurWatch.getEid(), new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    JSONObject mPl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
                    if (mPl != null) {
                        mBeanList.clear();
                        String data = String.valueOf(mPl.get(CloudBridgeUtil.KEY_GUARD_EFFENCES));
                        Type type = new TypeToken<ArrayList<EfencesAreaBean>>() {
                        }.getType();
                        List<EfencesAreaBean> list = new Gson().fromJson(data, type);
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getEtype().equals(intentType)) {
                                    mBeanList.add(list.get(i));
                                }
                            }
                        }
                        if (mBeanList != null && mBeanList.size() > 0) {
                            cvTip.setVisibility(View.GONE);
                            adapter.replaceData(mBeanList);
                        } else {
                            cvTip.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    btSave.setClickable(true);
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        if (v == mBtnBack) {
            finish();
        } else if (v == btSave) {
            if (!PermissionUtils.hasPermissions(this, PermissionUtils.storagePermissions)) {
                Toast.makeText(this, getString(R.string.no_storage_permission_tips), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mBeanList.size() >= 10) {
                if (intentType.equals(Constants.KEY_NAME_DANGER)) {
                    Toast.makeText(this, R.string.danger_area_danger_limit, Toast.LENGTH_SHORT).show();
                } else if (intentType.equals(Constants.KEY_NAME_SAFE)) {
                    Toast.makeText(this,R.string.danger_area_safe_limit, Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(this, MapDrawAreaActivity.class);
                if (intentType.equals(Constants.KEY_NAME_DANGER)) {
                    toDrawType = "dangerAdd";
                } else if (intentType.equals(Constants.KEY_NAME_SAFE)) {
                    toDrawType = "safeAdd";
                }
                intent.putExtra("drawType", toDrawType);
                startActivity(intent);
            }

        }
    }
}