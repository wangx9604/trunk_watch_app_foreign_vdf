package com.xiaoxun.xun.health.motion.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.motion.adapters.MotionScoreAdapter;
import com.xiaoxun.xun.motion.mpv.ISportRecordApi;
import com.xiaoxun.xun.motion.mpv.MotionPresenterImpl;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.networkv2.beans.BaseVPInfo;
import com.xiaoxun.xun.networkv2.beans.MotionResoponseInfo;
import com.xiaoxun.xun.networkv2.beans.MotionScoreBean;
import com.xiaoxun.xun.networkv2.beans.MotionScoreBeanDao;
import com.xiaoxun.xun.networkv2.retrofitclient.GreenDaoManager;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionScoreActivity extends NormalActivity implements ISportRecordApi {

    private ImibabyApp myApp;
    private WatchData mCUrWatch;
    private ArrayList<MotionScoreBean> mScoreList;
    private MotionScoreAdapter mAdapter;
    private ISportRecordApi mPresenterImpl;
    private MotionScoreBeanDao mDao;

    @BindView(R.id.rv_score_list)
    public RecyclerView mSocreRecyclerView;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @OnClick(R.id.iv_title_back)
    public void onClickToBack(){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_score);

        ButterKnife.bind(this);
        mTvTitle.setText(getString(R.string.motion_score_title));
        //1:初始化数据
        initManageData();

        //2:初始化视图
        initView();

        //3:请求网络数据
        JSONObject request = new JSONObject();
        request.put("sid", myApp.getToken());
        request.put("eid", mCUrWatch.getEid());
        String encryptInfo = Base64.encodeToString(AESUtil.encryptAESCBC(request.toJSONString(),
                myApp.getNetService().getAESKEY(), myApp.getNetService().getAESKEY()),
                Base64.NO_WRAP) + myApp.getToken();
        LogUtil.e(":MotionScoreActivity:"+request.toJSONString()+":"+encryptInfo);
        requestSportRecordMonth(new BaseVPInfo(this, request.toJSONString(),"1"));
    }

    private void initView() {
        mAdapter = new MotionScoreAdapter(this, mScoreList);
        mSocreRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSocreRecyclerView.setAdapter(mAdapter);
    }

    private void initManageData() {
        myApp = getMyApp();
        mCUrWatch = myApp.getCurUser().getFocusWatch();
        mPresenterImpl = new MotionPresenterImpl(this);
        mDao = GreenDaoManager.getInstance(this).getDaoSession().getMotionScoreBeanDao();
        mScoreList = new ArrayList<>();
    }

    @Override
    public void requestSportRecordMonth(BaseVPInfo mRequest) {
        mPresenterImpl.requestSportRecordMonth(mRequest);
    }

    @Override
    public void responseSportRecordMonth(MotionResoponseInfo mInfo) {
        if(mInfo.getmCode() != 1 || TextUtils.isEmpty(mInfo.getmStateInfo())){
            //1:刷新本地数据
            onActionUpdateData();
        }else{
            //2:获取需要请求的月份数据
            ArrayList<String> mMothList = MotionUtils.getRequestMonthFromList(myApp, CloudBridgeUtil.MOTION_MONTH_SCORE_VALUE,
                    mCUrWatch.getEid(),
                    mInfo.getmStateInfo());
            if(mMothList.size() == 0) {
                //2.1:刷新本地数据
                onActionUpdateData();
                return;
            }
            StringBuilder mBuilder = new StringBuilder();
            for (String s : mMothList) {
                mBuilder.append(s);
                mBuilder.append(",");
            }
            //3:请求月份数据
            JSONObject request = new JSONObject();
            request.put("sid", myApp.getToken());
            request.put("eid", mCUrWatch.getEid());
            request.put("month", mBuilder.substring(0, mBuilder.length()-1));
            String encryptInfo = Base64.encodeToString(AESUtil.encryptAESCBC(request.toJSONString(),
                    myApp.getNetService().getAESKEY(), myApp.getNetService().getAESKEY()),
                    Base64.NO_WRAP) + myApp.getToken();
            LogUtil.e(":MotionSportRecordActivity:"+request.toJSONString()+":"+encryptInfo);

            requestSportRecordDaily(new BaseVPInfo(this,request.toJSONString(),"1"));
        }
    }

    @Override
    public void requestSportRecordDaily(BaseVPInfo mRequest) {
        mPresenterImpl.requestSportRecordDaily(mRequest);
    }

    @Override
    public void responseSportRecordDaily(MotionResoponseInfo mInfo) {
        if(mInfo.getmCode() == 1 && !TextUtils.isEmpty(mInfo.getmStateInfo())){
            //1:移除当月数据
            String mCurMonth = TimeUtil.getMonth().substring(0,6);
            List<MotionScoreBean> mList = mDao.queryBuilder()
                    .where(MotionScoreBeanDao.Properties.DataTime.like("%"+mCurMonth+"%"))
                    .build().list();
            mDao.deleteInTx(mList);

            //1:解析数据保存数据库
            try {
                String mStateInfo = mInfo.getmStateInfo();
                JSONObject jsonObject = (JSONObject) JSONValue.parse(mStateInfo);
                JSONArray mMonthArray = (JSONArray) jsonObject.get("data");
                for (int i = 0; i < mMonthArray.size(); i++) {
//                    JSONArray mObject = (JSONArray) mMonthArray.get(i);
//                    for (int i1 = 0; i1 < mObject.size(); i1++) {
//                        JSONObject mItemInfo = (JSONObject) mObject.get(i1);
//
//                        //1：数据解析bean 添加额外的Eid数据
//                        MotionScoreBean mBeans = new Gson().fromJson(mItemInfo.toJSONString(),
//                                MotionScoreBean.class);
//                        mBeans.setEid(mCUrWatch.getEid());
//
//                        //2：保存数据库  删除当月数据后，添加数据
//                        mDao.insert(mBeans);
//                    }
                    JSONObject mObject = (JSONObject) mMonthArray.get(i);
                    //1：数据解析bean 添加额外的Eid数据
                    MotionScoreBean mBeans = new Gson().fromJson(mObject.toJSONString(),
                            MotionScoreBean.class);
                    mBeans.setEid(mCUrWatch.getEid());

                    //2：保存数据库  删除当月数据后，添加数据
                    mDao.insert(mBeans);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        onActionUpdateData();
    }

    private void onActionUpdateData() {
        mScoreList.clear();
        String mTemp = myApp.getStringValue(CloudBridgeUtil.MOTION_MONTH_SCORE_VALUE+mCUrWatch.getEid(), "[]");
        //测试数据       mTemp="[\"202203\",\"202202\"]";
        JSONArray mOldArray = (JSONArray) JSONValue.parse(mTemp);
        for (Object o : mOldArray) {
            String mMonth = (String) o;
            List<MotionScoreBean> mList = mDao.queryBuilder()
                    .where(MotionScoreBeanDao.Properties.DataTime.like("%"+mMonth+"%"),
                            MotionScoreBeanDao.Properties.Eid.eq(mCUrWatch.getEid()))
                    .orderDesc(MotionScoreBeanDao.Properties.DataTime)
                    .build().list();
            mScoreList.addAll(mList);
        }
        mAdapter.notifyDataSetChanged();
    }
}