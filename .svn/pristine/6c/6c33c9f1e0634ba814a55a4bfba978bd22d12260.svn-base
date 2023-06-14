package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.NoticeMsgData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.RanksStepsEntity;
import com.xiaoxun.xun.db.NoticeMsgHisDAO;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.StepsUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import java.util.ArrayList;

/**
 * Created by zhangjun on 2017/02/10.
 */
public class StepsRanksItemHolder extends RecyclerView.ViewHolder implements RecyclerItemHolder {
    private Activity mActivity;
    private LinearLayout mTimeLy;
    private LinearLayout ll_steps_ranks;
    private TextView mDate;
    private TextView mTime;
    private TextView mStepsRanksTitle;
    private ImageView iv_head_imge_no1;
    private ImageView iv_head_imge_no2;
    private ImageView iv_head_imge_no3;
    private ChatPopupWindow mMenuWindow;

    ArrayList<RanksStepsEntity> ranksList ;
    private ImibabyApp myApp;

    public StepsRanksItemHolder(View view, Activity activity) {
        super(view);
        mActivity = activity;
        myApp = (ImibabyApp) mActivity.getApplication();
        mTimeLy = view.findViewById(R.id.time_ly);
        ll_steps_ranks = view.findViewById(R.id.ll_steps_ranks);
        mDate = view.findViewById(R.id.time_txt);
        mTime = view.findViewById(R.id.time);
        mStepsRanksTitle = view.findViewById(R.id.tv_ranks_champoin);
        iv_head_imge_no1 = view.findViewById(R.id.head_imge_no1);
        iv_head_imge_no2 = view.findViewById(R.id.head_imge_no2);
        iv_head_imge_no3 = view.findViewById(R.id.head_imge_no3);
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
        ranksList = new ArrayList<>();
        StepsUtil.getDataByJsonStr(ranksList,msg.getmContent());

        int default_head_Id = R.drawable.ranks_custom;
        RanksStepsEntity entity1 = StepsUtil.getEntityFormRanksListByNum(ranksList,1);
        if(entity1 != null) {
            //设置用户图像
            String headpath = myApp.getCurUser().getHeadPathByEid(entity1.eid);
            if (myApp.getCurUser().getIsWatchByEid(entity1.eid)) {//如果是手表
                default_head_Id = R.drawable.default_head;
                mStepsRanksTitle.setText(mActivity.getString(R.string.steps_ranks_info, myApp.getCurUser().queryNicknameByEid(entity1.eid)));
                ImageUtil.setMaskImage(iv_head_imge_no1, R.drawable.rank_head_mask, myApp.getHeadDrawableByFile(mActivity.getResources(), headpath, entity1.eid, default_head_Id));
            } else {
                default_head_Id = R.drawable.ranks_custom;
                PhoneNumber phoneNumber = myApp.getPhoneNumberByEid(entity1.eid, watchId);

                MemberUserData testUser = myApp.getCurUser().queryUserDataByEid(entity1.eid);
                if (testUser != null) {
                    mStepsRanksTitle.setText(mActivity.getString(R.string.steps_ranks_info,testUser.getRelation(myApp.getCurUser().getFocusWatch().getEid())));
                } else {
                    mStepsRanksTitle.setText(mActivity.getString(R.string.steps_ranks_info,myApp.getCurUser().queryNicknameByEid(entity1.eid)));
                }
                // 用户头像
                final ImageView ivAvatar = iv_head_imge_no1;
                if (phoneNumber != null && phoneNumber.avatar == null) {
                    ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_head_mask,
                            myApp.getHeadDrawableByFile(mActivity.getResources(), Integer.toString(phoneNumber.attri), entity1.eid, R.drawable.relation_custom));
                } else if (phoneNumber != null) {
                    Bitmap headBitmap = new ImageDownloadHelper(mActivity).downloadImage(phoneNumber.avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                        @Override
                        public void onImageDownload(String url, Bitmap bitmap) {
                            Drawable headDrawable = new BitmapDrawable(mActivity.getResources(), bitmap);
                            ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_head_mask, headDrawable);
                        }
                    });
                    if (headBitmap != null) {
                        Drawable headDrawable = new BitmapDrawable(mActivity.getResources(), headBitmap);
                        ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_head_mask, headDrawable);
                    }
                } else {
                    ivAvatar.setImageResource(default_head_Id);
                }
            }
        }else{
            ImageUtil.setMaskImage(iv_head_imge_no1, R.drawable.rank_head_mask, myApp.getHeadDrawableByFile(mActivity.getResources(), null, null, default_head_Id));
        }
        default_head_Id = R.drawable.ranks_custom;
        RanksStepsEntity entity2 = StepsUtil.getEntityFormRanksListByNum(ranksList,2);
        if(entity2 != null){
            //设置用户图像
            String headpath  = myApp.getCurUser().getHeadPathByEid(entity2.eid);
            if (myApp.getCurUser().getIsWatchByEid(entity2.eid)) {//如果是手表
                default_head_Id = R.drawable.default_head;
                ImageUtil.setMaskImage(iv_head_imge_no2, R.drawable.rank_other_head, myApp.getHeadDrawableByFile(mActivity.getResources(), headpath, entity2.eid, default_head_Id));
            }else{
                default_head_Id = R.drawable.ranks_custom;
                PhoneNumber phoneNumber = myApp.getPhoneNumberByEid(entity2.eid, watchId);
                // 用户头像
                final ImageView ivAvatar = iv_head_imge_no2;
                if (phoneNumber != null && phoneNumber.avatar == null) {
                    ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_other_head,
                            myApp.getHeadDrawableByFile(mActivity.getResources(), Integer.toString(phoneNumber.attri), entity2.eid, R.drawable.relation_custom));
                } else if (phoneNumber != null) {
                    Bitmap headBitmap = new ImageDownloadHelper(mActivity).downloadImage(phoneNumber.avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                        @Override
                        public void onImageDownload(String url, Bitmap bitmap) {
                            Drawable headDrawable = new BitmapDrawable(mActivity.getResources(), bitmap);
                            ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_other_head, headDrawable);
                        }
                    });
                    if (headBitmap != null) {
                        Drawable headDrawable = new BitmapDrawable(mActivity.getResources(), headBitmap);
                        ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_other_head, headDrawable);
                    }
                } else {
                    ivAvatar.setImageResource(default_head_Id);
                }
            }
        }else{
            ImageUtil.setMaskImage(iv_head_imge_no2, R.drawable.rank_other_head, myApp.getHeadDrawableByFile(mActivity.getResources(), null, null, default_head_Id));
        }
        default_head_Id = R.drawable.ranks_custom;
        RanksStepsEntity entity3 = StepsUtil.getEntityFormRanksListByNum(ranksList,3);
        if(entity3 != null){
            //设置用户图像
            String headpath  = myApp.getCurUser().getHeadPathByEid(entity3.eid);
            if (myApp.getCurUser().getIsWatchByEid(entity3.eid)) {//如果是手表
                default_head_Id = R.drawable.default_head;
                ImageUtil.setMaskImage(iv_head_imge_no3, R.drawable.rank_other_head, myApp.getHeadDrawableByFile(mActivity.getResources(), headpath, entity3.eid, default_head_Id));
            }else{
                default_head_Id = R.drawable.ranks_custom;
                PhoneNumber phoneNumber = myApp.getPhoneNumberByEid(entity3.eid, watchId);
                // 用户头像
                final ImageView ivAvatar = iv_head_imge_no3;
                if (phoneNumber != null && phoneNumber.avatar == null) {
                    ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_other_head,
                            myApp.getHeadDrawableByFile(mActivity.getResources(), Integer.toString(phoneNumber.attri), entity3.eid, R.drawable.relation_custom));
                } else if (phoneNumber != null) {
                    Bitmap headBitmap = new ImageDownloadHelper(mActivity).downloadImage(phoneNumber.avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                        @Override
                        public void onImageDownload(String url, Bitmap bitmap) {
                            Drawable headDrawable = new BitmapDrawable(mActivity.getResources(), bitmap);
                            ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_other_head, headDrawable);
                        }
                    });
                    if (headBitmap != null) {
                        Drawable headDrawable = new BitmapDrawable(mActivity.getResources(), headBitmap);
                        ImageUtil.setMaskImage(ivAvatar, R.drawable.rank_other_head, headDrawable);
                    }
                } else {
                    ivAvatar.setImageResource(default_head_Id);
                }
            }
        }else{
            ImageUtil.setMaskImage(iv_head_imge_no3, R.drawable.rank_other_head, myApp.getHeadDrawableByFile(mActivity.getResources(), null, null, default_head_Id));
        }

        ll_steps_ranks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ll_steps_ranks.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMenuWindow = new ChatPopupWindow(mActivity, new View.OnClickListener() {

                    public void onClick(View v) {
                        mMenuWindow.dismiss();
                        msgData.setmStatus(NoticeMsgData.MSG_STATUS_DELETE);
                        NoticeMsgHisDAO.getInstance(mActivity.getApplicationContext()).updateNoticeMsg(msgData.getmGroupid(),myApp.getCurUser().getEid() ,msgData, msgData.getmTimeStamp());
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
                                        NoticeMsgHisDAO.getInstance(mActivity.getApplicationContext()).deleteAllMsg(msgData.getmGroupid(), myApp.getCurUser().getEid());
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
