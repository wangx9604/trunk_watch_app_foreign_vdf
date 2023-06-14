package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.ChatPopupWindow;
import com.xiaoxun.xun.activitys.SecurityWarnningGoogleActivity;
import com.xiaoxun.xun.beans.NoticeMsgData;
import com.xiaoxun.xun.db.NoticeMsgHisDAO;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

/**
 * Created by huangqilin on 2016/7/25.
 */
public class SosLocationItemHolder  extends RecyclerView.ViewHolder implements RecyclerItemHolder{
    private Activity mActivity;
    private LinearLayout mTimeLy;
    private TextView mDate;
    private TextView mTime;
    private TextView mSafeTitle;
    private TextView mSafeLocation;
    private ImageView mClickView;
    private ChatPopupWindow mMenuWindow;
    public SosLocationItemHolder(View view,Activity activity) {
        super(view);
        mActivity = activity;
        mTimeLy = view.findViewById(R.id.time_ly);
        mDate = view.findViewById(R.id.time_txt);
        mTime = view.findViewById(R.id.time);
        mSafeTitle = view.findViewById(R.id.sos_item_txt);
        mSafeLocation = view.findViewById(R.id.sos_location_txt);
        mClickView = view.findViewById(R.id.icon_content);
    }

    @Override
    public void bindTo(NoticeMsgData msg, String time) {
        final NoticeMsgData msgData = msg;
        final String watchId = msg.getmSrcid();
        if(time.equals(Const.DEFAULT_NEXT_KEY)
                || !TimeUtil.getDay(msg.getmTimeStamp()).equals(TimeUtil.getDay(time))){ //显示日期
            mTimeLy.setVisibility(View.VISIBLE);
            mDate.setText(TimeUtil.getDay(msg.getmTimeStamp()));
        }else {
            mTimeLy.setVisibility(View.GONE);
        }
        mTime.setText(TimeUtil.getTimeHHMM(msg.getmTimeStamp()));
        //设置位置信息
        JSONObject loc = (JSONObject) JSONValue.parse(msg.getmContent());

        mSafeTitle.setText(mActivity.getString(R.string.ask_for_help, ((ImibabyApp)mActivity.getApplication()).getCurUser().queryNicknameByEid(msg.getmSrcid())));
        mSafeLocation.setText((String) ((JSONObject) loc.get("Location")).get("desc"));
        mClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mActivity, SecurityWarnningGoogleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(NoticeMsgData.PARCE_KEY, msgData);
                it.putExtras(bundle);
                mActivity.startActivity(it);
            }
        });
        mClickView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMenuWindow = new ChatPopupWindow(mActivity, new View.OnClickListener() {

                    public void onClick(View v) {
                        mMenuWindow.dismiss();
                        msgData.setmStatus(NoticeMsgData.MSG_STATUS_DELETE);
                        NoticeMsgHisDAO.getInstance(mActivity.getApplicationContext()).updateNoticeMsg(msgData.getmGroupid(),((ImibabyApp)mActivity.getApplication()).getCurUser().getEid() ,msgData, msgData.getmTimeStamp());
                        Intent intent = new Intent(Const.ACTION_CLEAR_NOTICE_MESSAGE);
                        intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.noticeMsgTypeToNoticeType(msgData.getmType()));
                        mActivity.sendBroadcast(intent);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMenuWindow.dismiss();
                        DialogUtil.CustomALertDialog(mActivity, mActivity.getString(R.string.clear_messge_title),
                                mActivity.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }, mActivity.getString(R.string.cancel),
                                new DialogUtil.OnCustomDialogListener() {
                                    @Override
                                    public void onClick(View v) {
                                        NoticeMsgHisDAO.getInstance(mActivity.getApplicationContext()).deleteAllMsg(msgData.getmGroupid(), ((ImibabyApp)mActivity.getApplication()).getCurUser().getEid());
                                        Intent intent = new Intent(Const.ACTION_CLEAR_NOTICE_MESSAGE);
                                        intent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_ALL);
                                        mActivity.sendBroadcast(intent);
                                    }
                                }, mActivity.getString(R.string.confirm)).show();

                    }
                }, null, null);
                //显示窗口
                mMenuWindow.showAtLocation(mActivity.findViewById(R.id.notice_message_layout), Gravity.CENTER, 0, 0);
                return false;
            }
        });
    }
}
