/**
 * Creation Date:2015-2-3
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;

import java.io.FileDescriptor;
import java.io.IOException;

@SuppressLint("NewApi")
public class AlarmClockBellActivity extends NormalActivity implements OnClickListener {

    private ImageButton btnCancel;
    private Button btnConfirm;
    private ListView listSelectBell;
    private SelectBellAdapter bellAdapter;

    private MediaPlayer mp2;
    private AssetManager am;
    private int iBellItemSelect = 1;    //注意：协议中是从1开始的，而position是从0开始的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarmclock_bell);
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.bell_setting);

        initMediaPlayer();
        initViews();
        initData();
        initListener();
    }

    private void initMediaPlayer() {

        am = getAssets();
        mp2 = new MediaPlayer();
    }

    private void initViews() {

        btnCancel = findViewById(R.id.iv_title_back);
        btnConfirm = findViewById(R.id.confirm);
        listSelectBell = findViewById(R.id.listview_slect_bell);
    }

    private void initData() {

        SparseArray<String> bellArray = new SparseArray<>();
        bellArray.put(0, getString(R.string.alarm_bell_1));
        bellArray.put(1, getString(R.string.alarm_bell_2));
        bellArray.put(2, getString(R.string.alarm_bell_3));
        bellAdapter = new SelectBellAdapter(this, bellArray);
        listSelectBell.setAdapter(bellAdapter);

        Intent intent = this.getIntent();
        iBellItemSelect = intent.getIntExtra(Const.KEY_EXTRA_BELLSELECT, 1);
        bellAdapter.selectItem(iBellItemSelect - 1);
        bellAdapter.notifyDataSetChanged();
    }

    private void initListener() {

        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        listSelectBell.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iBellItemSelect = position;
                bellAdapter.selectItem(position);
                bellAdapter.notifyDataSetChanged();
                playMusic(position);
            }
        });
    }

    private void playMusic(int select) {

        try {
            AssetFileDescriptor afd = am.openFd("music/bell1.mp3");
            FileDescriptor fd;

            if (select == 0)
                afd = am.openFd("music/bell1.mp3");
            else if (select == 1)
                afd = am.openFd("music/bell2.mp3");
            else if (select == 2)
                afd = am.openFd("music/bell3.mp3");
            fd = afd.getFileDescriptor();

            mp2.reset();
            mp2.setDataSource(fd, afd.getStartOffset(), afd.getLength());
            mp2.prepare();
            mp2.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        if (btnCancel == v) {
            finish();
        } else if (btnConfirm == v) {
            Intent intent = new Intent();
            intent.putExtra(Const.KEY_EXTRA_BELLSELECT, iBellItemSelect + 1);
            setResult(3, intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp2.isPlaying()) {
            mp2.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp2.isPlaying()) {
            mp2.stop();
        }
    }

    @SuppressWarnings("deprecation")
    class SelectBellAdapter extends BaseAdapter {

        final Context context;
        final SparseArray<String> bellArray;
        int selectPosition;

        public SelectBellAdapter(Context context, SparseArray<String> bellArray) {
            this.context = context;
            this.bellArray = bellArray;
        }

        @Override
        public int getCount() {
            return bellArray.size();
        }

        @Override
        public Object getItem(int position) {
            return bellArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder vh;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_alarm_selectbell, null);
                vh = new ViewHolder();
                vh.bellName = convertView.findViewById(R.id.bell_name);
                vh.selectView = convertView.findViewById(R.id.select_img);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            vh.bellName.setText(bellArray.get(position));
            if (selectPosition == position) {
                vh.bellName.setTextColor(AlarmClockBellActivity.this.getResources().getColor(R.color.red_color));
                vh.selectView.setVisibility(View.VISIBLE);
            } else {
                vh.bellName.setTextColor(AlarmClockBellActivity.this.getResources().getColor(R.color.dark_grey));
                vh.selectView.setVisibility(View.GONE);
            }

            return convertView;
        }

        private void selectItem(int position) {
            this.selectPosition = position;
        }
    }

    private class ViewHolder {

        TextView bellName;
        ImageView selectView;
    }
}



