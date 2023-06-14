package com.xiaoxun.xun.motion.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.InterfacesUtil;
import com.xiaoxun.xun.motion.adapters.SportRecordAdapter;
import com.xiaoxun.xun.motion.mpv.ISportRecordApi;
import com.xiaoxun.xun.motion.mpv.MotionPresenterImpl;
import com.xiaoxun.xun.motion.utils.MotionUtils;
import com.xiaoxun.xun.networkv2.beans.BaseVPInfo;
import com.xiaoxun.xun.networkv2.beans.MotionResoponseInfo;
import com.xiaoxun.xun.networkv2.beans.MotionSportRecordBeans;
import com.xiaoxun.xun.networkv2.beans.MotionSportRecordBeansDao;
import com.xiaoxun.xun.networkv2.beans.SportRecordBean;
import com.xiaoxun.xun.networkv2.retrofitclient.GreenDaoManager;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MotionSportRecordActivity extends NormalActivity implements ISportRecordApi {

    private ImibabyApp myApp;
    private WatchData mCurWatch;
    private SportRecordAdapter mAdapter;
    private EasyPopup mCirclePop;
    private int mRecordType = 0;//记录类型
    private ISportRecordApi mPresenterImpl;
    ArrayList<SportRecordBean> mAdapterList;
    MotionSportRecordBeansDao mDao;


    @BindView(R.id.tv_title)
    public TextView mTvTitle;
    @BindView(R.id.layout_info)
    public RecyclerView mLayoutInfoRecyc;
    @BindView(R.id.title_background)
    public View mTitleBg;
    @BindView(R.id.group_no_data)
    Group mGroupNoData;

    @OnClick(R.id.iv_back)
    public void onClickToBack(){
        finish();
    }
    @OnClick({R.id.tv_title,R.id.iv_indicator})
    public void onClickTitle(){
        initPopUpDefSelect();
        mCirclePop.showAtAnchorView(mTitleBg, YGravity.BELOW, XGravity.CENTER, 10,0);
    }

    private void initPopUpDefSelect() {
        View mView0 = mCirclePop.findViewById(R.id.view_0);
        View mView1 = mCirclePop.findViewById(R.id.view_1);
        View mView2 = mCirclePop.findViewById(R.id.view_2);
        View mView3 = mCirclePop.findViewById(R.id.view_3);
        View mView4 = mCirclePop.findViewById(R.id.view_4);
        View mView5 = mCirclePop.findViewById(R.id.view_5);
        View mView6 = mCirclePop.findViewById(R.id.view_6);
        View mView7 = mCirclePop.findViewById(R.id.view_7);
        mView0.setBackgroundResource(R.color.transparent);
        mView1.setBackgroundResource(R.color.transparent);
        mView2.setBackgroundResource(R.color.transparent);
        mView3.setBackgroundResource(R.color.transparent);
        mView4.setBackgroundResource(R.color.transparent);
        mView5.setBackgroundResource(R.color.transparent);
        mView6.setBackgroundResource(R.color.transparent);
        mView7.setBackgroundResource(R.color.transparent);
        switch (mRecordType){
            case 0:
                mView0.setBackgroundResource(R.color.sport_context_color);
                break;
            case 1:
                mView1.setBackgroundResource(R.color.sport_context_color);
                break;
            case -1:
                mView7.setBackgroundResource(R.color.sport_context_color);
                break;
            case MotionUtils.Motion_Sport_Type_5:
                mView2.setBackgroundResource(R.color.sport_context_color);
                break;
            case MotionUtils.Motion_Sport_Type_6:
                mView3.setBackgroundResource(R.color.sport_context_color);
                break;
            case MotionUtils.Motion_Sport_Type_11:
                mView4.setBackgroundResource(R.color.sport_context_color);
                break;
            case MotionUtils.Motion_Sport_Type_9:
                mView5.setBackgroundResource(R.color.sport_context_color);
                break;
            case MotionUtils.Motion_Sport_Type_8:
                mView6.setBackgroundResource(R.color.sport_context_color);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motion_sport_record);

        ButterKnife.bind(this);
        myApp = getMyApp();
        String eid = getIntent().getStringExtra("eid");
        if(eid != null){
            mCurWatch = myApp.getCurUser().queryWatchDataByEid(eid);
        }else {
            mCurWatch = myApp.getCurUser().getFocusWatch();
        }
        mPresenterImpl = new MotionPresenterImpl(this);
        mDao = GreenDaoManager.getInstance(this).getDaoSession().getMotionSportRecordBeansDao();
        //1:初始化视图空间
        initView();

        //2:插入测试数据
//        insertTestData();

        //3:请求网络数据
        JSONObject request = new JSONObject();
        request.put("sid", myApp.getToken());
        request.put("eid", mCurWatch.getEid());
        String encryptInfo = Base64.encodeToString(AESUtil.encryptAESCBC(request.toJSONString(),
                myApp.getNetService().getAESKEY(), myApp.getNetService().getAESKEY()),
                Base64.NO_WRAP) + myApp.getToken();
        LogUtil.e(":MotionSportRecordActivity:"+request.toJSONString()+":"+encryptInfo);
        requestSportRecordMonth(new BaseVPInfo(this, request.toJSONString(),"0"));
    }

    private void insertTestData() {
        mDao.deleteAll();
        MotionSportRecordBeans mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202202", "20220303150258980",
                5,"20220303150007980","20220303150258980","20","0",
                "0","2","126","2",null,null,null,null);
        mDao.insert(mBeans);
        mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202203", "20220303154556019",
                6,"20220303150007980","20220303150258980","1.6","0",
                "0","2","126","2","2","5",null,null);
        mDao.insert(mBeans);
        mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202203", "20220303154556001",
                11,"20220303150007980","20220303150258980","1.6","0",
                "0","2","126","2","2","5",null,null);
        mDao.insert(mBeans);
        mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202203", "20220303154756001",
                7,"20220303150007980","20220303150258980","1.6","0",
                "0","2","10000","2","2","5",null,null);
        mDao.insert(mBeans);
        mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202203", "20220303154856001",
                8,"20220303150007980","20220303150258980","2","0",
                "0","2","10000","2","2","5",null,null);
        mDao.insert(mBeans);
        mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202203", "20220303154856001",
                9,"20220303150007980","20220303150258980","2","0",
                "0","2","2","2","2","5",null,null);
        mDao.insert(mBeans);
        mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202203", "20220303154856001",
                10,"20220303150007980","20220303150258980","2","0",
                "0","2","2","2","2","5","20","20");
        mDao.insert(mBeans);
        mBeans = new MotionSportRecordBeans(mCurWatch.getEid(),"202203", "20220303154856001",
                11,"20220303150007980","20220303150258980","2","0",
                "0","2","2","2","2","5","20","20");
        mDao.insert(mBeans);
    }

    private void initView() {
        mAdapterList = new ArrayList<>();
        mAdapter = new SportRecordAdapter(this, mAdapterList);
        mLayoutInfoRecyc.setLayoutManager(new LinearLayoutManager(this));
        mLayoutInfoRecyc.setAdapter(mAdapter);

        mAdapter.setListener(new InterfacesUtil.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SportRecordBean mBean = mAdapterList.get(position);
                Intent intent = new Intent(MotionSportRecordActivity.this, MotionRecordDetailActivity.class);
                intent.putExtra("_id", mBean.getmRecordId());
                startActivity(intent);
            }
        });

        View.OnClickListener mPopupListnenr = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.view_0:
                        mRecordType = 0;
                        mTvTitle.setText(R.string.motion_type_0);
                        break;
                    case R.id.view_1:
                        mRecordType = 1;
                        mTvTitle.setText(R.string.motion_type_1);
                        break;
                    case R.id.view_2:
                        mRecordType = MotionUtils.Motion_Sport_Type_5;
                        mTvTitle.setText(R.string.motion_type_2);
                        break;
                    case R.id.view_3:
                        mRecordType = MotionUtils.Motion_Sport_Type_6;
                        mTvTitle.setText(R.string.motion_type_3);
                        break;
                    case R.id.view_4:
                        mRecordType = MotionUtils.Motion_Sport_Type_11;
                        mTvTitle.setText(R.string.motion_type_4);
                        break;
                    case R.id.view_5:
                        mRecordType = MotionUtils.Motion_Sport_Type_9;
                        mTvTitle.setText(R.string.motion_type_5);
                        break;
                    case R.id.view_6:
                        mRecordType = MotionUtils.Motion_Sport_Type_8;
                        mTvTitle.setText(R.string.motion_type_6);
                        break;
                    case R.id.view_7:
                        mRecordType = -1;
                        mTvTitle.setText(R.string.motion_type_7);
                        break;
                }
                //1:刷新本地数据
                onActionUpdateData();

                //2:隐藏对话框
                mCirclePop.dismiss();
            }
        };
        mCirclePop = EasyPopup.create()
                .setContentView(this, R.layout.sport_record_list)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(true)
                //变暗的透明度(0-1)，0为完全透明
                .setDimValue(0.4f)
                //变暗的背景颜色
                .setDimColor(Color.GRAY)
                .setWidth(DensityUtil.dip2px(this,140))
                .setOnViewListener(new EasyPopup.OnViewListener() {
                    @Override
                    public void initViews(View view, EasyPopup popup) {
                        view.findViewById(R.id.view_0).setOnClickListener(mPopupListnenr);
                        view.findViewById(R.id.view_1).setOnClickListener(mPopupListnenr);
                        view.findViewById(R.id.view_2).setOnClickListener(mPopupListnenr);
                        view.findViewById(R.id.view_3).setOnClickListener(mPopupListnenr);
                        view.findViewById(R.id.view_4).setOnClickListener(mPopupListnenr);
                        view.findViewById(R.id.view_5).setOnClickListener(mPopupListnenr);
                        view.findViewById(R.id.view_6).setOnClickListener(mPopupListnenr);
                        view.findViewById(R.id.view_7).setOnClickListener(mPopupListnenr);
                    }
                })
                .apply();
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
            ArrayList<String> mMothList = MotionUtils.getRequestMonthFromList(myApp,CloudBridgeUtil.MOTION_MONTH_VALUE,
                    mCurWatch.getEid(),
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
            request.put("eid", mCurWatch.getEid());
            request.put("month", mBuilder.substring(0, mBuilder.length()-1));
            String encryptInfo = Base64.encodeToString(AESUtil.encryptAESCBC(request.toJSONString(),
                    myApp.getNetService().getAESKEY(), myApp.getNetService().getAESKEY()),
                    Base64.NO_WRAP) + myApp.getToken();
            LogUtil.e(":MotionSportRecordActivity:"+request.toJSONString()+":"+encryptInfo);

            requestSportRecordDaily(new BaseVPInfo(this,request.toJSONString(),"0"));
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
            String mCurMonth =TimeUtil.getMonth().substring(0,6);
            List<MotionSportRecordBeans> mList = mDao.queryBuilder()
                    .where(MotionSportRecordBeansDao.Properties.Recordmonth.like(mCurMonth),
                            MotionSportRecordBeansDao.Properties.Eid.eq(mCurWatch.getEid()))
                    .build().list();
            mDao.deleteInTx(mList);

            //2:解析数据保存数据库
            String mStateInfo = mInfo.getmStateInfo();
            JSONObject mJsonInfo = (JSONObject) JSONValue.parse(mStateInfo);
            JSONArray mMonthArray = (JSONArray) mJsonInfo.get("data");
            for (int i = 0; i < mMonthArray.size(); i++) {
                JSONObject mObject = (JSONObject) mMonthArray.get(i);
                JSONObject mDailyData = (JSONObject) mObject.get("data");
                String time = (String)mObject.get("time");
                int mType = (int)mObject.get("type");
                //1：数据解析bean 添加额外的recordmonth数据
//                MotionSportRecordBeans mBeans = new Gson().fromJson(mDailyData.toJSONString(),
//                        MotionSportRecordBeans.class);
                MotionSportRecordBeans mBeans = MotionUtils.convertRecordFromJson(mDailyData.toJSONString());
                mBeans.setTime(time);
                mBeans.setType(mType);
                mBeans.setEid(mCurWatch.getEid());
                mBeans.setRecordmonth(time.substring(0,6));

                //2：保存数据库  删除当月数据后，添加数据
                mDao.insert(mBeans);
            }
        }
        onActionUpdateData();
    }

    private void onActionUpdateData() {
        //1:所有数据
        mAdapterList.clear();
        String mTemp = myApp.getStringValue(CloudBridgeUtil.MOTION_MONTH_VALUE+mCurWatch.getEid(), "");
//        mTemp="[\"202203\",\"202202\"]";
        try {
            JSONArray mOldArray = (JSONArray) JSONValue.parse(mTemp);
            for (Object o : mOldArray) {
                String mMonth = (String) o;
                switch (mRecordType) {
                    case -1:
                        List<MotionSportRecordBeans> mList = mDao.queryBuilder()
                                .where(MotionSportRecordBeansDao.Properties.Recordmonth.like(mMonth),
                                        MotionSportRecordBeansDao.Properties.Eid.eq(mCurWatch.getEid()))
                                .whereOr(MotionSportRecordBeansDao.Properties.Type.eq(7),
                                        MotionSportRecordBeansDao.Properties.Type.eq(10),
                                        MotionSportRecordBeansDao.Properties.Type.eq(101),
                                        MotionSportRecordBeansDao.Properties.Type.eq(102),
                                        MotionSportRecordBeansDao.Properties.Type.eq(103),
                                        MotionSportRecordBeansDao.Properties.Type.eq(104),
                                        MotionSportRecordBeansDao.Properties.Type.eq(105),
                                        MotionSportRecordBeansDao.Properties.Type.eq(106),
                                        MotionSportRecordBeansDao.Properties.Type.eq(107),
                                        MotionSportRecordBeansDao.Properties.Type.eq(108),
                                        MotionSportRecordBeansDao.Properties.Type.eq(109),
                                        MotionSportRecordBeansDao.Properties.Type.eq(110),
                                        MotionSportRecordBeansDao.Properties.Type.eq(111),
                                        MotionSportRecordBeansDao.Properties.Type.eq(112),
                                        MotionSportRecordBeansDao.Properties.Type.eq(113),
                                        MotionSportRecordBeansDao.Properties.Type.eq(114),
                                        MotionSportRecordBeansDao.Properties.Type.eq(115),
                                        MotionSportRecordBeansDao.Properties.Type.eq(116),
                                        MotionSportRecordBeansDao.Properties.Type.eq(117),
                                        MotionSportRecordBeansDao.Properties.Type.eq(118),
                                        MotionSportRecordBeansDao.Properties.Type.eq(119),
                                        MotionSportRecordBeansDao.Properties.Type.eq(120),
                                        MotionSportRecordBeansDao.Properties.Type.eq(121),
                                        MotionSportRecordBeansDao.Properties.Type.eq(122),
                                        MotionSportRecordBeansDao.Properties.Type.eq(123))
                                .orderDesc(MotionSportRecordBeansDao.Properties.Time)
                                .build().list();
                        mAdapterList.addAll(MotionUtils.onSportRecordToAdapterItemData(this, mList));
                        break;
                    case 0:
                        mList = mDao.queryBuilder()
                                .where(MotionSportRecordBeansDao.Properties.Recordmonth.like(mMonth),
                                        MotionSportRecordBeansDao.Properties.Eid.eq(mCurWatch.getEid()))
                                .orderDesc(MotionSportRecordBeansDao.Properties.Time)
                                .build().list();
                        mAdapterList.addAll(MotionUtils.onSportRecordToAdapterItemData(this, mList));
                        break;
                    case 1:
                        mList = mDao.queryBuilder()
                                .where(MotionSportRecordBeansDao.Properties.Recordmonth.like(mMonth),
                                        MotionSportRecordBeansDao.Properties.Istest.eq("1"),
                                        MotionSportRecordBeansDao.Properties.Eid.eq(mCurWatch.getEid()))
                                .orderDesc(MotionSportRecordBeansDao.Properties.Time)
                                .build().list();
                        mAdapterList.addAll(MotionUtils.onSportRecordToAdapterItemData(this, mList));
                        break;
                    default:
                        QueryBuilder<MotionSportRecordBeans> mBuild = mDao.queryBuilder();
                        mBuild.where(MotionSportRecordBeansDao.Properties.Recordmonth.like(mMonth),
                                MotionSportRecordBeansDao.Properties.Type.eq(mRecordType),
                                MotionSportRecordBeansDao.Properties.Eid.eq(mCurWatch.getEid()));
                        mBuild.or(MotionSportRecordBeansDao.Properties.Istest.notEq("1"),
                                MotionSportRecordBeansDao.Properties.Istest.isNull());
                        mBuild.orderDesc(MotionSportRecordBeansDao.Properties.Time);
                        mList = mBuild.build().list();
                        mAdapterList.addAll(MotionUtils.onSportRecordToAdapterItemData(this, mList));
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(mAdapterList.size() >0){
            mGroupNoData.setVisibility(View.GONE);
            mLayoutInfoRecyc.setVisibility(View.VISIBLE);
        }else{
            mGroupNoData.setVisibility(View.VISIBLE);
            mLayoutInfoRecyc.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }
}