package com.xiaoxun.xun.activitys;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.beans.FunctionBean;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;

/**
 * Created by huangyouyang on 2017/3/13.
 */

public class FunctionControlActivity extends NormalActivity implements MsgCallback {

    private TextView tvTitle;
    private ImageButton btnTitleBack;
    private ImageButton btnTItleConfirm;
    private RelativeLayout cover;
    private RecyclerView functionRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FunctionControlAdapter functionAdapter;

    private ImibabyApp mApp;
    private WatchData curWatch;
    private ArrayList<FunctionBean> functionList;    //本地显示
    private boolean isModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_function_control);
        mApp = (ImibabyApp) getApplication();
        curWatch = mApp.getCurUser().getFocusWatch();

        initView();
        initData();
        initListener();
        initFunctionList();
        getFunctionListFromLocal();
        getFunctionListFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvTitle.setText(getString(R.string.function_control));
        btnTitleBack.setBackgroundResource(R.drawable.btn_cancel_selector);
        btnTItleConfirm.setBackgroundResource(R.drawable.btn_confirm_selector);
        btnTItleConfirm.setVisibility(View.VISIBLE);
    }

    private void initView() {

        tvTitle = findViewById(R.id.tv_title);
        btnTitleBack = findViewById(R.id.iv_title_back);
        btnTItleConfirm = findViewById(R.id.iv_title_menu);
        functionRecyclerView = findViewById(R.id.function_control_recyclerview);
        cover = findViewById(R.id.cover);
    }

    private void initData() {
        functionList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        functionRecyclerView.setLayoutManager(mLinearLayoutManager);
        functionAdapter = new FunctionControlAdapter(this, functionList);
        functionRecyclerView.setAdapter(functionAdapter);
//        functionRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST) {
//        });
    }

    private void initListener() {

        btnTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionControlActivity.this.finish();
            }
        });

        btnTItleConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isModify) {
                    mapsetFunctionList();
                    cover.setClickable(true);
                } else
                    FunctionControlActivity.this.finish();
            }
        });
    }

    private void initFunctionList() {
        if (curWatch.isDevice710()) {
            functionList.clear();
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_DIALER, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_CHATROOM, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_SETTING, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_PETS, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_SPORT, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_CAMERA, "1", "1", myApp));
        } else if (curWatch.isDevice306_A03()) {
            functionList.clear();
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_DIALER, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_CHATROOM, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_SETTING, "1", "1", myApp));
            functionList.add(new FunctionBean(Const.FUNCTION_NAME_STOPWATCH, "1", "1", myApp));
        }
    }

    public void getFunctionListFromLocal() {

        String functionListStr = mApp.getStringValue(curWatch.getEid() + Const.SHARE_PREF_KEY_FUNCTION_LIST, "");
        if (functionListStr.equals("") || functionListStr.equals("[]")) {

        } else {
            try {
                ArrayList<FunctionBean> tempFunctionList = new ArrayList<>();
                JSONArray array = (JSONArray) JSONValue.parse(functionListStr);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObj = (JSONObject) array.get(i);
                    FunctionBean function = new FunctionBean();
                    function = FunctionBean.toBeFunctionBean(function, jsonObj, myApp);
                    if (!function.functionName.equals(Const.FUNCTION_NAME_IMAGEVIEWER)) {
                        tempFunctionList.add(function);
                    }
                }

                replaceLocalDataByServer(tempFunctionList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        functionAdapter.notifyDataSetChanged();
    }

    private void replaceLocalDataByServer(ArrayList<FunctionBean> tempFunctionList) {

        for (FunctionBean functionBean : functionList) {
            for (FunctionBean innerFunction : tempFunctionList) {
                if (functionBean.functionName.equals(innerFunction.functionName)) {
                    functionBean.onoff = innerFunction.onoff;
                    tempFunctionList.remove(innerFunction);
                    break;
                }
            }
        }
    }

    public void getFunctionListFromServer() {
        if (mApp.getNetService() != null)
            mApp.getNetService().sendMapGetMsg(curWatch.getEid(), CloudBridgeUtil.FUNCTION_LIST, FunctionControlActivity.this);
    }

    private void mapsetFunctionList() {

        JSONArray plA = new JSONArray();
        for (FunctionBean function : functionList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject = FunctionBean.toJsonObjectFromSleepTimeBean(jsonObject, function);
            plA.add(jsonObject);
            if (function.functionName.equals(Const.FUNCTION_NAME_CAMERA)) {
                FunctionBean functionImage = new FunctionBean(Const.FUNCTION_NAME_IMAGEVIEWER, function.order, function.onoff, myApp);
                JSONObject jsonObjectImage = new JSONObject();
                jsonObjectImage = FunctionBean.toJsonObjectFromSleepTimeBean(jsonObjectImage, functionImage);
                plA.add(jsonObjectImage);
            }
        }
        int sn = Long.valueOf(TimeUtil.getTimeStampGMT()).intValue();
        if (mApp.getNetService() != null)
            mApp.getNetService().sendMapSetMsg(curWatch.getEid(), curWatch.getFamilyId(), sn,
                    CloudBridgeUtil.FUNCTION_LIST, plA.toString(), FunctionControlActivity.this);
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        cover.setClickable(false);
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        JSONObject pl = (JSONObject) respMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        JSONObject plReq = (JSONObject) reqMsg.get(CloudBridgeUtil.KEY_NAME_PL);
        switch (cid) {
            case CloudBridgeUtil.CID_MAPSET_RESP:
                cover.setClickable(false);
                int rc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rc > 0) {
                    String functionlistString = (String) plReq.get(CloudBridgeUtil.FUNCTION_LIST);
                    mApp.setValue(curWatch.getEid() + Const.SHARE_PREF_KEY_FUNCTION_LIST, functionlistString);
                    ToastUtil.showMyToast(this, getString(R.string.phone_set_success), Toast.LENGTH_SHORT);
                    FunctionControlActivity.this.finish();
                } else if (rc == CloudBridgeUtil.RC_TIMEOUT) {
                    getFunctionListFromLocal();
                    ToastUtil.showMyToast(this, getString(R.string.phone_set_timeout), Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.RC_NETERROR || rc == CloudBridgeUtil.RC_SOCKET_NOTREADY) {
                    getFunctionListFromLocal();
                    ToastUtil.showMyToast(this, getString(R.string.network_error_prompt), Toast.LENGTH_SHORT);
                } else if (rc == CloudBridgeUtil.ERROR_CODE_COMMOM_GENERAL_EXCEPTION) {
                    getFunctionListFromLocal();
                    ToastUtil.showMyToast(this, getString(R.string.set_error), Toast.LENGTH_SHORT);
                }
                break;

            case CloudBridgeUtil.CID_MAPGET_RESP:
                int rcMapGet = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (rcMapGet == CloudBridgeUtil.RC_SUCCESS) {
                    String functionlistString = (String) pl.get(CloudBridgeUtil.FUNCTION_LIST);
                    if (functionlistString != null && functionlistString.length() > 0) {
                        mApp.setValue(curWatch.getEid() + Const.SHARE_PREF_KEY_FUNCTION_LIST, functionlistString);
                        getFunctionListFromLocal();
                    }
                } else if (rcMapGet == CloudBridgeUtil.RC_SOCKET_NOTREADY)
                    ToastUtil.showMyToast(this, getString(R.string.set_error8), Toast.LENGTH_SHORT);
            default:
                break;
        }
    }


    class FunctionControlAdapter extends RecyclerView.Adapter<FunctionControlAdapter.FunctionControlViewHolder> {

        Context context;
        ArrayList<FunctionBean> functions;

        FunctionControlAdapter(Context context, ArrayList<FunctionBean> functions) {
            this.context = context;
            this.functions = functions;
        }

        @Override
        public int getItemCount() {
            return functions.size();
        }

        @Override
        public FunctionControlViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.item_function_control, null);
            return new FunctionControlViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FunctionControlViewHolder holder, int position) {
            final FunctionBean function = functions.get(position);
//            holder.ivFunctionIcon.setImageResource(function.resId);
            holder.tvFunctionName.setText(function.functionNameDesc);
            if ("1".equals(function.onoff))
                holder.cbFunctionOnoff.setChecked(true);
            else
                holder.cbFunctionOnoff.setChecked(false);

            holder.cbFunctionOnoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isModify = true;
                    if ("0".equals(function.onoff)) {
                        function.onoff = "1";
                    } else {
                        function.onoff = "0";
                    }
                }
            });

//            holder.cbFunctionOnoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        function.onoff = "1";
//                        if(curWatch != null && curWatch.getDeviceType() != null)
//
//                    }else {
//                        function.onoff = "0";
//                        if(curWatch != null && curWatch.getDeviceType() != null)
//
//                    }
//                }
//            });
        }

        class FunctionControlViewHolder extends RecyclerView.ViewHolder {

            View itemView;
            ImageView ivFunctionIcon;
            TextView tvFunctionName;
            CheckBox cbFunctionOnoff;

            public FunctionControlViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                ivFunctionIcon = itemView.findViewById(R.id.function_icon);
                tvFunctionName = itemView.findViewById(R.id.function_name);
                cbFunctionOnoff = itemView.findViewById(R.id.function_onoff);
            }
        }

    }
}
