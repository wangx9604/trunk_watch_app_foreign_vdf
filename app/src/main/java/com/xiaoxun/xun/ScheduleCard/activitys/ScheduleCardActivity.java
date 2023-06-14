package com.xiaoxun.xun.ScheduleCard.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.calendar.LoadingDialog;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.ScheduleCard.Views.MenuPopUpWindow;
import com.xiaoxun.xun.ScheduleCard.adapters.ScheduleCardAdapter;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleCardBean;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleCardItemBean;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.motion.beans.ScheduleWeekBean;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.CustomSelectDialogUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import net.minidev.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ScheduleCardActivity extends NormalActivity implements View.OnClickListener, MsgCallback {

    private final static String TAG = ScheduleCardActivity.class.getSimpleName();
    private final static String TESTDATA = "{\"weeklist\":[\"语文,数学,英语,手工,体育,科学\",\"语文,N/A,英语,手工,体育,科学\",\"语文,数学,英语,手工,N/A,科学\",\"语文,数学,英语,N/A,体育,科学\",\"语文,数学,英语,N/A,N/A,N/A\",\"N/A,N/A,N/A,N/A,N/A,N/A\",\"N/A,N/A,N/A,N/A,N/A,N/A\"],\"city\":\"上海\",\"class\":\"8\",\"customlist\":[\"手工\",\"书法\",\"科学\"],\"optype\":0,\"timelist\":[\"0810,0840\",\"0850,0930\",\"0950,1030\",\"1040,1120\",\"1430,1510\",\"1530,1610\"],\"province\":\"上海\",\"school\":\"向阳小学\",\"district\":\"徐汇\",\"grade\":\"9\",\"location\":\"119.599,48.866\",\"updateTS\":\"20200214174135788\"}";

    private LoadingDialog loadingDialog;
    private ImageView mBackImageView;
    private ImageView mMenuImageView;
    private ImageView mClassEditImageView;
    private ImageView mClassTran;
    private ImageView mCommitImageView;
    private RecyclerView mScheduleShowRecyclerView;
    private RecyclerView mClassEditRecyclerView;
    private ScheduleCardAdapter mScheduleCardAdapter;
    private ScheduleCardAdapter mScheduleEditAdapter;
    private Group mEditGroup;
    private Group mNoInfoGroup;
    TextView mTitleTextView;
    private Group mHasInfoGroup;
    private Group mTransStatusGroup;
    private ImageView mEditClear;
    private ImageView mEditAddClass;
    private TextView mSetCardInfoTextView;
    private View backgroud_view;
    //周末课程表
    private TextView mTvNowSettingWeek;
    private ImageView mIvWeekEdit;
    private Group mNoWeekGroup;
    private Group mWeekInfoGroup;
    private ConstraintLayout mLayoutForWeekClass;
    private LinearLayout mLayoutForShowMoringClass;
    private LinearLayout mLayoutForShowAfteringClass;

    private MenuPopUpWindow menuPopUpWindow;//切换课程的弹出框

    private WatchData mCurWatch;
    private ImibabyApp myApp;
    private String mScheduleInfo = "";

    private ScheduleCardBean cardBean = new ScheduleCardBean();
    private ArrayList<ScheduleCardItemBean> mCardAdapterList;
    private ArrayList<ScheduleCardItemBean> mEditAdapterList;

    private boolean isHasScheduleInfo = false;//是否有课程表数据
    private boolean isFirstSet = false;       //是否首次设置完成
    private boolean isScheduleEdit = false;  //是否处于编辑模式
    private boolean isSupportWeekShow = false;//是否处于周末课程
    private boolean isScheduleClearMode = false; //是否处于全局擦除模式
    private ScheduleCardHandler myHandler;
    private String mEditSelectItem; //编辑状态下选择的课程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_card);
        initActManage(getIntent());
        initViews();

        initDatas();
        initAdapterListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initActManage(intent);
        initViews();
        initDatas();
        initAdapterListener();
    }

    private void initAdapterListener() {
        mScheduleEditAdapter.setmOnRemoveClearListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isScheduleClearMode) {
                    isScheduleClearMode = false;
                    mEditClear.setBackgroundResource(R.drawable.schedule_card_clear_0);

                    //解除擦除状态后，更新列表和编辑视图
                    onEditStateChangedSet(true);
                } else {
                    isScheduleClearMode = true;
                    mEditClear.setBackgroundResource(R.drawable.schedule_card_clear_1);
                    //进入擦除状态后，移除编辑选择状态，同时更新列表和编辑视图
                    mEditSelectItem = ScheduleCardUtils.onUpdateEditSelectOper(mCardAdapterList, mEditAdapterList, position);
                    mScheduleEditAdapter.notifyDataSetChanged();
                    mScheduleCardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mScheduleEditAdapter.setmOnAddClassListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                //1:判断是否大于20个自定义课程
                if (mEditAdapterList.size() - 2 >= ScheduleCardUtils.FIELD_CLASS_LIMIT_NUM) {
                    ToastUtil.showMyToast(ScheduleCardActivity.this,
                            getText(R.string.schedule_add_class_limit).toString(), Toast.LENGTH_SHORT);
                    return;
                }

                ScheduleCardUtils.onOpenEditDialog(ScheduleCardActivity.this,
                        getText(R.string.schedule_class_name).toString(),
                        getText(R.string.schedule_class_name_limit_0).toString(),
                        new CustomSelectDialogUtil.CustomDialogListener() {
                            @Override
                            public void onClick(View v, String text) {
                                if (text.length() > 12 || text.length() < 1) {
                                    ToastUtil.showMyToast(ScheduleCardActivity.this, getText(R.string.schedule_class_name_limit_0).toString(), Toast.LENGTH_SHORT);
                                } else {
                                    if (ScheduleCardUtils.checkClassNameIsSameForCard(mEditAdapterList, text)) {
                                        ToastUtil.showMyToast(ScheduleCardActivity.this, getText(R.string.schedule_class_name_limit_1).toString(), Toast.LENGTH_SHORT);
                                        return;
                                    }

                                    ScheduleCardItemBean itemBean = new ScheduleCardItemBean(3, text, null,
                                            false, false, false);
                                    mEditAdapterList.add(mEditAdapterList.size() - 2, itemBean);
                                    mEditSelectItem = ScheduleCardUtils.onUpdateEditSelectOper(mCardAdapterList, mEditAdapterList, mEditAdapterList.size() - 3);

                                    mScheduleEditAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                );
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mScheduleEditAdapter.setmOnEditItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mEditAdapterList.size() - 1 >= position) {
                    if (mEditSelectItem.equals(mEditAdapterList.get(position).getmScheduleName())) {
                        return;
                    }
                    //1:选择后，清理全局擦除控制
                    isScheduleClearMode = false;
                    mEditClear.setBackgroundResource(R.drawable.schedule_card_clear_0);

                    //2：更新编辑列表的选择状态
                    mEditSelectItem = ScheduleCardUtils.onUpdateEditSelectOper(mCardAdapterList, mEditAdapterList, position);
                    mScheduleEditAdapter.notifyDataSetChanged();
                    mScheduleCardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                if (position < ScheduleCardUtils.BASE_CLASS.length) {
                    ToastUtil.showMyToast(ScheduleCardActivity.this,
                            getString(R.string.schedule_delete_def_class),
                            Toast.LENGTH_SHORT);
                    return;
                }
                if (position >= mEditAdapterList.size() - 2) return;

                final ScheduleCardItemBean mbeans = mEditAdapterList.get(position);
                String delete_title = getString(R.string.schedule_delete_cust_class, mbeans.getmScheduleName());
                ScheduleCardUtils.onLongClickDeleteDialog(ScheduleCardActivity.this,
                        delete_title,
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                                mEditAdapterList.remove(mbeans);
                                mScheduleEditAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });
        mScheduleCardAdapter.setmOnCardItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mCardAdapterList.size() - 1 >= position) {
                    if (!mCardAdapterList.get(position).isEditMode()) {
                        return;
                    }
                    if ("".equals(mEditSelectItem) && !isScheduleClearMode) {
                        //执行弹窗提醒用户设置课表
                        Dialog dlg = DialogUtil.CustomNormalDialog(ScheduleCardActivity.this,
                                getString(R.string.prompt),
                                getString(R.string.schedule_select_edit_hint),
                                new DialogUtil.OnCustomDialogListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }, getText(R.string.donothing_text).toString());
                        dlg.show();
                        return;
                    }
                    if (isScheduleClearMode) {
                        mCardAdapterList.get(position).setmScheduleName("N/A");
                    } else {
                        if (mEditSelectItem.equals(mCardAdapterList.get(position).getmScheduleName())) {
                            mCardAdapterList.get(position).setmScheduleName("N/A");
                        } else {
                            mCardAdapterList.get(position).setmScheduleName(mEditSelectItem);
                            mCardAdapterList.get(position).setEditOperate(false);
                        }
                    }
                    mScheduleCardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ScheduleCardUtils.hideLoading(loadingDialog);
        loadingDialog = null;
    }

    @Override
    public void onBackPressed() {
        onBackAction();
    }

    private void onBackAction() {
        if (isScheduleEdit) {
            //检查是否有修改
            ArrayList<String> weeklist = ScheduleCardUtils.transFixInfoToStandSchedule(cardBean, mCardAdapterList);
            ArrayList<String> mCustomList = ScheduleCardUtils.transCustomClassToStandSchedule(mEditAdapterList);

            if (ScheduleCardUtils.onCheckClassArrayForChange(cardBean.getWeeklist(), weeklist) ||
                    ScheduleCardUtils.onCheckClassArrayForChange(cardBean.getCustomlist(), mCustomList)) {
                DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                        getString(R.string.prompt),
                        getString(R.string.schedule_edit_exit),
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                                v.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        transScheduleInfoToCardBean();
                                    }
                                }, 500);

                            }
                        },
                        getText(R.string.schedule_edit_exit_yes).toString(),
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        },
                        getText(R.string.schedule_edit_exit_no).toString());
            } else {
                onEditStateChangedSet(false);
            }

        } else {
            finish();
        }
    }

    private void onEditStateChangedSet(boolean isEdit) {
        isScheduleEdit = isEdit;
        isScheduleClearMode = false;

        //1：初始化编辑状态的初始数据
        mEditSelectItem = "";
        if (isEdit) {
            ScheduleCardUtils.OnEditStateByOriginal(mCardAdapterList, mEditAdapterList);
        } else {
            ScheduleCardUtils.OnEditStateEndByOriginal(mCardAdapterList, mEditAdapterList);
        }

        //2：更新编辑状态下的视图
        updateScheduleViewByStatu();

        myApp.sdcardLog("scheduleXXXX:onEditStateChangedSet" + mCardAdapterList.size());
        //3:更新编辑列表和课程列表
        mScheduleCardAdapter.notifyDataSetChanged();
        mScheduleEditAdapter.notifyDataSetChanged();
    }

    private void updateScheduleViewByStatu() {
        if (isHasScheduleInfo) {
            onSupperWeekView();
            mTransStatusGroup.setVisibility(View.VISIBLE);
            mHasInfoGroup.setVisibility(View.VISIBLE);
            mNoInfoGroup.setVisibility(View.GONE);
        } else {
            mClassTran.setVisibility(View.GONE);
            mTransStatusGroup.setVisibility(View.GONE);
            mHasInfoGroup.setVisibility(View.GONE);
            mNoInfoGroup.setVisibility(View.VISIBLE);
        }

        if (isScheduleEdit) {
            mTitleTextView.setText(R.string.schedule_edit_title);
            mClassTran.setVisibility(View.GONE);
            mEditGroup.setVisibility(View.VISIBLE);
            mTransStatusGroup.setVisibility(View.GONE);
        } else {
            mTitleTextView.setText(R.string.schedule_card_title);
            mEditGroup.setVisibility(View.GONE);
            if (isHasScheduleInfo) {
                mTransStatusGroup.setVisibility(View.VISIBLE);
                onSupperWeekView();
            } else {
                mClassTran.setVisibility(View.GONE);
                mTransStatusGroup.setVisibility(View.GONE);
            }
        }
    }

    private void onSupperWeekView() {
        if (isSupportWeekShow) {
            mClassTran.setVisibility(View.VISIBLE);
        } else {
            mClassTran.setVisibility(View.GONE);
        }
    }

    private void initDatas() {
        //1:加载本地课表数据
        transScheduleInfoToCardBean();

        //2：请求网络课表数据
        if (!isFirstSet) {
            //显示加载对话框
            ScheduleCardUtils.showLoadingDialog(loadingDialog, getString(R.string.loading));
            ScheduleCardUtils.getScheduleCardInfo(myApp, this, mCurWatch);
        }
    }

    private void initViews() {
        loadingDialog = new LoadingDialog(this, R.style.Theme_DataSheet, null);
        initPopUpDialog();
        mBackImageView = findViewById(R.id.iv_back);
        mClassEditImageView = findViewById(R.id.iv_class_edit);
        mClassTran = findViewById(R.id.iv_class_tran);
        mMenuImageView = findViewById(R.id.iv_menu);
        mCommitImageView = findViewById(R.id.iv_commit);
        mClassEditRecyclerView = findViewById(R.id.class_edit_view);
        mScheduleShowRecyclerView = findViewById(R.id.schedule_show_view);
        mNoInfoGroup = findViewById(R.id.no_info_group);
        mTitleTextView = findViewById(R.id.tv_title);
        mHasInfoGroup = findViewById(R.id.has_info_group);
        mEditGroup = findViewById(R.id.edit_group);
        mTransStatusGroup = findViewById(R.id.trans_status_group);
        mEditClear = findViewById(R.id.edit_menu_clear);
        mEditAddClass = findViewById(R.id.edit_menu_class);
        mSetCardInfoTextView = findViewById(R.id.tv_now_setting);
        backgroud_view = findViewById(R.id.bottom_view);
        mTvNowSettingWeek = findViewById(R.id.tv_now_setting_week);
        mIvWeekEdit = findViewById(R.id.iv_week_edit);
        mNoWeekGroup = findViewById(R.id.no_week_group);
        mWeekInfoGroup = findViewById(R.id.schedule_week_info_group);
        mLayoutForWeekClass = findViewById(R.id.layout_week_show);
        mLayoutForShowMoringClass = findViewById(R.id.layout_moring_show);
        mLayoutForShowAfteringClass = findViewById(R.id.layout_aftering_show);

        if (mCurWatch.isDevice707_H01() || mCurWatch.isDevice709_H01()) {
            mClassTran.setVisibility(View.VISIBLE);
        } else {
            mClassTran.setVisibility(View.GONE);
        }

        mBackImageView.setOnClickListener(this);
        mClassEditImageView.setOnClickListener(this);
        mMenuImageView.setOnClickListener(this);
        mCommitImageView.setOnClickListener(this);
        mEditAddClass.setOnClickListener(this);
        mEditClear.setOnClickListener(this);
        mSetCardInfoTextView.setOnClickListener(this);
        mTvNowSettingWeek.setOnClickListener(this);
        mClassTran.setOnClickListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, ScheduleCardUtils.WEEKSUM + 1);
        mScheduleShowRecyclerView.setLayoutManager(layoutManager);
        mScheduleCardAdapter = new ScheduleCardAdapter(this, mCardAdapterList, myHandler);
        mScheduleShowRecyclerView.setAdapter(mScheduleCardAdapter);

        layoutManager = new GridLayoutManager(this, ScheduleCardUtils.EDITSUM);
        mClassEditRecyclerView.setLayoutManager(layoutManager);
        mScheduleEditAdapter = new ScheduleCardAdapter(this, mEditAdapterList, myHandler);
        mClassEditRecyclerView.setAdapter(mScheduleEditAdapter);

        //根据初始化状态更新视图
        if (isFirstSet) {
            onEditStateChangedSet(true);
        } else {
            updateScheduleViewByStatu();
        }

        //设置可拖动的按钮
        ScheduleCardUtils.setCanDragButton(this, mClassEditImageView, v -> {
            if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                ToastUtil.showMyToast(ScheduleCardActivity.this, "需要管理员权限", Toast.LENGTH_LONG);
                return;
            }
            onEditStateChangedSet(true);
        });
        ScheduleCardUtils.setCanDragButton(this, mIvWeekEdit, v -> {
            if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                ToastUtil.showMyToast(ScheduleCardActivity.this, "需要管理员权限", Toast.LENGTH_LONG);
                return;
            }
            mScheduleInfo = ScheduleCardUtils.transScheInfoByCardBean(cardBean);
            Intent intent = new Intent(ScheduleCardActivity.this,
                    ScheduleWeekSettingActivity.class);
            intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
            intent.putExtra(Constants.WATCH_EID_DATA, mCurWatch.getEid());
            startActivityForResult(intent, 2);
        });
    }

    private void initActManage(Intent intent) {
        myApp = (ImibabyApp) getApplication();
        String mWatchData = intent.getStringExtra(Constants.WATCH_EID_DATA);
        if (null != mWatchData && !"".equals(mWatchData)) {
            mCurWatch = myApp.getCurUser().queryWatchDataByEid(mWatchData);
        } else {
            mCurWatch = myApp.getCurUser().getFocusWatch();
        }
        myHandler = new ScheduleCardHandler(this);
        mCardAdapterList = new ArrayList<>();
        mEditAdapterList = new ArrayList<>();

        //1:加载本地数据
        if (mCurWatch.getEid() != null) {
            mScheduleInfo = ScheduleCardUtils.GetScheduleCardInfoByLocal(myApp, mCurWatch.getEid());
        }
        if (!"".equals(mScheduleInfo)) {
            isHasScheduleInfo = true;
        }

        //2:首次进入进行设置后，更新信息
        isFirstSet = intent.getBooleanExtra(Constants.SCHEDULE_SETTING_FIRST, false);
        if (isFirstSet) {
            //原始数据两种初始化方式：1：初次设置后的返回表，直接进入编辑状态，2：网络获取的返回信息
            mScheduleInfo = intent.getStringExtra(Constants.SCHEDULE_CARD_INFO);
            isHasScheduleInfo = true;
            //首次设置后，保存该表到服务器
            LogUtil.e("updateScheduleCardInfo --isFirstSet-- mScheduleInfo = " + mScheduleInfo);
            ScheduleCardUtils.updateScheduleCardInfo(getMyApp(), mScheduleInfo, new MsgCallback() {
                @Override
                public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                    LogUtil.e(TAG + "respMsg" + ":" + respMsg.toJSONString());
                    if (mCurWatch != null) {
                        ScheduleCardUtils.SaveScheduleInfoToLocal(myApp, mCurWatch.getEid(), mScheduleInfo);
                    }
                }
            });
        }
        //3:检查是否支持周末课程表
        isSupportWeekShow = mCurWatch.isDevice707_H01() || mCurWatch.isDevice709_H01();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != 1) return;

        //1:本地数据保存
        isFirstSet = false;
        mScheduleInfo = data.getStringExtra(Constants.SCHEDULE_CARD_INFO);
        if (mCurWatch != null) {
            ScheduleCardUtils.SaveScheduleInfoToLocal(myApp, mCurWatch.getEid(), mScheduleInfo);
        }

        if(requestCode == 1) {
            //1:周内课程表返回
            transScheduleInfoToCardBean();
        }else{
            //2：周末课程表返回
            cardBean = ScheduleCardUtils.transCardBeanByScheInfo(mScheduleInfo);
            myApp.sdcardLog("scheduleXXXX:transScheduleInfoToCardBean:"+mScheduleInfo+":"+cardBean);

            if(cardBean == null){
                cardBean = new ScheduleCardBean();
            }
            myApp.sdcardLog("scheduleXXXX:transScheduleInfoToCardBean:"+mScheduleInfo+":"+cardBean.getWeeklist());

            //3:更新周末课程Ui
            onRefeshWeekClass();
        }
    }

    //结构体数据解析
    private void transScheduleInfoToCardBean() {
        cardBean = ScheduleCardUtils.transCardBeanByScheInfo(mScheduleInfo);
        myApp.sdcardLog("scheduleXXXX:transScheduleInfoToCardBean:" + mScheduleInfo + ":" + cardBean);

        if (cardBean == null) {
            cardBean = new ScheduleCardBean();
        }
        myApp.sdcardLog("scheduleXXXX:transScheduleInfoToCardBean:" + mScheduleInfo + ":" + cardBean.getWeeklist());

        ScheduleCardUtils.transCardBeanToAdapterBean(this, cardBean, mCardAdapterList);
        //编辑列表添加逻辑 1：添加默认课程
        ScheduleCardUtils.genEditArrayToAdapterBean(this, mEditAdapterList);
        //2：添加自定义课程
        mEditAdapterList.addAll(ScheduleCardUtils.transCardBeanToEditList(cardBean));
        //3：添加操作按钮
        mEditAdapterList.add(new ScheduleCardItemBean(4, "", null,
                false, false, false));
        mEditAdapterList.add(new ScheduleCardItemBean(5, "", null,
                false, false, false));

        if (cardBean != null) {
            mScheduleCardAdapter.AddCustomList(cardBean.getCustomlist());
        }

        onEditStateChangedSet(isFirstSet);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackAction();
                break;
            case R.id.iv_menu: {
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.showMyToast(ScheduleCardActivity.this, getString(R.string.need_admin_auth), Toast.LENGTH_LONG);
                    return;
                }
                mScheduleInfo = ScheduleCardUtils.transScheInfoByCardBean(cardBean);
                Intent intent = new Intent(ScheduleCardActivity.this,
                        ScheduleSettingsActivity.class);
                intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
                intent.putExtra(Constants.WATCH_EID_DATA, mCurWatch.getEid());
                startActivityForResult(intent, 1);
            }
            break;
            case R.id.iv_commit:
                //1:修改的课表信息转化为标准表信息
                ArrayList<String> weeklist = ScheduleCardUtils.transFixInfoToStandSchedule(cardBean, mCardAdapterList);
                cardBean.setWeeklist(weeklist);
                ArrayList<String> mCustomList = ScheduleCardUtils.transCustomClassToStandSchedule(mEditAdapterList);
                cardBean.setCustomlist(mCustomList);
                cardBean.setOptype(1);
                mScheduleInfo = ScheduleCardUtils.transScheInfoByCardBean(cardBean);
                LogUtil.e("updateScheduleCardInfo --iv_commit-- mScheduleInfo = " + mScheduleInfo);
                //2:执行提交信息
                ScheduleCardUtils.updateScheduleCardInfo(getMyApp(), mScheduleInfo, new MsgCallback() {
                    @Override
                    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
                        LogUtil.e(TAG + "respMsg" + ":" + respMsg.toJSONString());
                    }
                });
                onEditStateChangedSet(false);

                break;
            case R.id.iv_class_edit:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.showMyToast(ScheduleCardActivity.this, getString(R.string.need_admin_auth), Toast.LENGTH_LONG);
                    return;
                }
                onEditStateChangedSet(true);
                break;
            case R.id.iv_class_tran:
                menuPopUpWindow.showAsDropDown(mClassTran, -280, -20, Gravity.BOTTOM);
                break;
            case R.id.edit_menu_clear:
                if (isScheduleClearMode) {
                    isScheduleClearMode = false;
                    mEditClear.setBackgroundResource(R.drawable.schedule_card_clear_0);

                    //解除擦除状态后，更新列表和编辑视图
                    onEditStateChangedSet(true);
                } else {
                    isScheduleClearMode = true;
                    mEditClear.setBackgroundResource(R.drawable.schedule_card_clear_1);
                    //进入擦除状态后，移除编辑选择状态，同时更新列表和编辑视图
                    mEditSelectItem = ScheduleCardUtils.onUpdateEditSelectOper(mCardAdapterList, mEditAdapterList, -1);
                    mScheduleEditAdapter.notifyDataSetChanged();
                    mScheduleCardAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.edit_menu_class:
                ScheduleCardUtils.onOpenEditDialog(ScheduleCardActivity.this,
                        getText(R.string.schedule_class_name).toString(),
                        getText(R.string.schedule_class_name_limit_0).toString(),
                        new CustomSelectDialogUtil.CustomDialogListener() {
                            @Override
                            public void onClick(View v, String text) {
                                if (text.length() > 12 || text.length() < 1) {
                                    ToastUtil.showMyToast(ScheduleCardActivity.this, getText(R.string.schedule_class_name_limit_0).toString(), Toast.LENGTH_SHORT);
                                } else {
                                    if (ScheduleCardUtils.checkClassNameIsSameForCard(mEditAdapterList, text)) {
                                        ToastUtil.showMyToast(ScheduleCardActivity.this, getText(R.string.schedule_class_name_limit_1).toString(), Toast.LENGTH_SHORT);
                                        return;
                                    }

                                    ScheduleCardItemBean itemBean = new ScheduleCardItemBean(3, text, null,
                                            false, false, false);
                                    mEditAdapterList.add(mEditAdapterList.size() - 2, itemBean);

                                    mScheduleEditAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                );
                break;
            case R.id.tv_now_setting:
                if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                    ToastUtil.showMyToast(ScheduleCardActivity.this, getString(R.string.need_admin_auth), Toast.LENGTH_LONG);
                    return;
                }
                //1:创建新的课表
                ScheduleCardUtils.GenNewScheduleCardInfo(cardBean, "N/A", "N/A",
                        "N/A",
                        "N/A",
                        "N/A",
                        "N/A",
                        "N/A",
                        mCurWatch.getEid(),
                        mCurWatch.getFamilyId());

                //2：进入初次设置页面
                mScheduleInfo = ScheduleCardUtils.transScheInfoByCardBean(cardBean);
                Intent intent = new Intent(ScheduleCardActivity.this,
                        ScheduleTimeSetActivity.class);
                intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
                intent.putExtra(Constants.SCHEDULE_SETTING_FIRST, true);
                startActivity(intent);

                break;
            case R.id.tv_now_setting_week:
            case R.id.iv_week_edit: {
                {
                    if (!myApp.isMeAdmin(myApp.getCurUser().getFocusWatch())) {
                        ToastUtil.showMyToast(ScheduleCardActivity.this, "需要管理员权限", Toast.LENGTH_LONG);
                        return;
                    }
                    mScheduleInfo = ScheduleCardUtils.transScheInfoByCardBean(cardBean);
                    Intent intent1 = new Intent(ScheduleCardActivity.this,
                            ScheduleWeekSettingActivity.class);
                    intent1.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
                    intent1.putExtra(Constants.WATCH_EID_DATA, mCurWatch.getEid());
                    startActivityForResult(intent1, 2);
                }
            }
            break;
        }
    }

    @Override
    public void doCallBack(JSONObject reqMsg, JSONObject respMsg) {
        int cid = (Integer) respMsg.get(CloudBridgeUtil.KEY_NAME_CID);
        switch (cid) {
            case CloudBridgeUtil.CID_SCHEDULE_DATA_GET_RESP:
                ScheduleCardUtils.hideLoading(loadingDialog);
                int mapRc = CloudBridgeUtil.getCloudMsgRC(respMsg);
                if (mapRc == CloudBridgeUtil.RC_SUCCESS) {
                    String mCardInfo = CloudBridgeUtil.getCloudMsgPL(respMsg).toJSONString();
                    myApp.sdcardLog("scheduleXXXX:" + mCardInfo);
                    if ("{}".equals(mCardInfo)) {
                        //没有获取到表格的处理
                        isHasScheduleInfo = false;
                        updateScheduleViewByStatu();
                    } else {
                        //1、更新获取数据后的视图
                        isHasScheduleInfo = true;
                        updateScheduleViewByStatu();
                        //2、保存本地数据
                        mScheduleInfo = mCardInfo;
                        if (mCurWatch != null) {
                            ScheduleCardUtils.SaveScheduleInfoToLocal(myApp, mCurWatch.getEid(), mScheduleInfo);
                        }

                        //3、数据解析
                        transScheduleInfoToCardBean();
                    }

                }
                break;
        }
    }

    private void initPopUpDialog() {
        List<MenuPopUpWindow.ItemData> list = new ArrayList<>();
        list.add(new MenuPopUpWindow.ItemData(getString(R.string.schedule_week_select_title_0),
                getResources().getDrawable(R.drawable.schedule_tran_mon),
                null,
                new MenuPopUpWindow.OnItemClickListener() {
                    @Override
                    public void onClick() {
                        mLayoutForWeekClass.setVisibility(View.GONE);
                        menuPopUpWindow.dismiss();
                    }
                }));
        list.add(new MenuPopUpWindow.ItemData(getString(R.string.schedule_week_select_title_1),
                getResources().getDrawable(R.drawable.schedule_tran_sun),
                null,
                new MenuPopUpWindow.OnItemClickListener() {
                    @Override
                    public void onClick() {
                        mLayoutForWeekClass.setVisibility(View.VISIBLE);
                        onRefeshWeekClass();

                        menuPopUpWindow.dismiss();
                    }
                }));
        menuPopUpWindow = new MenuPopUpWindow(this, list, 1);
        menuPopUpWindow.setOutsideTouchable(true);
    }

    private void onRefeshWeekClass() {
        //1:检查是否有周末课程
        ArrayList<ScheduleWeekBean> mWeekArray = ScheduleCardUtils.getScheduleWeekListByWeekList(cardBean.getOthers());
        if (mWeekArray.size() > 0) {
            mNoWeekGroup.setVisibility(View.GONE);
            mWeekInfoGroup.setVisibility(View.VISIBLE);

            //2:更新周末课程Ui
            ScheduleCardUtils.OnGenWeekClassViewBySetInfo(ScheduleCardActivity.this,
                    mLayoutForShowMoringClass, mLayoutForShowAfteringClass, mWeekArray);
        } else {
            mNoWeekGroup.setVisibility(View.VISIBLE);
            mWeekInfoGroup.setVisibility(View.GONE);
        }
    }

    private class ScheduleCardHandler extends Handler {
        private WeakReference<ScheduleCardActivity> mWeakRef;

        private ScheduleCardHandler(ScheduleCardActivity context) {
            this.mWeakRef = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ScheduleCardActivity activity = mWeakRef.get();
            if (activity != null) {
                switch (msg.what) {
                    case ScheduleCardUtils.GET_SCHEDULE_CARD_INFO:
                        break;
                    case ScheduleCardUtils.UPDATE_SCHEDULE_CARD_INFO:
                        break;
                }
            }
        }
    }
}
