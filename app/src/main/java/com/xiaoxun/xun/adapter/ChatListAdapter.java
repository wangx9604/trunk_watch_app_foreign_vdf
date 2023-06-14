/**
 * @time 2015-1-27
 */
package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.ChatPopupWindow;
import com.xiaoxun.xun.activitys.ImageDisplayActivity;
import com.xiaoxun.xun.activitys.VideoDisplayActivity;
import com.xiaoxun.xun.beans.ChatMsgEntity;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MemberUserData;
import com.xiaoxun.xun.beans.PhoneNumber;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.db.ChatHisDao;
import com.xiaoxun.xun.interfaces.OnImageDownload;
import com.xiaoxun.xun.utils.AESUtil;
import com.xiaoxun.xun.utils.DensityUtil;
import com.xiaoxun.xun.utils.DialogUtil;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.MyMediaPlayerUtil;
import com.xiaoxun.xun.utils.TimeUtil;
import com.xiaoxun.xun.utils.ToastUtil;
import com.xiaoxun.xun.views.HttpTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * * Description Of The Class<br>
 *
 * @author huangqilin
 * @time 2015-1-27
 */
public class ChatListAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;
    private ArrayList<ChatMsgEntity> mData = null;
    private Activity mContext;
    private ChatPopupWindow menuWindow;
    private ImibabyApp mApp;
    private int mType;
    private WatchData mWatch = null;

    public ChatListAdapter(Activity mContext, LayoutInflater mInflater, ArrayList<ChatMsgEntity> mData, int mType) {
        this.mContext = mContext;
        this.mInflater = mInflater;
        this.mData = mData;
        this.mApp = (ImibabyApp) mContext.getApplication();
        this.mType = mType;
        if (mData != null) {
            for (ChatMsgEntity chat : mData) {
                chat.setmSelectFlag(false);
            }
        }
    }

    public ChatListAdapter(Activity mContext, LayoutInflater mInflater, ArrayList<ChatMsgEntity> mData, int mType, WatchData watch) {
        this.mContext = mContext;
        this.mInflater = mInflater;
        this.mData = mData;
        this.mApp = (ImibabyApp) mContext.getApplication();
        this.mType = mType;
        if (mData != null) {
            for (ChatMsgEntity chat : mData) {
                chat.setmSelectFlag(false);
            }
        }
        this.mWatch = watch;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mData.get(position);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ChatMsgEntity chat;
        final ViewHolder viewHolder;
        AnimationDrawable anim;
        chat = mData.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.chat_list_item, null);
            viewHolder.mNickname = convertView.findViewById(R.id.chat_nickname);
            viewHolder.mSendTimeLayout = convertView.findViewById(R.id.send_time);
            viewHolder.mSendTime = convertView.findViewById(R.id.chat_send_time);
            viewHolder.mLeftAnim = convertView.findViewById(R.id.left_anim);
            viewHolder.mLeftHead = convertView.findViewById(R.id.left_head);
            viewHolder.mLeftContent = convertView.findViewById(R.id.left_chat_content);
            viewHolder.mLeftDurationTime = convertView.findViewById(R.id.left_duration);
            viewHolder.mLeftRetry = convertView.findViewById(R.id.left_retry);
            viewHolder.mLeftNotify = convertView.findViewById(R.id.left_notify_text);
            viewHolder.mRightHead = convertView.findViewById(R.id.right_head);
            viewHolder.mRightContent = convertView.findViewById(R.id.right_chat_content);
            viewHolder.mRightAnim = convertView.findViewById(R.id.right_anim);
            viewHolder.mRightRetry = convertView.findViewById(R.id.right_retry);
            viewHolder.mRightWaiting = convertView.findViewById(R.id.right_waiting);
            viewHolder.mRightDurationTime = convertView.findViewById(R.id.right_duration);
            viewHolder.mNotifyLocation = convertView.findViewById(R.id.notify_location);
            viewHolder.mNotifyInfo = convertView.findViewById(R.id.notify_info);
            viewHolder.mLeftTextContent = convertView.findViewById(R.id.tv_left_chat_content);
            viewHolder.mRightTextContent = convertView.findViewById(R.id.tv_right_chat_content);
            viewHolder.mLeftImageContent = convertView.findViewById(R.id.iv_left_image_content);
            viewHolder.mInterval = convertView.findViewById(R.id.interval_view);
            viewHolder.mLeftImagePlay = convertView.findViewById(R.id.iv_left_image_play);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mIsFrom = chat.getmIsFrom();
        setSendTime(viewHolder, position, chat); //设置发送时间
        Boolean existsflag = false;
        if (chat.getmAudioPath() != null) {
            existsflag = new File(chat.getmAudioPath()).exists();
        }
        setViewVisibility(chat, viewHolder); //设置view的整体显示状态
        switch (chat.getmType()) {
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE:
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_SOS:
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_RECORD:
                if (chat.getmIsFrom()) {
                    final String watchid;
                    if (mApp.getCurUser().getIsWatchByEid(chat.getmSrcId())) {
                        watchid = chat.getmSrcId();
                    } else {
                        if (mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId()) != null) {
                            watchid = mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId()).getWatchlist().get(0).getEid();
                        } else {
                            break;
                        }
                    }

                    if (chat.getmSrcId().equals(watchid)) {
                        viewHolder.mNickname.setText(mApp.getCurUser().queryNicknameByEid(chat.getmSrcId()));
                    } else {
                        PhoneNumber phoneNumber = mApp.getPhoneNumberByEid(chat.getmSrcId(), watchid);
                        if (phoneNumber != null && phoneNumber.nickname != null && phoneNumber.nickname.length() > 0) {
                            viewHolder.mNickname.setText(phoneNumber.nickname);
                        } else {
                            viewHolder.mNickname.setText(R.string.default_relation_text);
                        }
                    }
                    //设置用户图像
                    if (mApp.getCurUser().getIsWatchByEid(chat.getmSrcId())) {//如果是手表
                        String headpath = mApp.getCurUser().getHeadPathByEid(chat.getmSrcId());
                        ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                                mApp.getHeadDrawableByFile(mContext.getResources(), headpath, chat.getmSrcId(), R.drawable.default_head));
                    } else {
                        int attri = mApp.getUserAttriByEid(watchid, chat.getmSrcId());
                        String avatatr = mApp.getUserAvatarByEid(watchid, chat.getmSrcId());
                        final ImageView ivAvatar = viewHolder.mLeftHead;
                        if (avatatr == null) {
                            ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                                    mApp.getHeadDrawableByFile(mContext.getResources(), Integer.toString(attri), chat.getmSrcId(), R.drawable.relation_custom));
                        } else {
                            Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatatr, new ImageDownloadHelper.OnImageDownloadListener() {
                                @Override
                                public void onImageDownload(String url, Bitmap bitmap) {
                                    Drawable headDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                    ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                                }
                            });
                            if (headBitmap != null) {
                                Drawable headDrawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                                ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_2, headDrawable);
                            }
                        }
                    }
                    viewHolder.mLeftHead.setTag(chat);
                    viewHolder.mLeftHead.setOnClickListener(head_click);

                    LayoutParams pl = viewHolder.mLeftContent.getLayoutParams(); //根据语音长度设置content view的宽度
                    pl.width = DensityUtil.dip2px(mContext.getApplicationContext(), chat.getmDuration() * 5 + 60);
                    viewHolder.mLeftContent.setLayoutParams(pl);
                    viewHolder.mLeftContent.invalidate();

                    viewHolder.mLeftTextContent.setVisibility(View.GONE);
                    viewHolder.mLeftImageContent.setVisibility(View.GONE);
                    viewHolder.mLeftImagePlay.setVisibility(View.GONE);
                    viewHolder.mLeftDurationTime.setText(chat.getmDuration() + "\""); //设置语音时长
                    if (mType != 2) {
                        viewHolder.mLeftContent.setOnLongClickListener(new OnLongClickListener() {

                            public boolean onLongClick(View v) {
                                // TODO Auto-generated method stub
                                mApp.hideKeyboard(viewHolder.mLeftContent);
                                for (ChatMsgEntity msg : mData) {
                                    if (msg.getmPlayAnimation() != null) {
                                        msg.getmPlayAnimation().stop();
                                        msg.getmPlayAnimation().selectDrawable(0);
                                        msg.setmIsClick(false);
                                    }
                                }
                                MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        File recordFile = new File(chat.getmAudioPath());
                                        if (recordFile.exists()) {
                                            recordFile.delete();
                                        }
                                        chat.setmSended(4);
                                        mData.remove(chat);
                                        ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                        notifyDataSetChanged();
                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                    }
                                }, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                mContext.getString(R.string.clear_messge_title), new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                }, mContext.getString(R.string.cancel),
                                                new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        File recordFile = null;
                                                        String familyid = chat.getmFamilyId();
                                                        ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                        for (ChatMsgEntity chatmsg : mData) {
                                                            if (chatmsg.getmAudioPath() != null) {
                                                                recordFile = new File(chatmsg.getmAudioPath());
                                                                if (recordFile.exists()) {
                                                                    recordFile.delete();
                                                                }
                                                            }
                                                        }
                                                        mData.clear();
                                                        notifyDataSetChanged();
                                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                    }
                                                }, mContext.getString(R.string.confirm)).show();

                                    }
                                }, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        menuWindow.dismiss();

                                        try {
                                            FileInputStream fileInputStream = new FileInputStream(chat.getmAudioPath());
                                            int length = fileInputStream.available();
                                            byte[] buffer = new byte[length];
                                            fileInputStream.read(buffer);
                                            fileInputStream.close();
                                            byte[] tmp = AESUtil.getInstance().decrypt(buffer);
                                            StringBuilder filename = new StringBuilder();
                                            if (mWatch != null) {
                                                filename.append(mWatch.getNickname());
                                            } else {
                                                filename.append(mApp.getCurUser().queryNicknameByEid(watchid));
                                            }
                                            filename.append("_").append(TimeUtil.getTimeStampFromUTC(TimeUtil.getMillisByTime(chat.getmDate()))).append(".amr");
                                            File file = new File(ImibabyApp.getMyChat(), filename.toString());
                                            FileOutputStream fos = new FileOutputStream(file);
                                            fos.write(tmp);
                                            fos.close();
                                            ToastUtil.show(mContext, file.getAbsolutePath());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                //显示窗口
                                menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                return false;
                            }
                        });
                    }
                    if (!existsflag) {
                        viewHolder.mLeftContent.setBackgroundResource(R.drawable.dialog_box_1);
                        viewHolder.mLeftContent.setOnClickListener(null);
                        viewHolder.mLeftAnim.setVisibility(View.GONE);
                        viewHolder.mLeftRetry.setVisibility(View.GONE);
                    } else {
                        //设置消息类型
                        switch (chat.getmType()) {
                            case ChatMsgEntity.CHAT_MESSAGE_TYPE_VOICE:
                                viewHolder.mLeftAnim.setBackgroundResource(R.drawable.animation_chat_left);
                                anim = (AnimationDrawable) viewHolder.mLeftAnim.getBackground();
                                anim.stop();
                                anim.selectDrawable(0);
                                //设未读标志
                                if (!chat.getmPlayed()) {
                                    viewHolder.mLeftRetry.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.mLeftRetry.setVisibility(View.GONE);
                                }
                                //监控content view点击事件
                                viewHolder.mLeftContent.setTag(R.id.view_tag_first__, chat);
                                viewHolder.mLeftContent.setTag(R.id.view_tag_second__, viewHolder);
                                viewHolder.mLeftContent.setOnClickListener(content_click);
                                break;
                            case ChatMsgEntity.CHAT_MESSAGE_TYPE_SOS:
                            case ChatMsgEntity.CHAT_MESSAGE_TYPE_RECORD:
                                //设未读标志
                                if (!chat.getmPlayed()) {
                                    viewHolder.mLeftRetry.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.mLeftRetry.setVisibility(View.GONE);
                                }
                                viewHolder.mLeftAnim.setBackgroundResource(R.drawable.animation_force_record);
                                anim = (AnimationDrawable) viewHolder.mLeftAnim.getBackground();
                                anim.stop();
                                anim.selectDrawable(0);
                                //监控content view点击事件
                                viewHolder.mLeftContent.setTag(R.id.view_tag_first__, chat);
                                viewHolder.mLeftContent.setTag(R.id.view_tag_second__, viewHolder);
                                viewHolder.mLeftContent.setOnClickListener(content_click);
                                break;
                            default:
                                break;
                        }
                    }
                    chat.setmLeftRetry(viewHolder.mLeftRetry);
                    //初始化播放动画
                    AnimationDrawable animDra;
                    animDra = (AnimationDrawable) viewHolder.mLeftAnim.getBackground();
                    animDra.selectDrawable(0);
                    chat.setmPlayAnimation(animDra);
                } else {
                    //设置用户图像
                    int attri = mApp.getUserAttriByEid(mWatch.getEid(), chat.getmSrcId());
                    String avatatr = mApp.getUserAvatarByEid(mWatch.getEid(), chat.getmSrcId());
                    final ImageView ivAvatar = viewHolder.mRightHead;
                    if (avatatr == null) {
                        ImageUtil.setMaskImage(viewHolder.mRightHead, R.drawable.head_1, mApp.getHeadDrawableByFile(mContext.getResources(), Integer.toString(attri), chat.getmSrcId(), R.drawable.relation_custom));
                    } else {
                        Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatatr, new ImageDownloadHelper.OnImageDownloadListener() {
                            @Override
                            public void onImageDownload(String url, Bitmap bitmap) {
                                Drawable headDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                            }
                        });
                        if (headBitmap != null) {
                            Drawable headDrawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                            ImageUtil.setMaskImage(viewHolder.mRightHead, R.drawable.head_2, headDrawable);
                        }
                    }
                    viewHolder.mRightHead.setTag(chat);
                    viewHolder.mRightHead.setOnClickListener(head_click);

                    LayoutParams pl = viewHolder.mRightContent.getLayoutParams(); //根据语音长度设置content view的宽度
                    pl.width = DensityUtil.dip2px(mContext.getApplicationContext(), chat.getmDuration() * 5 + 60);
                    viewHolder.mRightContent.setLayoutParams(pl);
                    viewHolder.mRightContent.invalidate();
                    viewHolder.mRightDurationTime.setText(chat.getmDuration() + "\""); //设置语音时长
                    viewHolder.mRightTextContent.setVisibility(View.GONE);

                    if (mType != 2) {
                        viewHolder.mRightContent.setOnLongClickListener(new OnLongClickListener() {

                            public boolean onLongClick(View v) {
                                // TODO Auto-generated method stub
                                mApp.hideKeyboard(viewHolder.mRightContent);
                                MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                for (ChatMsgEntity msg : mData) {
                                    if (msg.getmPlayAnimation() != null) {
                                        msg.getmPlayAnimation().stop();
                                        msg.getmPlayAnimation().selectDrawable(0);
                                        msg.setmIsClick(false);
                                    }
                                }
                                menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        File recordFile = new File(chat.getmAudioPath());
                                        if (recordFile.exists()) {
                                            recordFile.delete();
                                        }
                                        chat.setmSended(4);
                                        mData.remove(chat);

                                        ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                        notifyDataSetChanged();
                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                    }
                                }, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                mContext.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                }, mContext.getString(R.string.cancel),
                                                new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        File recordFile = null;
                                                        String familyid = chat.getmFamilyId();
                                                        ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                        for (ChatMsgEntity chatmsg : mData) {
                                                            recordFile = new File(chatmsg.getmAudioPath());
                                                            if (recordFile.exists()) {
                                                                recordFile.delete();
                                                            }
                                                        }
                                                        mData.clear();
                                                        notifyDataSetChanged();
                                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                    }
                                                }, mContext.getString(R.string.confirm)).show();

                                    }
                                }, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        try {
                                            FileInputStream fileInputStream = new FileInputStream(chat.getmAudioPath());
                                            int length = fileInputStream.available();
                                            byte[] buffer = new byte[length];
                                            fileInputStream.read(buffer);
                                            fileInputStream.close();
                                            byte[] tmp = AESUtil.getInstance().decrypt(buffer);
                                            StringBuilder filename = new StringBuilder();
                                            if (mWatch != null) {
                                                filename.append(mWatch.getNickname());
                                            } else {
                                                filename.append(mApp.getCurUser().queryNicknameByEid(mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId()).getWatchlist().get(0).getEid()));
                                            }
                                            filename.append("_").append(TimeUtil.getTimeStampFromUTC(TimeUtil.getMillisByTime(chat.getmDate()))).append(".amr");
                                            File file = new File(ImibabyApp.getMyChat(), filename.toString());
                                            FileOutputStream fos = new FileOutputStream(file);
                                            fos.write(tmp);
                                            fos.close();
                                            ToastUtil.show(mContext, file.getAbsolutePath());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        notifyDataSetChanged();
                                    }
                                });
                                //显示窗口
                                menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                return false;
                            }
                        });
                    }
                    if (!existsflag) {
                        viewHolder.mRightContent.setBackgroundResource(R.drawable.dialog_box2_1);
                        viewHolder.mRightContent.setOnClickListener(null);
                        viewHolder.mRightAnim.setVisibility(View.GONE);
                        viewHolder.mRightRetry.setVisibility(View.GONE);
                    } else {
                        switch (chat.getmSended()) {
                            case 0:
                            case 3:
                                viewHolder.mRightDurationTime.setVisibility(View.GONE);
                                viewHolder.mRightRetry.setVisibility(View.GONE);
                                viewHolder.mRightAnim.setVisibility(View.INVISIBLE);
                                viewHolder.mRightRetry.setVisibility(View.GONE);
                                viewHolder.mRightWaiting.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                anim = (AnimationDrawable) viewHolder.mRightAnim.getBackground();
                                anim.stop();
                                anim.selectDrawable(0);
                                viewHolder.mRightContent.setTag(R.id.view_tag_first__, chat);
                                viewHolder.mRightContent.setTag(R.id.view_tag_second__, viewHolder);
                                viewHolder.mRightContent.setOnClickListener(content_click);
                                viewHolder.mRightRetry.setVisibility(View.GONE);
                                viewHolder.mRightWaiting.setVisibility(View.GONE);
                                break;
                            case 2:
                                anim = (AnimationDrawable) viewHolder.mRightAnim.getBackground();
                                anim.stop();
                                anim.selectDrawable(0);
                                viewHolder.mRightContent.setTag(R.id.view_tag_first__, chat);
                                viewHolder.mRightContent.setTag(R.id.view_tag_second__, viewHolder);
                                viewHolder.mRightContent.setOnClickListener(content_click);
                                viewHolder.mRightRetry.setBackgroundResource(R.drawable.refresh_0);
                                viewHolder.mRightRetry.setVisibility(View.VISIBLE);
                                viewHolder.mRightWaiting.setVisibility(View.GONE);
                                viewHolder.mRightRetry.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        Intent it = new Intent(Const.ACTION_RESEND_CHAT);
                                        it.putExtra("position", mData.indexOf(chat));
                                        mContext.sendBroadcast(it);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                    //初始化播放动画
                    AnimationDrawable animDra;
                    animDra = (AnimationDrawable) viewHolder.mRightAnim.getBackground();
                    animDra.selectDrawable(0);
                    chat.setmPlayAnimation(animDra);
                }
                break;
            case 4:
                viewHolder.mNickname.setVisibility(View.GONE);
                viewHolder.mLeftAnim.setVisibility(View.GONE);
                viewHolder.mLeftContent.setVisibility(View.GONE);
                viewHolder.mLeftDurationTime.setVisibility(View.GONE);
                viewHolder.mLeftHead.setVisibility(View.VISIBLE);
                viewHolder.mLeftRetry.setVisibility(View.GONE);
                viewHolder.mLeftNotify.setVisibility(View.VISIBLE);
                viewHolder.mRightAnim.setVisibility(View.GONE);
                viewHolder.mRightContent.setVisibility(View.GONE);
                viewHolder.mRightDurationTime.setVisibility(View.GONE);
                viewHolder.mRightRetry.setVisibility(View.GONE);
                viewHolder.mRightWaiting.setVisibility(View.GONE);
                //设置用户图像
                if (mApp.getCurUser().getIsWatchByEid(chat.getmSrcId())) {//如果是手表
                    String headpath = mApp.getCurUser().getHeadPathByEid(chat.getmSrcId());
                    ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                            mApp.getHeadDrawableByFile(mContext.getResources(), headpath, chat.getmSrcId(), R.drawable.default_head));
                } else {
                    int attri = mApp.getUserAttriByEid(mWatch.getEid(), chat.getmSrcId());
                    String avatatr = mApp.getUserAvatarByEid(mWatch.getEid(), chat.getmSrcId());
                    final ImageView ivAvatar = viewHolder.mLeftHead;
                    if (avatatr == null) {
                        ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                                mApp.getHeadDrawableByFile(mContext.getResources(), Integer.toString(attri), chat.getmSrcId(), R.drawable.relation_custom));
                    } else {
                        Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatatr, new ImageDownloadHelper.OnImageDownloadListener() {
                            @Override
                            public void onImageDownload(String url, Bitmap bitmap) {
                                Drawable headDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                            }
                        });
                        if (headBitmap != null) {
                            Drawable headDrawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                            ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_2, headDrawable);
                        }
                    }
                }
                viewHolder.mLeftHead.setTag(chat);
                viewHolder.mLeftHead.setOnClickListener(head_click);

                FamilyData family;
                switch (chat.getmForceRecordOk()) {
                    case 1:
                        family = mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId());
                        viewHolder.mLeftNotify.setText(mContext.getString(R.string.watch_join_family, chat.getmAudioPath(), family.getFamilyName()));
                        break;
                    case 2:
                        family = mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId());
                        viewHolder.mLeftNotify.setText(mContext.getString(R.string.watch_quit_family, chat.getmAudioPath(), family.getFamilyName()));
                        break;
                    case 4:
                        family = mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId());
                        viewHolder.mLeftNotify.setText(mContext.getString(R.string.member_become_admin, chat.getmAudioPath(), family.getFamilyName()));
                        break;
                    default:
                        break;
                }
                break;
            case 5:
                if (mType == 2) {
                    viewHolder.mNickname.setVisibility(View.GONE);
                    viewHolder.mSendTimeLayout.setVisibility(View.GONE);
                    viewHolder.mLeftAnim.setVisibility(View.GONE);
                    viewHolder.mLeftContent.setVisibility(View.GONE);
                    viewHolder.mLeftDurationTime.setVisibility(View.GONE);
                    viewHolder.mLeftHead.setVisibility(View.GONE);
                    viewHolder.mLeftRetry.setVisibility(View.GONE);
                    viewHolder.mLeftNotify.setVisibility(View.GONE);
                    viewHolder.mRightAnim.setVisibility(View.GONE);
                    viewHolder.mRightContent.setVisibility(View.GONE);
                    viewHolder.mRightDurationTime.setVisibility(View.GONE);
                    viewHolder.mRightHead.setVisibility(View.GONE);
                    viewHolder.mRightRetry.setVisibility(View.GONE);
                    viewHolder.mRightWaiting.setVisibility(View.GONE);
                    viewHolder.mNotifyLocation.setVisibility(View.VISIBLE);
                    viewHolder.mNotifyLocation.setText(chat.getmUserName());
                    viewHolder.mNotifyInfo.setVisibility(View.GONE);
                    viewHolder.mNotifyInfo.setText(chat.getmAudioPath());
                    viewHolder.mLeftTextContent.setVisibility(View.GONE);
                    viewHolder.mRightTextContent.setVisibility(View.GONE);
                    viewHolder.mLeftImageContent.setVisibility(View.GONE);
                    viewHolder.mLeftImagePlay.setVisibility(View.GONE);
                    viewHolder.mInterval.setVisibility(View.GONE);
                }
                break;
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT:
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI:
                if (chat.getmIsFrom()) {

                    final String watchid;
                    if (mApp.getCurUser().getIsWatchByEid(chat.getmSrcId())) {
                        watchid = chat.getmSrcId();
                    } else {
                        watchid = mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId()).getWatchlist().get(0).getEid();
                    }
                    if (chat.getmSrcId().equals(watchid)) {
                        viewHolder.mNickname.setText(mApp.getCurUser().queryNicknameByEid(chat.getmSrcId()));
                    } else {
                        MemberUserData testUser = mApp.getCurUser().queryUserDataByEid(chat.getmSrcId());
                        if (testUser != null) {
                            viewHolder.mNickname.setText(testUser.getRelation(watchid));
                        } else {
                            viewHolder.mNickname.setText(R.string.default_relation_text);
                        }
                    }

                    //设置用户图像
                    if (mApp.getCurUser().getIsWatchByEid(chat.getmSrcId())) {//如果是手表
                        String headpath = mApp.getCurUser().getHeadPathByEid(chat.getmSrcId());
                        ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                                mApp.getHeadDrawableByFile(mContext.getResources(), headpath, chat.getmSrcId(), R.drawable.default_head));
                    } else {
                        int attri = mApp.getUserAttriByEid(watchid, chat.getmSrcId());
                        String avatatr = mApp.getUserAvatarByEid(watchid, chat.getmSrcId());
                        final ImageView ivAvatar = viewHolder.mLeftHead;
                        if (avatatr == null) {
                            ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                                    mApp.getHeadDrawableByFile(mContext.getResources(), Integer.toString(attri), chat.getmSrcId(), R.drawable.relation_custom));
                        } else {
                            Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatatr, new ImageDownloadHelper.OnImageDownloadListener() {
                                @Override
                                public void onImageDownload(String url, Bitmap bitmap) {
                                    Drawable headDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                    ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                                }
                            });
                            if (headBitmap != null) {
                                Drawable headDrawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                                ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_2, headDrawable);
                            }
                        }
                    }
                    viewHolder.mLeftHead.setTag(chat);
                    viewHolder.mLeftHead.setOnClickListener(head_click);

                    viewHolder.mLeftContent.setVisibility(View.GONE);
                    viewHolder.mLeftDurationTime.setVisibility(View.GONE);
                    if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT) {
                        viewHolder.mLeftImageContent.setVisibility(View.GONE);
                        viewHolder.mLeftImagePlay.setVisibility(View.GONE);
                        viewHolder.mLeftTextContent.setVisibility(View.VISIBLE);
                        viewHolder.mLeftTextContent.setUrlText(chat.getmAudioPath());
                        if (mType != 2) {
                            viewHolder.mLeftTextContent.setOnLongClickListener(new OnLongClickListener() {

                                public boolean onLongClick(View v) {
                                    // TODO Auto-generated method stub
                                    mApp.hideKeyboard(viewHolder.mLeftTextContent);
                                    for (ChatMsgEntity msg : mData) {
                                        if (msg.getmPlayAnimation() != null) {
                                            msg.getmPlayAnimation().stop();
                                            msg.getmPlayAnimation().selectDrawable(0);
                                            msg.setmIsClick(false);
                                        }
                                    }
                                    MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                    menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            chat.setmSended(4);
                                            mData.remove(chat);
                                            ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                            notifyDataSetChanged();
                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                    mContext.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    }, mContext.getString(R.string.cancel),
                                                    new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            File recordFile = null;
                                                            String familyid = chat.getmFamilyId();
                                                            ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                            for (ChatMsgEntity chatmsg : mData) {
                                                                recordFile = new File(chatmsg.getmAudioPath());
                                                                if (recordFile.exists()) {
                                                                    recordFile.delete();
                                                                }
                                                            }
                                                            mData.clear();
                                                            notifyDataSetChanged();
                                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                        }
                                                    }, mContext.getString(R.string.confirm)).show();

                                        }
                                    }, null, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            menuWindow.dismiss();
                                            ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                            clip.setText(viewHolder.mLeftTextContent.getText());
                                            ToastUtil.showMyToast(mApp, mContext.getString(R.string.copy_success), Toast.LENGTH_SHORT);
                                        }
                                    });
                                    //显示窗口
                                    menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                    return false;
                                }
                            });
                        }
                    } else if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI) {
                        viewHolder.mLeftImageContent.setVisibility(View.GONE);
                        viewHolder.mLeftImagePlay.setVisibility(View.GONE);
                        viewHolder.mLeftTextContent.setVisibility(View.VISIBLE);
                        viewHolder.mLeftTextContent.setText(getEmojiSequence(chat.getmAudioPath()));

                        if (mType != 2) {
                            viewHolder.mLeftTextContent.setOnLongClickListener(new OnLongClickListener() {

                                public boolean onLongClick(View v) {
                                    // TODO Auto-generated method stub
                                    mApp.hideKeyboard(viewHolder.mLeftTextContent);
                                    for (ChatMsgEntity msg : mData) {
                                        if (msg.getmPlayAnimation() != null) {
                                            msg.getmPlayAnimation().stop();
                                            msg.getmPlayAnimation().selectDrawable(0);
                                            msg.setmIsClick(false);
                                        }
                                    }
                                    MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                    menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            chat.setmSended(4);
                                            mData.remove(chat);
                                            ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                            notifyDataSetChanged();
                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                    mContext.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    }, mContext.getString(R.string.cancel),
                                                    new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            File recordFile = null;
                                                            String familyid = chat.getmFamilyId();
                                                            ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                            for (ChatMsgEntity chatmsg : mData) {
                                                                recordFile = new File(chatmsg.getmAudioPath());
                                                                if (recordFile.exists()) {
                                                                    recordFile.delete();
                                                                }
                                                            }
                                                            mData.clear();
                                                            notifyDataSetChanged();
                                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                        }
                                                    }, mContext.getString(R.string.confirm)).show();

                                        }
                                    }, null, null);
                                    //显示窗口
                                    menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                    return false;
                                }
                            });
                        }
                    }

                    viewHolder.mLeftAnim.setVisibility(View.GONE);
                    viewHolder.mLeftRetry.setVisibility(View.GONE);

                } else {
                    //设置用户图像
                    int attri = mApp.getUserAttriByEid(mWatch.getEid(), chat.getmSrcId());
                    String avatatr = mApp.getUserAvatarByEid(mWatch.getEid(), chat.getmSrcId());
                    final ImageView ivAvatar = viewHolder.mRightHead;
                    if (avatatr == null) {
                        ImageUtil.setMaskImage(viewHolder.mRightHead, R.drawable.head_1, mApp.getHeadDrawableByFile(mContext.getResources(), Integer.toString(attri), chat.getmSrcId(), R.drawable.relation_custom));
                    } else {
                        Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatatr, new ImageDownloadHelper.OnImageDownloadListener() {
                            @Override
                            public void onImageDownload(String url, Bitmap bitmap) {
                                Drawable headDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                            }
                        });
                        if (headBitmap != null) {
                            Drawable headDrawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                            ImageUtil.setMaskImage(viewHolder.mRightHead, R.drawable.head_2, headDrawable);
                        }
                    }
                    viewHolder.mRightHead.setTag(chat);
                    viewHolder.mRightHead.setOnClickListener(head_click);

                    viewHolder.mRightContent.setVisibility(View.GONE);
                    viewHolder.mRightDurationTime.setVisibility(View.GONE); //设置语音时长
                    if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_TEXT) {
                        viewHolder.mRightTextContent.setVisibility(View.VISIBLE);
                        viewHolder.mRightTextContent.setUrlText(chat.getmAudioPath());
                        if (mType != 2) {
                            viewHolder.mRightTextContent.setOnLongClickListener(new OnLongClickListener() {

                                public boolean onLongClick(View v) {
                                    // TODO Auto-generated method stub
                                    mApp.hideKeyboard(viewHolder.mRightTextContent);
                                    MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                    for (ChatMsgEntity msg : mData) {
                                        if (msg.getmPlayAnimation() != null) {
                                            msg.getmPlayAnimation().stop();
                                            msg.getmPlayAnimation().selectDrawable(0);
                                            msg.setmIsClick(false);
                                        }
                                    }
                                    menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            chat.setmSended(4);
                                            mData.remove(chat);
                                            ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                            notifyDataSetChanged();
                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                    mContext.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    }, mContext.getString(R.string.cancel),
                                                    new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            File recordFile = null;
                                                            String familyid = chat.getmFamilyId();
                                                            ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                            for (ChatMsgEntity chatmsg : mData) {
                                                                recordFile = new File(chatmsg.getmAudioPath());
                                                                if (recordFile.exists()) {
                                                                    recordFile.delete();
                                                                }
                                                            }
                                                            mData.clear();
                                                            notifyDataSetChanged();
                                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                        }
                                                    }, mContext.getString(R.string.confirm)).show();

                                        }
                                    }, null, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            menuWindow.dismiss();
                                            ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                            clip.setText(viewHolder.mRightTextContent.getText());
                                            ToastUtil.showMyToast(mApp, mContext.getString(R.string.copy_success), Toast.LENGTH_SHORT);
                                        }
                                    });
                                    //显示窗口
                                    menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                    return false;
                                }
                            });
                        }
                    } else if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_EMOJI) {
                        viewHolder.mRightTextContent.setVisibility(View.VISIBLE);
                        viewHolder.mRightTextContent.setText(getEmojiSequence(chat.getmAudioPath()));
                        if (mType != 2) {
                            viewHolder.mRightTextContent.setOnLongClickListener(new OnLongClickListener() {

                                public boolean onLongClick(View v) {
                                    // TODO Auto-generated method stub
                                    mApp.hideKeyboard(viewHolder.mRightTextContent);
                                    for (ChatMsgEntity msg : mData) {
                                        if (msg.getmPlayAnimation() != null) {
                                            msg.getmPlayAnimation().stop();
                                            msg.getmPlayAnimation().selectDrawable(0);
                                            msg.setmIsClick(false);
                                        }
                                    }
                                    MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                    menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            chat.setmSended(4);
                                            mData.remove(chat);
                                            ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                            notifyDataSetChanged();
                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            menuWindow.dismiss();
                                            DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                    mContext.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                        }
                                                    }, mContext.getString(R.string.cancel),
                                                    new DialogUtil.OnCustomDialogListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            File recordFile = null;
                                                            String familyid = chat.getmFamilyId();
                                                            ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                            for (ChatMsgEntity chatmsg : mData) {
                                                                recordFile = new File(chatmsg.getmAudioPath());
                                                                if (recordFile.exists()) {
                                                                    recordFile.delete();
                                                                }
                                                            }
                                                            mData.clear();
                                                            notifyDataSetChanged();
                                                            mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                        }
                                                    }, mContext.getString(R.string.confirm)).show();

                                        }
                                    }, null, null);
                                    //显示窗口
                                    menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                    return false;
                                }
                            });
                        }
                    }

                    viewHolder.mRightAnim.setVisibility(View.INVISIBLE);

                    switch (chat.getmSended()) {
                        case 0:
                        case 3:
                            viewHolder.mRightRetry.setVisibility(View.GONE);
                            viewHolder.mRightWaiting.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            viewHolder.mRightRetry.setVisibility(View.GONE);
                            viewHolder.mRightWaiting.setVisibility(View.GONE);
                            break;
                        case 2:
                            viewHolder.mRightRetry.setBackgroundResource(R.drawable.refresh_0);
                            viewHolder.mRightRetry.setVisibility(View.VISIBLE);
                            viewHolder.mRightWaiting.setVisibility(View.GONE);
                            viewHolder.mRightRetry.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Intent it = new Intent(Const.ACTION_RESEND_CHAT);
                                    it.putExtra("position", mData.indexOf(chat));
                                    mContext.sendBroadcast(it);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                }
                break;
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_IMAGE:
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO:
            case ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO:
                mWatch = mApp.getCurUser().queryFamilyByGid(chat.getmFamilyId()).getWatchlist().get(0);
                final String watchid = mWatch.getEid();
                if (chat.getmSrcId().equals(watchid)) {
                    viewHolder.mNickname.setText(mApp.getCurUser().queryNicknameByEid(chat.getmSrcId()));
                } else {
                    MemberUserData testUser = mApp.getCurUser().queryUserDataByEid(chat.getmSrcId());
                    if (testUser != null) {
                        viewHolder.mNickname.setText(testUser.getRelation(watchid));
                    } else {
                        viewHolder.mNickname.setText(R.string.default_relation_text);
                    }
                }

                //设置用户图像
                if (mApp.getCurUser().getIsWatchByEid(chat.getmSrcId())) {//如果是手表
                    String headpath = mApp.getCurUser().getHeadPathByEid(chat.getmSrcId());
                    ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                            mApp.getHeadDrawableByFile(mContext.getResources(), headpath, chat.getmSrcId(), R.drawable.default_head));
                } else {
                    int attri = mApp.getUserAttriByEid(watchid, chat.getmSrcId());
                    String avatatr = mApp.getUserAvatarByEid(watchid, chat.getmSrcId());
                    final ImageView ivAvatar = viewHolder.mLeftHead;
                    if (avatatr == null) {
                        ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_1,
                                mApp.getHeadDrawableByFile(mContext.getResources(), Integer.toString(attri), chat.getmSrcId(), R.drawable.relation_custom));
                    } else {
                        Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatatr, new ImageDownloadHelper.OnImageDownloadListener() {
                            @Override
                            public void onImageDownload(String url, Bitmap bitmap) {
                                Drawable headDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
                                ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                            }
                        });
                        if (headBitmap != null) {
                            Drawable headDrawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                            ImageUtil.setMaskImage(viewHolder.mLeftHead, R.drawable.head_2, headDrawable);
                        }
                    }
                }
                viewHolder.mLeftHead.setTag(chat);
                viewHolder.mLeftHead.setOnClickListener(head_click);

                viewHolder.mLeftContent.setVisibility(View.GONE);
                viewHolder.mLeftTextContent.setVisibility(View.GONE);
                viewHolder.mLeftDurationTime.setVisibility(View.GONE);
                viewHolder.mLeftImageContent.setVisibility(View.VISIBLE);
                if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO) {
                    viewHolder.mLeftImagePlay.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mLeftImagePlay.setVisibility(View.GONE);
                }
                if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_IMAGE) {
                    final Bitmap bitmap = BitmapFactory.decodeFile(chat.getmAudioPath());
                    if (bitmap != null) {
                        viewHolder.mLeftImageContent.setImageBitmap(scale(bitmap, (float)118 * 3 / bitmap.getWidth(), mContext));
                    } else {
                        Bitmap delete = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image_deleted);
                        viewHolder.mLeftImageContent.setImageBitmap(scale(delete, (float)118 * 3 / delete.getWidth(), mContext));
                    }
                    if (mType != 2) {
                        viewHolder.mLeftImageContent.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mApp.hideKeyboard(viewHolder.mLeftImageContent);
                                if (bitmap != null) {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setDataAndType(Uri.fromFile(new File(chat.getmAudioPath())), "image/*");
//                                    mContext.startActivity(intent);
                                    Intent intent = new Intent(mContext, ImageDisplayActivity.class);

                                    intent.setData(getUriFromFile(mContext, new File(chat.getmAudioPath())));
                                    mContext.startActivity(intent);
                                    mContext.overridePendingTransition(R.anim.activity_zoom_enter, 0);
                                } else {
                                    ToastUtil.showMyToast(mApp, mContext.getString(R.string.image_delete), Toast.LENGTH_SHORT);
                                }
                            }
                        });

                        viewHolder.mLeftImageContent.setOnLongClickListener(new OnLongClickListener() {

                            public boolean onLongClick(View v) {
                                // TODO Auto-generated method stub
                                mApp.hideKeyboard(viewHolder.mLeftImageContent);
                                for (ChatMsgEntity msg : mData) {
                                    if (msg.getmPlayAnimation() != null) {
                                        msg.getmPlayAnimation().stop();
                                        msg.getmPlayAnimation().selectDrawable(0);
                                        msg.setmIsClick(false);
                                    }
                                }
                                MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        chat.setmSended(4);
                                        mData.remove(chat);
                                        ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                        notifyDataSetChanged();
                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                    }
                                }, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                mContext.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                }, mContext.getString(R.string.cancel),
                                                new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        File recordFile = null;
                                                        String familyid = chat.getmFamilyId();
                                                        ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                        for (ChatMsgEntity chatmsg : mData) {
                                                            recordFile = new File(chatmsg.getmAudioPath());
                                                            if (recordFile.exists()) {
                                                                recordFile.delete();
                                                            }
                                                        }
                                                        mData.clear();
                                                        notifyDataSetChanged();
                                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                    }
                                                }, mContext.getString(R.string.confirm)).show();

                                    }
                                });
                                //显示窗口
                                menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                return false;
                            }
                        });
                    }
                } else if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_VIDEO || chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO) {
                    final File preview = new File(ImibabyApp.getChatCacheDir().getPath(), chat.getmAudioPath().substring(chat.getmAudioPath().lastIndexOf("/")));
                    boolean hasPreview = false;
                    if (preview.exists()) {
                        Bitmap previewBitmap = BitmapFactory.decodeFile(preview.getAbsolutePath());
                        if(previewBitmap != null) {
                            hasPreview = true;
                            viewHolder.mLeftImageContent.setImageBitmap(scale(previewBitmap, (float) 118 * 3 / previewBitmap.getWidth(), mContext));
                        }
                    }
                    if (!hasPreview){
                        preview.delete();
                        //viewHolder.mLeftImageContent.setImageResource(R.drawable.btn_play_selector);
                        viewHolder.mLeftImageContent.setTag(chat.getmAudioPath().substring(chat.getmAudioPath().lastIndexOf("/")));
                        mApp.downloadNoticeVideo(watchid, chat.getmAudioPath(), new OnImageDownload() {
                            @Override
                            public void onSuccess(String filePath) {
                                String tag = (String) viewHolder.mLeftImageContent.getTag();
                                if (filePath.endsWith(tag)) {
                                    Bitmap previewBitmap = BitmapFactory.decodeFile(filePath);
                                    if (previewBitmap != null) {
                                        viewHolder.mLeftImageContent.setImageBitmap(scale(previewBitmap, (float) 118 * 3 / previewBitmap.getWidth(), mContext));
                                    }
                                }
                            }

                            @Override
                            public void onFail() {

                            }
                        });
                    }

                    viewHolder.mLeftImageContent.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mApp.hideKeyboard(viewHolder.mLeftImageContent);

                            if (chat.getmType() == ChatMsgEntity.CHAT_MESSAGE_TYPE_PHOTO) {
                                Intent intent = new Intent(mContext, ImageDisplayActivity.class);
                                intent.setData(getUriFromFile(mContext, preview));
                                mContext.startActivity(intent);
                                mContext.overridePendingTransition(R.anim.activity_zoom_enter, 0);
                            } else {
                                Intent intent = new Intent(mContext, VideoDisplayActivity.class);
                                intent.putExtra("key", chat.getmAudioPath());
                                intent.putExtra("type", chat.getmType());
                                intent.putExtra("eid", watchid);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                    if (mType != 2) {
                        viewHolder.mLeftImageContent.setOnLongClickListener(new OnLongClickListener() {

                            public boolean onLongClick(View v) {
                                // TODO Auto-generated method stub
                                mApp.hideKeyboard(viewHolder.mLeftImageContent);
                                for (ChatMsgEntity msg : mData) {
                                    if (msg.getmPlayAnimation() != null) {
                                        msg.getmPlayAnimation().stop();
                                        msg.getmPlayAnimation().selectDrawable(0);
                                        msg.setmIsClick(false);
                                    }
                                }
                                MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);
                                menuWindow = new ChatPopupWindow(mContext, new OnClickListener() {

                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        chat.setmSended(4);
                                        mData.remove(chat);
                                        ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                                        notifyDataSetChanged();
                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                    }
                                }, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        menuWindow.dismiss();
                                        DialogUtil.CustomALertDialog(mContext, mContext.getString(R.string.clear_messge_title),
                                                mContext.getString(R.string.clear_message_text), new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                }, mContext.getString(R.string.cancel),
                                                new DialogUtil.OnCustomDialogListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        File recordFile = null;
                                                        String familyid = chat.getmFamilyId();
                                                        ChatHisDao.getInstance(mContext.getApplicationContext()).delAllMsg(familyid);
                                                        for (ChatMsgEntity chatmsg : mData) {
                                                            recordFile = new File(chatmsg.getmAudioPath());
                                                            if (recordFile.exists()) {
                                                                recordFile.delete();
                                                            }
                                                        }
                                                        mData.clear();
                                                        notifyDataSetChanged();
                                                        mContext.sendBroadcast(new Intent(Const.ACTION_CLEAR_MESSAGE));
                                                    }
                                                }, mContext.getString(R.string.confirm)).show();

                                    }
                                });
                                //显示窗口
                                menuWindow.showAtLocation(mContext.findViewById(R.id.chatlayout), Gravity.CENTER, 0, 0);
                                return false;
                            }
                        });
                    }
                }

                viewHolder.mLeftAnim.setVisibility(View.GONE);
                viewHolder.mLeftRetry.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        return convertView;
    }

    //通过ViewHolder显示项的内容
    static class ViewHolder {
        public RelativeLayout mSendTimeLayout;
        public TextView mSendTime;
        public TextView mLeftDurationTime;
        public TextView mRightDurationTime;
        public ImageView mLeftHead;
        public ImageView mRightHead;
        public ImageButton mLeftContent;
        public ImageButton mRightContent;
        public ImageView mLeftAnim;
        public ImageView mRightAnim;
        public ImageView mLeftRetry;
        public ImageView mRightRetry;
        public ProgressBar mRightWaiting;
        public TextView mLeftNotify;
        public TextView mNotifyLocation;
        public TextView mNotifyInfo;
        public TextView mNickname;
        public View mInterval;

        public HttpTextView mLeftTextContent;
        public HttpTextView mRightTextContent;

        public ImageView mLeftImageContent;
        public ImageView mLeftImagePlay;

        public Boolean mIsFrom = false;
    }

    private void setSendTime(ViewHolder viewHolder, int position, ChatMsgEntity chat) {
        if (0 != position) {
            long t1 = TimeUtil.getMillisByTime(chat.getmDate());
            long t2 = TimeUtil.getMillisByTime(mData.get(position - 1).getmDate());
            long interval = Math.abs(t1 - t2) / 1000;
            if (interval > Const.CHAT_INTERVAL_TIME || (mType == 2 && position == 1)) {
                viewHolder.mSendTimeLayout.setVisibility(View.VISIBLE);
                viewHolder.mSendTime.setVisibility(View.VISIBLE);
                viewHolder.mSendTime.setText(TimeUtil.getTime(mContext, TimeUtil.chnToLocalTimestamp(chat.getmDate())));
            } else {
                viewHolder.mSendTimeLayout.setVisibility(View.GONE);
                viewHolder.mSendTime.setVisibility(View.GONE);
            }
        } else {
            viewHolder.mSendTimeLayout.setVisibility(View.VISIBLE);
            viewHolder.mSendTime.setVisibility(View.VISIBLE);
            viewHolder.mSendTime.setText(TimeUtil.getTime(mContext, TimeUtil.chnToLocalTimestamp(chat.getmDate())));
        }
    }

    private void setViewVisibility(ChatMsgEntity from, ViewHolder viewHolder) {
        if (from.getmIsFrom()) {
            viewHolder.mNickname.setVisibility(View.VISIBLE);
            viewHolder.mLeftAnim.setVisibility(View.VISIBLE);
            viewHolder.mLeftContent.setVisibility(View.VISIBLE);
            viewHolder.mLeftTextContent.setVisibility(View.VISIBLE);
            viewHolder.mLeftDurationTime.setVisibility(View.VISIBLE);
            viewHolder.mLeftImageContent.setVisibility(View.VISIBLE);
            viewHolder.mLeftImagePlay.setVisibility(View.GONE);
            viewHolder.mLeftHead.setVisibility(View.VISIBLE);
            viewHolder.mLeftRetry.setVisibility(View.VISIBLE);
            viewHolder.mLeftNotify.setVisibility(View.GONE);
            viewHolder.mRightAnim.setVisibility(View.GONE);
            viewHolder.mRightContent.setVisibility(View.GONE);
            viewHolder.mRightDurationTime.setVisibility(View.GONE);
            viewHolder.mRightHead.setVisibility(View.GONE);
            viewHolder.mRightRetry.setVisibility(View.GONE);
            viewHolder.mRightWaiting.setVisibility(View.GONE);
            viewHolder.mNotifyInfo.setVisibility(View.GONE);
            viewHolder.mNotifyLocation.setVisibility(View.GONE);
            viewHolder.mRightTextContent.setVisibility(View.GONE);
            viewHolder.mInterval.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mNickname.setVisibility(View.GONE);
            viewHolder.mLeftAnim.setVisibility(View.GONE);
            viewHolder.mLeftContent.setVisibility(View.GONE);
            viewHolder.mLeftTextContent.setVisibility(View.GONE);
            viewHolder.mLeftDurationTime.setVisibility(View.GONE);
            viewHolder.mLeftImageContent.setVisibility(View.GONE);
            viewHolder.mLeftImagePlay.setVisibility(View.GONE);
            viewHolder.mLeftHead.setVisibility(View.GONE);
            viewHolder.mLeftRetry.setVisibility(View.GONE);
            viewHolder.mLeftNotify.setVisibility(View.GONE);
            viewHolder.mRightAnim.setVisibility(View.VISIBLE);
            viewHolder.mRightContent.setVisibility(View.VISIBLE);
            viewHolder.mRightDurationTime.setVisibility(View.VISIBLE);
            viewHolder.mRightHead.setVisibility(View.VISIBLE);
            viewHolder.mRightRetry.setVisibility(View.VISIBLE);
            viewHolder.mNotifyInfo.setVisibility(View.GONE);
            viewHolder.mNotifyLocation.setVisibility(View.GONE);
            viewHolder.mRightTextContent.setVisibility(View.VISIBLE);
            viewHolder.mInterval.setVisibility(View.VISIBLE);
        }
        if (from.getmType() == 2) {
            viewHolder.mLeftContent.setBackgroundResource(R.drawable.dialog_box_sos_0);
        } else {
            viewHolder.mLeftContent.setBackgroundResource(R.drawable.dialog_box_0);
        }
        viewHolder.mRightContent.setBackgroundResource(R.drawable.dialog_box2_0);
    }

    private OnClickListener head_click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//			int f_index = 0;
//			int m_index = 0;
//			int m_type = 0;
//			ChatMsgEntity chat = (ChatMsgEntity)v.getTag();
//			for(FamilyData family:mApp.getCurUser().getFamilyList()){
//				if(chat.getmFamilyId().equals(family.getFamilyId())){
//					break;
//				}else{
//					f_index++;
//				}
//			}
//			for(MemberUserData user: mApp.getCurUser().getFamilyList().get(f_index).getMemberList()){
//				if(chat.getmSrcId().equals(user.getEid())){
//					m_type = 1;
//					break;
//				}else{
//					m_index ++;
//				}
//			}
//			if(m_type == 0){
//				m_index = 0;
//				for(WatchData watch:mApp.getCurUser().getFamilyList().get(f_index).getWatchlist()){
//					if(chat.getmSrcId().equals(watch.getEid())){
//						break;
//					}else{
//						m_index ++;
//					}
//				}
//			}
//			Intent it = new Intent(mContext, DeviceDetailActivity.class);
//            it.putExtra(Const.KEY_FAMILY_LIST_INDEX, f_index);                      
//            it.putExtra(Const.KEY_MEMBER_LIST_INDEX, m_index);
//            it.putExtra(Const.KEY_MEMBER_TYPE, m_type);
//            mContext.startActivity(it); 
        }
    };
    private OnClickListener content_click = new OnClickListener() {

        @SuppressWarnings("deprecation")
        public void onClick(View v) {
            // TODO Auto-generated method stub
            final ChatMsgEntity chat = (ChatMsgEntity) v.getTag(R.id.view_tag_first__);
            final ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.view_tag_second__);
            Thread th = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    playRecord(chat);
                }
            });
            mApp.sdcardLog("onclick record item");
            if (!chat.getmIsClick()) {
                for (ChatMsgEntity msg : mData) {
                    if (msg.getmPlayAnimation() != null) {
                        msg.getmPlayAnimation().stop();
                        msg.getmPlayAnimation().selectDrawable(0);
                        msg.setmIsClick(false);
                    }
                }
                chat.setmIsClick(true);

                if (chat.getmIsFrom()) {
                    chat.setmPlayed(true);
                    ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(chat.getmFamilyId(), chat, chat.getmDate());
                    viewHolder.mLeftRetry.setVisibility(View.GONE);
                }
                if (chat.getmPlayAnimation() != null) {
                    chat.getmPlayAnimation().start();
                    th.start();
                }
            } else {
                if (chat.getmPlayAnimation() != null) {
                    chat.setmIsClick(false);
                    chat.getmPlayAnimation().stop();
                    chat.getmPlayAnimation().selectDrawable(0);
                }
                MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);


                if (th.getState() == Thread.State.RUNNABLE) {
                    th.interrupt();
                }
            }
        }
    };

    public void stopVoicePlayAnimation() {
        for (ChatMsgEntity msg : mData) {
            if (msg.getmPlayAnimation() != null) {
                msg.getmPlayAnimation().stop();
                msg.getmPlayAnimation().selectDrawable(0);
                msg.setmIsClick(false);

            }
        }
    }

    public void startNextRecord(final ChatMsgEntity mChat) {
        Thread th = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                playRecord(mChat);
            }
        });

        if (!mChat.getmIsClick()) {
            for (ChatMsgEntity msg : mData) {
                if (msg.getmPlayAnimation() != null) {
                    msg.getmPlayAnimation().stop();
                    msg.getmPlayAnimation().selectDrawable(0);
                    msg.setmIsClick(false);
                }
            }
            mChat.setmIsClick(true);
            if (mChat.getmIsFrom()) {
                mChat.setmPlayed(true);
                ChatHisDao.getInstance(mContext.getApplicationContext()).updateChatMsg(mChat.getmFamilyId(), mChat, mChat.getmDate());
                if (mChat.getmLeftRetry() != null) {
                    mChat.getmLeftRetry().setVisibility(View.GONE);
                }
            }
            if (mChat.getmPlayAnimation() != null) {
                mChat.getmPlayAnimation().start();
                th.start();
            }
        } else {
            if (mChat.getmPlayAnimation() != null) {
                mChat.setmIsClick(false);
                mChat.getmPlayAnimation().stop();
                mChat.getmPlayAnimation().selectDrawable(0);
            }
            MyMediaPlayerUtil.getInstance().stopMediaPlayer(mApp);


            if (th.getState() == Thread.State.RUNNABLE) {
                th.interrupt();
            }
        }
    }

    private void playRecord(final ChatMsgEntity mChat) {
        if (mChat != null) {
            try {
                MediaPlayer mMediaPlayer = MyMediaPlayerUtil.getInstance().StarMediaPlayer(mChat.getmAudioPath(), mApp, mApp.getmUseCall());
                mApp.mAudioPath = mChat.getmAudioPath();
                mApp.sdcardLog("play record start");
                mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        if (mChat.getmPlayAnimation() != null) {
                            mChat.getmPlayAnimation().stop();
                            mChat.getmPlayAnimation().selectDrawable(0);
                            mChat.setmIsClick(false);
                        }
                        MyMediaPlayerUtil.getInstance().abandonAudioFocus(mApp);
                        int position = -1;
                        if (-1 == mData.indexOf(mChat)) {
                            return;//播放中被删除，找不到这条语音
                        }
                        for (int i = mData.indexOf(mChat); i < mData.size(); i++) {
                            if (mData.get(i).getmType() != 4 && mData.get(i).getmIsFrom() && !mData.get(i).getmPlayed()) {
                                position = i;
                                break;
                            }
                        }
                        if (position != -1) {
                            Intent it = new Intent(Const.ACTION_PLAY_RECORD_COMPLETION);
                            it.putExtra("position", position);
                            mContext.sendBroadcast(it);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.e("chat path is null ! playRecord");
        }
    }

    private CharSequence getEmojiSequence(String emoji) {
        String html= "<img src='" + emoji + "'/>";
        CharSequence charSequence = Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable;
                if (source.equals(mContext.getString(R.string.emoji_cry))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.cry);
                } else if (source.equals(mContext.getString(R.string.emoji_nose))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.nose);
                } else if (source.equals(mContext.getString(R.string.emoji_basketball))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.basketball);
                } else if (source.equals(mContext.getString(R.string.emoji_snot))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.snot);
                } else if (source.equals(mContext.getString(R.string.emoji_naughty))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.naughty);
                } else if (source.equals(mContext.getString(R.string.emoji_smile))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.smile);
                } else if (source.equals(mContext.getString(R.string.emoji_flower))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.flower);
                } else if (source.equals(mContext.getString(R.string.emoji_laugh))) {
                    drawable = mContext.getResources().getDrawable(R.drawable.laugh);
                } else {
                    drawable = mContext.getResources().getDrawable(R.drawable.moon);
                }
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
        }, null);
        return charSequence;
    }


    private static Bitmap scale(Bitmap bitmap, float size, Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        float fitWidth = (float) width * size / 1080;
        Matrix matrix = new Matrix();
        matrix.postScale(fitWidth,fitWidth); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    private Uri getUriFromFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = ImageUtil.getImageContentUri(context,file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
