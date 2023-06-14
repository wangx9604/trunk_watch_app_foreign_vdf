package com.xiaoxun.xun.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.views.RoundProgressBar;

public class StepsTargetActivity extends NormalActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    private TextView tv_steps_value;
    private TextView tv_head_title;
    private ImageView tv_head_back;
    private RoundProgressBar roundProgressBar;
    private SeekBar  targetStepsBar;
    private Button   bt_target_steps_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_target);

        initView();
        getTargetDataForLocal();
    }

    private void initView(){
        tv_head_title = findViewById(R.id.tv_title);
        tv_head_title.setText(getString(R.string.steps_control_target_step));
        tv_head_back = findViewById(R.id.iv_title_back);
        tv_head_back.setOnClickListener(this);
        tv_steps_value = findViewById(R.id.steps_value);
        targetStepsBar = findViewById(R.id.seek_radius_level);
        targetStepsBar.setOnSeekBarChangeListener(this);
        roundProgressBar = findViewById(R.id.round_progressbar_value);

        bt_target_steps_finish = findViewById(R.id.target_steps_finish);
        bt_target_steps_finish.setOnClickListener(this);
    }
    /**
    * 类名称：StepsTargetActivity
    * 创建人：zhangjun5
    * 创建时间：2016/3/10 13:41
    * 方法描述：从本地获取到计划步数
    */
    private void getTargetDataForLocal(){
        String steps_target_level = myApp.getStringValue(myApp.getCurUser().getFocusWatch().getEid()
                + CloudBridgeUtil.STEPS_TARGET_LEVEL, "0");
        if(steps_target_level == null || steps_target_level.equals("0")){
            targetStepsBar.setProgress(6);
            roundProgressBar.setProgress(8);
            tv_steps_value.setText(Integer.toString(targetStepsBar.getProgress() * 1000 + 2000));
        }else{
            int steps_target = Integer.valueOf(steps_target_level);
            targetStepsBar.setProgress((steps_target-2000)/1000);
            roundProgressBar.setProgress((steps_target)/1000);
            tv_steps_value.setText(steps_target_level);
        }
        tv_steps_value.invalidate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            roundProgressBar.setProgress(progress+2);
            tv_steps_value.setText(Integer.toString(progress*1000+2000));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.iv_title_back:
                finish();
                break;
            case R.id.target_steps_finish:
                Intent intent = new Intent();
                int targetStepsData = targetStepsBar.getProgress()*1000 + 2000;
                intent.putExtra("targetsteps", targetStepsData);
                setResult(1,intent);
                finish();
                break;
        }
    }
}
