package com.xiaoxun.xun.activitys;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.WatchAppBean;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.services.NetService;
import com.xiaoxun.xun.utils.AppStoreUtils;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppManagerActivity extends NormalActivity implements MsgCallback {

    private ImageButton mBtnBack;
    private ImageButton mBtnSetting;
    private TextView mTitle;

    private RecyclerView mLayoutAppList;
    private ArrayList<WatchAppBean> mAppList = new ArrayList<>();
    private WatchAppListAdapter mAppListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Map<String, String>> packInfoList;

    private WatchData focusWatch;
    private NetService mNetService;
    AppStoreHandler mHandler;
    private BroadcastReceiver mReceiver;

    class AppStoreHandler extends Handler {

        static final int UPDATE_INSTALLAPP_LIST = 2;
        static final int UPDATE_APPSTORE_ITEM = 3;

        static final int ERROR = 1000;

        final WeakReference<AppManagerActivity> mActivity;

        AppStoreHandler(AppManagerActivity mActivity, Looper looper) {
            super(looper);
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManagerActivity context = mActivity.get();
            switch (msg.what) {
                case UPDATE_INSTALLAPP_LIST: {
                    mAppListAdapter.notifyDataSetChanged();
                }
                break;

                case UPDATE_APPSTORE_ITEM: {
                    int position = (int) msg.obj;
                    mAppListAdapter.notifyItemChanged(position);
//                    mAppListAdapter.notifyDataSetChanged();
                }
                break;

                case ERROR:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initView();
        initData();
        initReceiver();
        initListener();
        reqInstalledAppList();
        getFunctionListHighpower();
    }

    private void initView() {

        mBtnBack = (ImageButton) findViewById(R.id.iv_title_back);
        mBtnSetting = (ImageButton) findViewById(R.id.iv_title_menu);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mLayoutAppList = (RecyclerView) findViewById(R.id.recyclerview_app_list);

        mTitle.setText(R.string.app_manager);
        mBtnSetting.setVisibility(View.GONE);
    }

    private void initData() {

        focusWatch = myApp.getCurUser().getFocusWatch();
        mNetService = myApp.getNetService();
        mHandler = new AppStoreHandler(AppManagerActivity.this, getMainLooper());
        packInfoList = AppStoreUtils.getTableFromSourceData(myApp);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLayoutAppList.setLayoutManager(mLinearLayoutManager);
        mAppListAdapter = new WatchAppListAdapter(AppManagerActivity.this);
        mLayoutAppList.setAdapter(mAppListAdapter);
        mAppListAdapter.notifyDataSetChanged();
    }

    private void initReceiver() {

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Const.ACTION_INSTALL_APPLIST_CHANGE.equals(intent.getAction())) {
                    reqInstalledAppList();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_INSTALL_APPLIST_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    private void initListener() {

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManagerActivity.this.finish();
            }
        });

        setOnButtonClickLitener(new OnButtonClickListener() {
            @Override
            public void onStatusClick(int position) {
                if (!myApp.isMeAdmin(focusWatch)) {
                    ToastUtil.show(AppManagerActivity.this, getString(R.string.need_admin_auth));
                    return;
                }
                WatchAppBean appBean = mAppList.get(position);
                openUninstallAppDialog(appBean, position);
            }

            @Override
            public void onHiddenClick(int position) {
                if (!myApp.isMeAdmin(focusWatch)) {
                    ToastUtil.show(AppManagerActivity.this, getString(R.string.need_admin_auth));
                    return;
                }
                WatchAppBean appBean = mAppList.get(position);
                if (appBean.name == null) return;
                if (appBean.hidden == 1) {
                    boolean isShowDialog = false;
                    int count = 0;
                    if (flistData.size() > 0) {
                        for (int i = 0; i < flistData.size(); i++) {
                            count = count + 1;
                            String name = flistData.get(i);
                            if (appBean.name.equals(name)) {
                                isShowDialog = true;
                                openPromptDialog(appBean, 1, position);
                            } else {
                                if (count == flistData.size()) {
                                    if (!isShowDialog) {
                                        appBean.hidden = 0;
                                        setWatchAppState(appBean, 1, position);
                                    }
                                }
                            }
                        }
                    } else {
                        appBean.hidden = 0;
                        setWatchAppState(appBean, 1, position);
                    }
                } else if (appBean.hidden == 0) {
                    appBean.hidden = 1;
                    setWatchAppState(appBean, 1, position);
                }

            }
        });
    }

    private void openPromptDialog(final WatchAppBean appBean, int optType, final int position) {
        Dialog dlg = DialogUtil.CustomNormalDialog(AppManagerActivity.this,
                getString(R.string.prompt),
                getString(R.string.dialog_prompt, "" + appBean.name),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, getString(R.string.cancel),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        appBean.hidden = 0;
                        setWatchAppState(appBean, 1, position);
                    }
                }, getString(R.string.confirm));
        dlg.show();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void reqInstalledAppList() {

        AppStoreUtils.getInstance(this).getInstalledAppList(focusWatch.getEid(), mNetService, myApp.getToken(),
                new AppStoreUtils.OperationCallback() {

                    @Override
                    public void onSuccess(String result) {

                        JSONArray appArray = (JSONArray) JSONValue.parse(result);
                        mAppList = WatchAppBean.toWatchAppList(appArray);
                        mHandler.sendEmptyMessage(AppStoreHandler.UPDATE_INSTALLAPP_LIST);
                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
    }

    private void openUninstallAppDialog(final WatchAppBean appBean, final int position) {

        String title = getString(R.string.watch_app_uninstall_title);
        String desc = getString(R.string.app_uninstall_desc, appBean.name);
        Dialog dlg = DialogUtil.CustomNormalDialog(AppManagerActivity.this,
                title, desc,
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                    }
                },
                getText(R.string.cancel).toString(),
                new DialogUtil.OnCustomDialogListener() {
                    @Override
                    public void onClick(View v) {
                        appBean.status = 3;
                        setWatchAppState(appBean, 1, position);
                    }
                },
                getText(R.string.confirm).toString());
        dlg.show();
    }

    private void setWatchAppState(final WatchAppBean appBean, int optType, final int position) {

        AppStoreUtils.getInstance(AppManagerActivity.this).setWatchAppState(appBean, optType, focusWatch.getEid(), focusWatch.getFamilyId(),
                mNetService, myApp.getToken(), new AppStoreUtils.OperationCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Message msg = Message.obtain();
                        msg.what = AppStoreHandler.UPDATE_APPSTORE_ITEM;
                        msg.obj = position;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFail(String error) {
                        ToastUtil.show(AppManagerActivity.this, error);
                    }
                });
    }

    class WatchAppListAdapter extends RecyclerView.Adapter<WatchAppViewHolder> {

        Context context;

        WatchAppListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return mAppList.size();
        }

        @NonNull
        @Override
        public WatchAppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = View.inflate(context, R.layout.item_watch_app, null);
            return new WatchAppViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final WatchAppViewHolder watchAppViewHolder, final int i) {
            WatchAppBean appBean = mAppList.get(i);
            watchAppViewHolder.tvAppName.setText(
                    appBean.name == null ? "" : appBean.name
            );

            if (appBean.status == 0 || appBean.status == 2 || appBean.status == 3)
                watchAppViewHolder.layoutApp.setVisibility(View.VISIBLE);
            else
                watchAppViewHolder.layoutApp.setVisibility(View.GONE);

            // icon
            showAppIcon(context, appBean.icon, appBean.app_id, watchAppViewHolder.ivAppIcon);
            // status
            showAppStatus(appBean, watchAppViewHolder.btnUninstallApp);
            // hidden
            showAppHidden(appBean, watchAppViewHolder.btnHiddenApp);

            watchAppViewHolder.btnUninstallApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnButtonClickLitener != null)
                        mOnButtonClickLitener.onStatusClick(i);
                }
            });
            watchAppViewHolder.btnHiddenApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnButtonClickLitener != null)
                        mOnButtonClickLitener.onHiddenClick(i);
                }
            });
        }
    }

    private void showAppIcon(final Context context, String url, String app_id, final ImageView imageView) {

        String imageUrl;
        if (url == null || !url.startsWith("http")) {
            imageUrl = AppStoreUtils.getInstance(this).getIconUrlFromList(myApp, app_id, packInfoList);
            if (TextUtils.isEmpty(imageUrl)) {
                int index = WatchAppBean.getResIdByPackage(app_id);
                imageView.setImageResource(WatchAppBean.app_icons[index]);
                return;
            }
        } else {
            imageUrl = url;
        }

        Bitmap headBitmap = new ImageDownloadHelper(context).downloadImage(imageUrl, new ImageDownloadHelper.OnImageDownloadListener() {
            @Override
            public void onImageDownload(String url, Bitmap bitmap) {
                Drawable headDrawable = new BitmapDrawable(context.getResources(), bitmap);
                ImageUtil.setMaskImage(imageView, R.drawable.head_2, headDrawable);
            }
        });
        if (headBitmap != null) {
            Drawable headDrawable = new BitmapDrawable(context.getResources(), headBitmap);
            ImageUtil.setMaskImage(imageView, R.drawable.head_2, headDrawable);
        }
    }

    private void showAppStatus(WatchAppBean appBean, ImageButton btnUninstallApp) {

        if ("ado.install.xiaoxun.com.xiaoxuninstallapk".equals(appBean.app_id)) {
            btnUninstallApp.setVisibility(View.INVISIBLE);
            return;
        }
        if (appBean.type != 1) {
            btnUninstallApp.setVisibility(View.GONE);
            return;
        }
        switch (appBean.status) {
            case 0:
            case 2:
                btnUninstallApp.setVisibility(View.VISIBLE);
                btnUninstallApp.setBackgroundResource(R.drawable.btn_app_uninstall);
                break;
            case 3:
                btnUninstallApp.setVisibility(View.VISIBLE);
                btnUninstallApp.setBackgroundResource(R.drawable.btn_app_uninstall_wait);
                break;
        }
    }

    private void showAppHidden(WatchAppBean appBean, ImageButton btnHiddenApp) {

        if ("ado.install.xiaoxun.com.xiaoxuninstallapk".equals(appBean.app_id)) {
            btnHiddenApp.setVisibility(View.INVISIBLE);
            return;
        }
        btnHiddenApp.setVisibility(View.VISIBLE);
        btnHiddenApp.setBackground(null);
        switch (appBean.hidden) {
            case 0:
                btnHiddenApp.setImageResource(R.drawable.switch_on);  //隐藏
                break;
            case 1:
                btnHiddenApp.setImageResource(R.drawable.switch_off);  //显示
                break;
        }
    }

    class WatchAppViewHolder extends RecyclerView.ViewHolder {

        View layoutApp;
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppSize;
        TextView tvAppDesc;
        ImageButton btnUninstallApp;
        ImageButton btnHiddenApp;

        WatchAppViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutApp = itemView.findViewById(R.id.layout_watch_app);
            ivAppIcon = (ImageView) itemView.findViewById(R.id.iv_app_icon);
            tvAppName = (TextView) itemView.findViewById(R.id.tv_app_name);
            tvAppSize = (TextView) itemView.findViewById(R.id.tv_app_size);
            tvAppSize.setVisibility(View.GONE);
            tvAppDesc = (TextView) itemView.findViewById(R.id.tv_app_desc);
            tvAppDesc.setVisibility(View.GONE);
            btnUninstallApp = (ImageButton) itemView.findViewById(R.id.btn_uninstall_app);
            btnHiddenApp = (ImageButton) itemView.findViewById(R.id.btn_download_app);
        }
    }

    private OnButtonClickListener mOnButtonClickLitener;

    public void setOnButtonClickLitener(OnButtonClickListener mOnButtonClickLitener) {
        this.mOnButtonClickLitener = mOnButtonClickLitener;
    }

    interface OnButtonClickListener {
        void onStatusClick(int position);

        void onHiddenClick(int position);
    }

    public void getFunctionListHighpower() {
        if (myApp.getNetService() != null)
            myApp.getNetService().getHighPowerApplist(focusWatch.getEid(), AppManagerActivity.this);
    }

    private List<String> flistData = new ArrayList<>();

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        switch (cid) {
            case CloudBridgeUtil.CID_GET_FUNCTION_HIGHPOWER_STATE_RESP:
                int rcMapGetH = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rcMapGetH == CloudBridgeUtil.RC_SUCCESS) {
                    try {
                        JSONArray functionflist = (JSONArray) pl.get(CloudBridgeUtil.FUNCTION_FLIST);
                        flistData.clear();
                        for (int i = 0; i < functionflist.size(); i++) {
                            JSONObject jsonObject2 = (JSONObject) functionflist.get(i);
                            String attrstr = (String) jsonObject2.get("attr");
                            if (attrstr.equals("1")) {
                                flistData.add((String) jsonObject2.get("name"));
                            }
                        }
                        Log.i("cui", "flistData = " + flistData.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

}
