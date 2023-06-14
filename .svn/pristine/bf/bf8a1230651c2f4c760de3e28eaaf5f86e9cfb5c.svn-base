package com.xiaoxun.xun.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchAppBean;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.AppStoreUtils;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.List;

public class AppDetailActivity extends NormalActivity {

    private ImageButton mBtnBack;
    private TextView mTitle;

    private TextView mTvAppName;
    private TextView mTvAppDetail;
    private Button mBtnDownloadApp;

    private RecyclerView mLayoutIconList;
    private String[] mIconList =new String[]{};
    private AppDetailActivity.WatchIconListAdapter mIconListAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private NetService mNetService;
    private String appId;
    private int status;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        initView();
        initData();
        updateStatus();
        initListener();
        reqAppDetail();
    }

    private void initView() {

        mBtnBack = findViewById(R.id.iv_title_back);
        mTitle = findViewById(R.id.tv_title);
        mLayoutIconList = findViewById(R.id.recyclerview_icon_list);
        mTvAppName = findViewById(R.id.tv_app_name);
        mTvAppDetail = findViewById(R.id.tv_app_detail);
        mBtnDownloadApp = findViewById(R.id.btn_download_app);

        mTitle.setText(R.string.app_detail);
    }

    private void updateStatus(){
        //0已安装，1待安装，2待更新，3待卸载  4有更新
        switch (status){
            case 0:
                // 显示已安装
                mBtnDownloadApp.setVisibility(View.GONE);
                break;
            case 1:
                mBtnDownloadApp.setText(getString(R.string.app_install_wait));
                mBtnDownloadApp.setVisibility(View.VISIBLE);
                break;
            case 2:
                mBtnDownloadApp.setText(getString(R.string.app_update_wait));
                mBtnDownloadApp.setVisibility(View.VISIBLE);
                break;
            case 3:
                mBtnDownloadApp.setVisibility(View.GONE);
                break;
            case 4:
                mBtnDownloadApp.setText(getString(R.string.app_update));
                mBtnDownloadApp.setVisibility(View.VISIBLE);
                break;
            default:
                mBtnDownloadApp.setText(getString(R.string.install));
                mBtnDownloadApp.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initData() {

        appId = getIntent().getStringExtra("app_id");
        status = getIntent().getIntExtra("status", 0);
        position = getIntent().getIntExtra("position", 0);
        mNetService = myApp.getNetService();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLayoutIconList.setLayoutManager(mLinearLayoutManager);
        mIconListAdapter = new WatchIconListAdapter(AppDetailActivity.this);
        mLayoutIconList.setAdapter(mIconListAdapter);
        mIconListAdapter.notifyDataSetChanged();
    }


    private void initListener() {

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDetailActivity.this.finish();
            }
        });

        mBtnDownloadApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.show(AppDetailActivity.this, getString(R.string.need_admin_auth));
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                AppDetailActivity.this.finish();
            }
        });
    }

    private void updateView(WatchAppBean.AppDetail appDetail) {

        mTvAppName.setText(appDetail.name);
        mTvAppDetail.setText(appDetail.function);
        mIconList = appDetail.page;
        mIconListAdapter.notifyDataSetChanged();
    }

    private void reqAppDetail() {

        AppStoreUtils.getInstance(AppDetailActivity.this).getAppDetail(appId, mNetService.AES_KEY, myApp.getToken(),
                new AppStoreUtils.OperationCallback() {
                    @Override
                    public void onSuccess(String result) {

                        JSONObject appDetailJson= (JSONObject) JSONValue.parse(result);
                        final WatchAppBean.AppDetail appDetail=WatchAppBean.toAppDeatilBean(appDetailJson);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateView(appDetail);
                            }
                        });
                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
    }

    class WatchIconListAdapter  extends RecyclerView.Adapter<WatchIconViewHolder>{

        Context context;
        WatchIconListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return mIconList.length;
        }

        @NonNull
        @Override
        public WatchIconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = View.inflate(context, R.layout.item_watch_icon, null);
            return new WatchIconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WatchIconViewHolder watchIconViewHolder, int i) {
            String iconUrl = mIconList[i];
            Glide.with(context).load(iconUrl).into(watchIconViewHolder.ivAppIcon);
        }
    }

    class WatchIconViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAppIcon;

        WatchIconViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.iv_app_icon);
        }
    }
}
