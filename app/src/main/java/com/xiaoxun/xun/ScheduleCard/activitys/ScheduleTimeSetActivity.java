package com.xiaoxun.xun.ScheduleCard.activitys;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.ScheduleCard.Views.SimpleDecoration;
import com.xiaoxun.xun.ScheduleCard.adapters.ScheduleTimeAdapter;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleCardBean;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleCardItemBean;
import com.xiaoxun.xun.ScheduleCard.beans.ScheduleTimeBean;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.interfaces.MsgCallback;
import com.xiaoxun.xun.interfaces.OnItemClickListener;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.SelectTimeUtils;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.CustomerPickerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScheduleTimeSetActivity extends NormalActivity implements View.OnClickListener {
    private static final String TAG = ScheduleTimeSetActivity.class.getSimpleName();

    private String mScheduleInfo;
    private ImageView mBackImageView;
    private ImageView mEditModeImageView;
    private ImageView mAddTimeImageView;
    private ImageView mDeleteTimeImageView;
    private TextView mSaveTextView;
    private RecyclerView mTimeShowRecycler;
    private RelativeLayout mTimeLayout;
    private RelativeLayout mTimeLayout1;
    private ScheduleTimeAdapter mClassAdapter;

    private ArrayList<ScheduleTimeBean> mTimeArray;
    private boolean isEditMode = true;
    private boolean isFirstSet = false;
    private ScheduleCardBean cardBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_time_set);

        initActManage();
        initView();
        initAdapter();
        initData();
        initTimeListener();
        updateViewByOptState();
    }

    private void initTimeListener() {

        mClassAdapter.setmItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if(!isEditMode){
                    return;
                }
                //选择显示状态更新
                final ScheduleTimeBean itemBean = mTimeArray.get(position);
                ScheduleCardUtils.updateScheduleTimeListIsSelectState(mTimeArray, itemBean);
                mClassAdapter.notifyDataSetChanged();
                ScheduleCardUtils.onTimeSelectForSchedule(ScheduleTimeSetActivity.this, mTimeLayout,
                        itemBean.getmScheduleStartTime().substring(0, 2),
                        itemBean.getmScheduleStartTime().substring(2), itemBean.getmScheduleEndTime().substring(0, 2),
                        itemBean.getmScheduleEndTime().substring(2), new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                String StartTime = text+itemBean.getmScheduleStartTime().substring(2);
                                if(!ScheduleCardUtils.CheckClassTimeToPass(StartTime, itemBean.getmScheduleEndTime())){
                                    ToastUtil.showMyToast(ScheduleTimeSetActivity.this,
                                            getString(R.string.schedule_class_time_error_0), Toast.LENGTH_SHORT);
                                }

                                itemBean.setmScheduleStartTime(StartTime);
                                mTimeArray.remove(position);
                                mTimeArray.add(position,itemBean);
                                mClassAdapter.notifyDataSetChanged();
                            }
                        },
                        new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                String StartTime = itemBean.getmScheduleStartTime().substring(0,2)+text;

                                if(!ScheduleCardUtils.CheckClassTimeToPass(StartTime, itemBean.getmScheduleEndTime())){
                                    ToastUtil.showMyToast(ScheduleTimeSetActivity.this,
                                            getString(R.string.schedule_class_time_error_0), Toast.LENGTH_SHORT);
                                }

                                itemBean.setmScheduleStartTime(StartTime);
                                mTimeArray.remove(position);
                                mTimeArray.add(position,itemBean);
                                mClassAdapter.notifyDataSetChanged();
                            }
                        },
                        new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                String EndTime = text+itemBean.getmScheduleEndTime().substring(2);

                                if(!ScheduleCardUtils.CheckClassTimeToPass(itemBean.getmScheduleStartTime(), EndTime)){
                                    ToastUtil.showMyToast(ScheduleTimeSetActivity.this,
                                            getString(R.string.schedule_class_time_error_0), Toast.LENGTH_SHORT);
                                }

                                itemBean.setmScheduleEndTime(EndTime);
                                mTimeArray.remove(position);
                                mTimeArray.add(position,itemBean);
                                mClassAdapter.notifyDataSetChanged();
                            }
                        },
                        new CustomerPickerView.onSelectListener() {
                            @Override
                            public void onSelect(String text) {
                                String EndTime = itemBean.getmScheduleEndTime().substring(0,2)+text;

                                if(!ScheduleCardUtils.CheckClassTimeToPass(itemBean.getmScheduleStartTime(), EndTime)){
                                    ToastUtil.showMyToast(ScheduleTimeSetActivity.this,
                                            getString(R.string.schedule_class_time_error_0), Toast.LENGTH_SHORT);
                                }

                                itemBean.setmScheduleEndTime(EndTime);
                                mTimeArray.remove(position);
                                mTimeArray.add(position,itemBean);
                                mClassAdapter.notifyDataSetChanged();
                            }
                        });
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    private void initData() {
        //主表时间数据切换为具体使用数据结构
        ScheduleCardUtils.transCardBeanToTimeArray(this,cardBean,mTimeArray);

        if(isFirstSet){
            isEditMode = true;
            mSaveTextView.setText(R.string.security_zone_next);
            mEditModeImageView.setVisibility(View.GONE);
        }else{
            isEditMode = true;
            mSaveTextView.setText(R.string.save_edit);
            mSaveTextView.setVisibility(View.GONE);
        }
        updateEditModeView();
    }

    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mTimeShowRecycler.setLayoutManager(linearLayoutManager);
        SimpleDecoration mDecoration = new SimpleDecoration(16);
        mTimeShowRecycler.addItemDecoration(mDecoration);
        mClassAdapter = new ScheduleTimeAdapter(this, mTimeArray);
        mTimeShowRecycler.setAdapter(mClassAdapter);

    }

    private void initView() {
        mEditModeImageView = findViewById(R.id.iv_menu);
        mBackImageView= findViewById(R.id.iv_back);
        mTimeShowRecycler = findViewById(R.id.schedule_show_view);
        mSaveTextView = findViewById(R.id.iv_save_info);
        mTimeLayout = findViewById(R.id.layout_save);
        mTimeLayout1 = findViewById(R.id.layout_save_1);
        mAddTimeImageView = findViewById(R.id.iv_time_add);
        mDeleteTimeImageView = findViewById(R.id.iv_time_delete);

        mBackImageView.setOnClickListener(this);
        mEditModeImageView.setOnClickListener(this);
        mSaveTextView.setOnClickListener(this);
        mAddTimeImageView.setOnClickListener(this);
        mDeleteTimeImageView.setOnClickListener(this);
    }

    private void initActManage() {
        mScheduleInfo = getIntent().getStringExtra(Constants.SCHEDULE_CARD_INFO);
        isFirstSet = getIntent().getBooleanExtra(Constants.SCHEDULE_SETTING_FIRST,false);
        cardBean = ScheduleCardUtils.transCardBeanByScheInfo(mScheduleInfo);

        mTimeArray = new ArrayList<>();
    }

    private void updateEditModeView(){
        mTimeLayout1.setVisibility(View.VISIBLE);
        mTimeLayout.setVisibility(View.VISIBLE);
        mClassAdapter.notifyDataSetChanged();
        mTimeShowRecycler.scrollToPosition(mTimeArray.size()-1);
    }

    private void onBackAction(){
        if(!isFirstSet){
            ArrayList<String> mtimeList = ScheduleCardUtils.transTimeArrayToCardBean(mTimeArray);
            if(ScheduleCardUtils.onCheckClassArrayForChange(cardBean.getTimelist(),
                    mtimeList)){
                DialogUtil.ShowCustomSystemDialog(getApplicationContext(),
                        getString(R.string.prompt),
                        getString(R.string.schedule_edit_exit),
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        },
                        getText(R.string.schedule_edit_exit_yes).toString(),
                        new DialogUtil.OnCustomDialogListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        },
                        getText(R.string.schedule_edit_exit_no).toString());
            }else {
                finish();
            }
        }else{
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBackAction();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_time_add:
                if(mTimeArray.size() >= ScheduleTimeAdapter.ScheduleTimeMore){
//                    ToastUtil.showMyToast(ScheduleTimeSetActivity.this,
//                            getString(R.string.schedule_class_limit),Toast.LENGTH_SHORT);
                    return;
                }
                //添加课时前，先删除设置时间控件
                mTimeLayout.removeAllViews();

                //添加课时
                ScheduleTimeBean itemBean = ScheduleCardUtils.onGenerateClassTimeInfo(ScheduleTimeSetActivity.this
                        ,mTimeArray);
                mTimeArray.add(itemBean);
                Collections.sort(mTimeArray, new Comparator<ScheduleTimeBean>() {
                    @Override
                    public int compare(ScheduleTimeBean o1, ScheduleTimeBean o2) {
                        return o1.getmScheduleTimeId() > o2.getmScheduleTimeId()?1:-1;
                    }
                });
                ScheduleCardUtils.updateScheduleTimeListIsSelectState(mTimeArray, itemBean);
                mClassAdapter.notifyDataSetChanged();
                mTimeShowRecycler.scrollToPosition(mTimeArray.size()-1);
                updateViewByOptState();
                break;
            case R.id.iv_time_delete:
                if(mTimeArray.size() > ScheduleTimeAdapter.ScheduleTimeLower) {
                    //删除课时前，先删除设置时间控件
                    mTimeLayout.removeAllViews();

                    mTimeArray.remove(mTimeArray.size() - 1);
                    mClassAdapter.notifyDataSetChanged();
                    updateViewByOptState();
                }else{
//                    ToastUtil.showMyToast(ScheduleTimeSetActivity.this,
//                            getString(R.string.schedule_time_delete_limit),Toast.LENGTH_SHORT);
                }
                break;
            case R.id.iv_back:
                onBackAction();
                break;
            case R.id.iv_menu:
            case R.id.iv_save_info:
                //1:检查每节课是否单课程时间错误和多课程时间冲突
                if(ScheduleCardUtils.checkClassTimeConflict(
                        ScheduleTimeSetActivity.this, mTimeArray)) return;

                //2:保存数据操作
                ScheduleCardUtils.clearTimeOperItem(mTimeArray);
                cardBean.getTimelist().clear();
                cardBean.getTimelist().addAll(ScheduleCardUtils.transTimeArrayToCardBean(mTimeArray));
                ArrayList<ScheduleCardItemBean> mCardAdapterList = new ArrayList<>();
                ScheduleCardUtils.transCardBeanToAdapterBean(this, cardBean, mCardAdapterList);
                ArrayList<String> weeklist = ScheduleCardUtils.transFixInfoToStandSchedule(cardBean,mCardAdapterList);
                cardBean.setWeeklist(weeklist);
                if(isFirstSet){
                    //进入到下一步
                    mScheduleInfo = ScheduleCardUtils.transScheInfoByCardBean(cardBean);
                    Intent intent = new Intent(ScheduleTimeSetActivity.this,
                            ScheduleCardActivity.class);
                    intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
                    intent.putExtra(Constants.SCHEDULE_SETTING_FIRST, true);
                    startActivity(intent);
                }else {
                    //1:执行提交信息
                    cardBean.setOptype(1);
                    mScheduleInfo = ScheduleCardUtils.transScheInfoByCardBean(cardBean);
                    LogUtil.e("updateScheduleCardInfo --iv_save_info-- mScheduleInfo = " + mScheduleInfo);
                    ScheduleCardUtils.updateScheduleCardInfo(getMyApp(), mScheduleInfo, new MsgCallback() {
                        @Override
                        public void doCallBack(net.minidev.json.JSONObject reqMsg, net.minidev.json.JSONObject respMsg) {
                            LogUtil.e(TAG + "respMsg"+":"+respMsg.toJSONString());
                            ToastUtil.showMyToast(ScheduleTimeSetActivity.this,
                                    getString(R.string.phone_set_success), Toast.LENGTH_SHORT);
                        }
                    });
                    Intent intent = new Intent();
                    intent.putExtra(Constants.SCHEDULE_CARD_INFO, mScheduleInfo);
                    setResult(1, intent);
                    finish();
                }
                break;
        }
    }

    //更新增删操作状态变化下的view更新
    private void updateViewByOptState(){
        if(mTimeArray.size() >= ScheduleTimeAdapter.ScheduleTimeMore){
            mAddTimeImageView.setImageResource(R.drawable.schedule_time_add_0);
        }else{
            mAddTimeImageView.setImageResource(R.drawable.schedule_add_class);
        }

        if(mTimeArray.size() <= ScheduleTimeAdapter.ScheduleTimeLower){
            mDeleteTimeImageView.setImageResource(R.drawable.schedule_time_delete_0);
        }else{
            mDeleteTimeImageView.setImageResource(R.drawable.schedule_delete_class);
        }
    }
}
