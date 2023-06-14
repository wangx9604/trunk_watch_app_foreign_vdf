package com.xiaoxun.xun.focustime;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NormalActivity;
import com.xiaoxun.xun.utils.StatusBarUtil;

public class FocusTimeSmartIntroActivity extends NormalActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_time_smart_intro);
        StatusBarUtil.setFullScreenWithStatusbar(getWindow(),findViewById(R.id.layout_focustime_smart));
        StatusBarUtil.setStatusbarSeatHeight(this, findViewById(R.id.statusbar_seat), Color.TRANSPARENT);

        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}