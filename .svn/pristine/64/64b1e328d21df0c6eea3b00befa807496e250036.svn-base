package com.xiaoxun.xun.securityarea.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;

public class LeaveSchoolDaySetActivity extends NormalActivity implements View.OnClickListener {
    private ImageButton mBtnBack;
    private Button btnSave;

    private ImageView iv_week_1;
    private ImageView iv_week_2;
    private ImageView iv_week_3;
    private ImageView iv_week_4;
    private ImageView iv_week_5;
    private ImageView iv_week_6;
    private ImageView iv_week_7;

    private ConstraintLayout cl_mon;
    private ConstraintLayout cl_tues;
    private ConstraintLayout cl_wed;
    private ConstraintLayout cl_thus;
    private ConstraintLayout cl_fri;
    private ConstraintLayout cl_sat;
    private ConstraintLayout cl_sun;

    private String mDays;//已被选择的天数
    private String day;//当前item选择的天数
    private String[] mDayArray = new String[7];
    private String[] dayArray = new String[7];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_school_day_set);
        mDays = getIntent().getStringExtra("mDays");//总
        day = getIntent().getStringExtra("day");//
        initViews();
        initListener();
        initData();
    }

    private void initViews() {
        mBtnBack = findViewById(R.id.iv_title_back);
        btnSave = findViewById(R.id.btn_save);
        iv_week_1 = findViewById(R.id.iv_week_1);
        iv_week_2 = findViewById(R.id.iv_week_2);
        iv_week_3 = findViewById(R.id.iv_week_3);
        iv_week_4 = findViewById(R.id.iv_week_4);
        iv_week_5 = findViewById(R.id.iv_week_5);
        iv_week_6 = findViewById(R.id.iv_week_6);
        iv_week_7 = findViewById(R.id.iv_week_7);
        cl_mon = findViewById(R.id.cl_mon);
        cl_tues = findViewById(R.id.cl_tues);
        cl_wed = findViewById(R.id.cl_wed);
        cl_thus = findViewById(R.id.cl_thus);
        cl_fri = findViewById(R.id.cl_fri);
        cl_sat = findViewById(R.id.cl_sat);
        cl_sun = findViewById(R.id.cl_sun);
    }

    private void initData() {
        for (int i = 0; i < 7; i++) {
            mDayArray[i] = mDays.substring(i,i+1);
            dayArray[i] = day.substring(i,i+1);
            if(mDayArray[i].equals("0")){//置空
                setChooseStatus(i,0);
            }else if(mDayArray[i].equals("1")&&dayArray[i].equals("1")){//亮
                setChooseStatus(i,1);
            }else if(mDayArray[i].equals("1")&&dayArray[i].equals("0")){//暗
                setChooseStatus(i,2);            }
        }
    }
    private void initListener() {
        mBtnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        cl_mon.setOnClickListener(this);
        cl_tues.setOnClickListener(this);
        cl_wed.setOnClickListener(this);
        cl_thus.setOnClickListener(this);
        cl_fri.setOnClickListener(this);
        cl_sat.setOnClickListener(this);
        cl_sun.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mBtnBack){
            finish();
        }else if(v == btnSave){
            Toast.makeText(getApplicationContext(), getString(R.string.save_successs), Toast.LENGTH_SHORT).show();
            day = dayArray[0] + dayArray[1]+dayArray[2]+dayArray[3]+dayArray[4]+dayArray[5]+dayArray[6];
            mDays = mDayArray[0]+mDayArray[1]+mDayArray[2]+mDayArray[3]+mDayArray[4]+mDayArray[5]+mDayArray[6];
            Intent  intent = getIntent();
            intent.putExtra("day",day);
            intent.putExtra("mDays",mDays);
            setResult(RESULT_OK,intent);
            finish();
        }else if(v == cl_mon){
            if(dayArray[0].equals("1")){
                iv_week_1.setBackgroundResource(R.drawable.week_choosed);
                iv_week_1.setVisibility(View.GONE);
                dayArray[0] = "0";
                mDayArray[0] = "0";
            }else {
                iv_week_1.setBackgroundResource(R.drawable.week_choose_click);
                iv_week_1.setVisibility(View.VISIBLE);
                dayArray[0] = "1";
                mDayArray[0] = "1";
            }
        }else if(v == cl_tues){
            if(dayArray[1].equals("1")){
                iv_week_2.setBackgroundResource(R.drawable.week_choosed);
                iv_week_2.setVisibility(View.GONE);
                dayArray[1] = "0";
                mDayArray[1] = "0";
            }else {
                iv_week_2.setBackgroundResource(R.drawable.week_choose_click);
                iv_week_2.setVisibility(View.VISIBLE);
                dayArray[1] = "1";
                mDayArray[1] = "1";
            }
        }else if(v == cl_wed){
            if(dayArray[2].equals("1")){
                iv_week_3.setBackgroundResource(R.drawable.week_choosed);
                iv_week_3.setVisibility(View.GONE);
                dayArray[2] = "0";
                mDayArray[2] = "0";
            }else {
                iv_week_3.setBackgroundResource(R.drawable.week_choose_click);
                iv_week_3.setVisibility(View.VISIBLE);
                dayArray[2] = "1";
                mDayArray[2] = "1";
            }
        }else if(v == cl_thus){
            if(dayArray[3].equals("1")){
                iv_week_4.setBackgroundResource(R.drawable.week_choosed);
                iv_week_4.setVisibility(View.GONE);
                dayArray[3] = "0";
                mDayArray[3] = "0";
            }else {
                iv_week_4.setBackgroundResource(R.drawable.week_choose_click);
                iv_week_4.setVisibility(View.VISIBLE);
                dayArray[3] = "1";
                mDayArray[3] = "1";
            }
        }else if(v == cl_fri){
            if(dayArray[4].equals("1")){
                iv_week_5.setBackgroundResource(R.drawable.week_choosed);
                iv_week_5.setVisibility(View.GONE);
                dayArray[4] = "0";
                mDayArray[4] = "0";
            }else {
                iv_week_5.setBackgroundResource(R.drawable.week_choose_click);
                iv_week_5.setVisibility(View.VISIBLE);
                dayArray[4] = "1";
                mDayArray[4] = "1";
            }
        }else if(v == cl_sat){
            if(dayArray[5].equals("1")){
                iv_week_6.setBackgroundResource(R.drawable.week_choosed);
                iv_week_6.setVisibility(View.GONE);
                dayArray[5] = "0";
                mDayArray[5] = "0";
            }else {
                iv_week_6.setBackgroundResource(R.drawable.week_choose_click);
                iv_week_6.setVisibility(View.VISIBLE);
                dayArray[5] = "1";
                mDayArray[5] = "1";
            }
        }else if(v == cl_sun){
            if(dayArray[6].equals("1")){
                iv_week_7.setBackgroundResource(R.drawable.week_choosed);
                iv_week_7.setVisibility(View.GONE);
                dayArray[6] = "0";
                mDayArray[6] = "0";
            }else {
                iv_week_7.setBackgroundResource(R.drawable.week_choose_click);
                iv_week_7.setVisibility(View.VISIBLE);
                dayArray[6] = "1";
                mDayArray[5] = "1";
            }
        }
    }

    private void setChooseStatus(int position,int type){
        if(position == 0){
            if(type == 0){
                iv_week_1.setVisibility(View.GONE);
            }else if(type == 1){
                iv_week_1.setVisibility(View.VISIBLE);
                iv_week_1.setBackgroundResource(R.drawable.week_choose_click);
            }else if(type == 2){
                iv_week_1.setVisibility(View.VISIBLE);
                iv_week_1.setBackgroundResource(R.drawable.week_choosed);
                cl_mon.setClickable(false);
            }
        }else if(position == 1){
            if(type == 0){
                iv_week_2.setVisibility(View.GONE);
            }else if(type == 1){
                iv_week_2.setVisibility(View.VISIBLE);
                iv_week_2.setBackgroundResource(R.drawable.week_choose_click);
            }else if(type == 2){
                iv_week_2.setVisibility(View.VISIBLE);
                iv_week_2.setBackgroundResource(R.drawable.week_choosed);
                cl_tues.setClickable(false);
            }
        }else if(position == 2){
            if(type == 0){
                iv_week_3.setVisibility(View.GONE);
            }else if(type == 1){
                iv_week_3.setVisibility(View.VISIBLE);
                iv_week_3.setBackgroundResource(R.drawable.week_choose_click);
            }else if(type == 2){
                iv_week_3.setVisibility(View.VISIBLE);
                iv_week_3.setBackgroundResource(R.drawable.week_choosed);
                cl_wed.setClickable(false);
            }
        }else if(position == 3){
            if(type == 0){
                iv_week_4.setVisibility(View.GONE);
            }else if(type == 1){
                iv_week_4.setVisibility(View.VISIBLE);
                iv_week_4.setBackgroundResource(R.drawable.week_choose_click);
            }else if(type == 2){
                iv_week_4.setVisibility(View.VISIBLE);
                iv_week_4.setBackgroundResource(R.drawable.week_choosed);
                cl_thus.setClickable(false);
            }
        }else if(position == 4){
            if(type == 0){
                iv_week_5.setVisibility(View.GONE);
            }else if(type == 1){
                iv_week_5.setVisibility(View.VISIBLE);
                iv_week_5.setBackgroundResource(R.drawable.week_choose_click);
            }else if(type == 2){
                iv_week_5.setVisibility(View.VISIBLE);
                iv_week_5.setBackgroundResource(R.drawable.week_choosed);
                cl_fri.setClickable(false);
            }
        }else if(position == 5){
            if(type == 0){
                iv_week_6.setVisibility(View.GONE);
            }else if(type == 1){
                iv_week_6.setVisibility(View.VISIBLE);
                iv_week_6.setBackgroundResource(R.drawable.week_choose_click);
            }else if(type == 2){
                iv_week_6.setVisibility(View.VISIBLE);
                iv_week_6.setBackgroundResource(R.drawable.week_choosed);
                cl_sat.setClickable(false);
            }
        }else if(position == 6){
            if(type == 0){
                iv_week_7.setVisibility(View.GONE);
            }else if(type == 1){
                iv_week_7.setVisibility(View.VISIBLE);
                iv_week_7.setBackgroundResource(R.drawable.week_choose_click);
            }else if(type == 2){
                iv_week_7.setVisibility(View.VISIBLE);
                iv_week_7.setBackgroundResource(R.drawable.week_choosed);
                cl_sun.setClickable(false);
            }
        }
    }
}