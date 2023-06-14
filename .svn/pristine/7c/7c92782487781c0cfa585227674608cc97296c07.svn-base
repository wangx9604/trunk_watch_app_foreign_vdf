package com.xiaoxun.xun.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.CallLogActivity;
import com.xiaoxun.xun.activitys.ChatPopupWindow;
import com.xiaoxun.xun.activitys.GroupMessageActivity;
import com.xiaoxun.xun.activitys.NoticeTypeActivity;
import com.xiaoxun.xun.activitys.PrivateMessageActivity;
import com.xiaoxun.xun.beans.FamilyData;
import com.xiaoxun.xun.beans.MessageItemData;
import com.xiaoxun.xun.beans.NoticeMsgData;
import com.xiaoxun.xun.beans.WatchData;
import com.xiaoxun.xun.utils.CloudBridgeUtil;
import com.xiaoxun.xun.utils.EmojiUtil;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;
import com.xiaoxun.xun.utils.LogUtil;
import com.xiaoxun.xun.utils.TimeUtil;

import net.minidev.json.JSONObject;

import java.util.List;


/**
 * Created by guxiaolong on 2016/12/13.
 */

public class AllMessageAdapter extends RecyclerView.Adapter<AllMessageAdapter.AllMessageHolder> {
    private List<MessageItemData> mMessageList;
    private Activity mContext;
    private ImibabyApp mApp;
    private ChatPopupWindow menuWindow;

    public static final int MESSAGE_TYPE_GROUP = 0;
    public static final int MESSAGE_TYPE_PRIVATE = 1;
    public static final int MESSAGE_TYPE_NOTICE = 2;
    public static final int MESSAGE_TYPE_CALLLOG = 3;
    public static final int MESSAGE_TYPE_LOCATION = 4;
    public static final int MESSAGE_TYPE_BATTERY = 5;
    public static final int MESSAGE_TYPE_STEPS = 6;
    public static final int MESSAGE_TYPE_FAMILY_MEMBER = 7;
    public static final int MESSAGE_TYPE_SMS = 8;
    public static final int MESSAGE_TYPE_DOWNLOAD = 9;
    public static final int MESSAGE_TYPE_ALL = 10;
    public static final int MESSAGE_TYPE_SYSTEM = 11;
    public static final String MESSAGE_TYPE = "notice_type";

    public AllMessageAdapter(Activity activity, List<MessageItemData> messageList) {
        mContext = activity;
        mApp = (ImibabyApp) activity.getApplication();
        mMessageList = messageList;
    }

    @Override
    public AllMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_message_item, parent, false);
        return new AllMessageHolder(view);
    }

    @Override
    public void onBindViewHolder(AllMessageHolder holder, final int position) {
        final MessageItemData item = mMessageList.get(position);
        final int messageType = item.getType();
        holder.mMessageTitle.setText(item.getTitle());
        if (item.getTop() == 1) {
            holder.mMessageItemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.notice));
        } else {
            holder.mMessageItemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        final String watchEid = item.getWatchEid();
        WatchData watch = mApp.getCurUser().queryWatchDataByEid(watchEid);
        if(watch == null) return;
        switch (messageType) {
            case MESSAGE_TYPE_GROUP:
                setGroupMessageHead(holder.mMessageHead, watch);
                if (mApp.getHasNewGroupMsg(watch.getFamilyId())) {
                    int newMsgCount = mApp.getNewGroupMsgCount(watch.getFamilyId());
                    holder.mNewMessageCount.setVisibility(View.VISIBLE);
                    holder.mNewMsgBackground.setVisibility(View.VISIBLE);
                    holder.mNewMessageCount.setText(newMsgCount + "");
                    if (newMsgCount > 99) {
                        holder.mNewMessageCount.setText("");
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_more);
                    } else if (newMsgCount > 9) {
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_double);
                    } else {
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_single);
                    }

                } else {
                    holder.mNewMessageCount.setVisibility(View.GONE);
                    holder.mNewMsgBackground.setVisibility(View.GONE);
                }

                if (item.getContent() != null) {
                    holder.mMessageContent.setVisibility(View.VISIBLE);
                    if (EmojiUtil.isEmojiText(item.getContent())) {
                        holder.mMessageContent.setText(EmojiUtil.getEmojiSequence(mContext,item.getContent()));
                    } else {
                        holder.mMessageContent.setText(item.getContent());
                    }
                    if (item.getTime() != null) {
                        holder.mMessageTime.setVisibility(View.VISIBLE);
                        LogUtil.e("AllMsgTime  = "+TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())) +  "  time  = " + item.getTime());
                        holder.mMessageTime.setText(TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())));
                    } else {
                        holder.mMessageTime.setVisibility(View.GONE);
                    }
                } else {
                    holder.mMessageContent.setVisibility(View.GONE);
                    holder.mMessageTime.setVisibility(View.GONE);
                }
                holder.mNoticeDisable.setVisibility(View.GONE);
                break;
            case MESSAGE_TYPE_PRIVATE:
                ImageUtil.setMaskImage(holder.mMessageHead, R.drawable.head_0,
                        mApp.getHeadDrawableByFile(mContext.getResources(), watch.getHeadPath(), watch.getEid(), R.drawable.default_head));
                String userGid = mApp.getWatchPrivateGid(watch.getEid());
                if (mApp.getHasNewPrivateMsg(userGid)) {
                    holder.mNewMessageCount.setVisibility(View.VISIBLE);
                    holder.mNewMsgBackground.setVisibility(View.VISIBLE);
                    int newMsgCount = mApp.getNewPrivateMsgCount(userGid);
                    holder.mNewMessageCount.setText(newMsgCount + "");
                    if (newMsgCount > 99) {
                        holder.mNewMessageCount.setText("");
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_more);
                    } else if (newMsgCount > 9) {
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_double);
                    } else {
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_single);
                    }
                } else {
                    holder.mNewMessageCount.setVisibility(View.GONE);
                    holder.mNewMsgBackground.setVisibility(View.GONE);
                }
                if (item.getContent() != null) {
                    holder.mMessageContent.setVisibility(View.VISIBLE);
                    if (EmojiUtil.isEmojiText(item.getContent())) {
                        holder.mMessageContent.setText(EmojiUtil.getEmojiSequence(mContext,item.getContent()));
                    } else {
                        holder.mMessageContent.setText(item.getContent());
                    }
                    if (item.getTime() != null) {
                        holder.mMessageTime.setVisibility(View.VISIBLE);
                        LogUtil.e("AllMsgTime  = "+TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())) +  "  time  = " + item.getTime());
                        holder.mMessageTime.setText(TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())));
                    } else {
                        holder.mMessageTime.setVisibility(View.GONE);
                    }
                } else {
                    holder.mMessageContent.setVisibility(View.GONE);
                    holder.mMessageTime.setVisibility(View.GONE);
                }
                holder.mNoticeDisable.setVisibility(View.GONE);

                break;
            case MESSAGE_TYPE_NOTICE:
            case MESSAGE_TYPE_BATTERY:
            case MESSAGE_TYPE_LOCATION:
            case MESSAGE_TYPE_STEPS:
            case MESSAGE_TYPE_FAMILY_MEMBER:
            case MESSAGE_TYPE_SMS:
            case MESSAGE_TYPE_DOWNLOAD:

                if (messageType == MESSAGE_TYPE_NOTICE) {
                    holder.mMessageHead.setImageResource(R.drawable.notice);
                } else if (messageType == MESSAGE_TYPE_LOCATION) {
                    holder.mMessageHead.setImageResource(R.drawable.location_notice);
                } else if (messageType == MESSAGE_TYPE_BATTERY) {
                    holder.mMessageHead.setImageResource(R.drawable.electricity_notice);
                } else if (messageType == MESSAGE_TYPE_STEPS) {
                    holder.mMessageHead.setImageResource(R.drawable.step_notice);
                } else if (messageType == MESSAGE_TYPE_FAMILY_MEMBER) {
                    holder.mMessageHead.setImageResource(R.drawable.member_notice);
                } else if (messageType == MESSAGE_TYPE_SMS) {
                    holder.mMessageHead.setImageResource(R.drawable.sms_notice);
                } else if (messageType == MESSAGE_TYPE_DOWNLOAD) {
                    holder.mMessageHead.setImageResource(R.drawable.story_notice);
                }
                if (TextUtils.isEmpty(item.getOfflineType())) {
                    holder.mNoticeDisable.setVisibility(View.GONE);
                } else {
                    JSONObject noticeSetting = mApp.getNoticeSetting(watch.getEid());
                    if ("0".equals((String) noticeSetting.get(item.getOfflineType()))) {
                        holder.mNoticeDisable.setVisibility(View.VISIBLE);
                    } else {
                        holder.mNoticeDisable.setVisibility(View.GONE);
                    }
                }
                boolean hasNewMessage = false;
                int newMsgCount = 0;
                if (messageType == MESSAGE_TYPE_NOTICE) {
                    hasNewMessage = mApp.getHasNewNoticeMsg(watch.getFamilyId());
                    newMsgCount = mApp.getNewNoticeMsgCount(watch.getFamilyId());
                } else {
                    hasNewMessage = mApp.getHasNewNoticeMsg(watch.getFamilyId(), messageType);
                    newMsgCount = mApp.getNewNoticeMsgCount(watch.getFamilyId(), messageType);
                }
                if (hasNewMessage) {
                    holder.mNewMessageCount.setVisibility(View.VISIBLE);
                    holder.mNewMsgBackground.setVisibility(View.VISIBLE);

                    holder.mNewMessageCount.setText(newMsgCount + "");
                    if (newMsgCount > 99) {
                        holder.mNewMessageCount.setText("");
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_more);
                    } else if (newMsgCount > 9) {
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_double);
                    } else {
                        holder.mNewMsgBackground.setBackgroundResource(R.drawable.red_single);
                    }
                } else {
                    holder.mNewMessageCount.setVisibility(View.GONE);
                    holder.mNewMsgBackground.setVisibility(View.GONE);
                }

                if (item.getContent() != null) {
                    holder.mMessageContent.setVisibility(View.VISIBLE);
                    holder.mMessageContent.setText(item.getContent());
                    if (item.getTime() != null) {
                        holder.mMessageTime.setVisibility(View.VISIBLE);
                        LogUtil.e("AllMsgTime  = "+TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())) +  "  time  = " + item.getTime());
                        holder.mMessageTime.setText(TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())));
                    } else {
                        holder.mMessageTime.setVisibility(View.GONE);
                    }
                } else {
                    holder.mMessageContent.setVisibility(View.GONE);
                    holder.mMessageTime.setVisibility(View.GONE);
                }

                break;
            case MESSAGE_TYPE_CALLLOG:
                if (watch.isDevice102()) {
                    holder.mMessageItemLayout.setVisibility(View.GONE);
                } else {
                    holder.mMessageItemLayout.setVisibility(View.VISIBLE);
                }
                holder.mMessageHead.setImageResource(R.drawable.call_member_head);

                holder.mNewMessageCount.setVisibility(View.GONE);
                holder.mNewMsgBackground.setVisibility(View.GONE);

                if (item.getContent() != null) {
                    holder.mMessageContent.setVisibility(View.VISIBLE);
                    holder.mMessageContent.setText(item.getContent());
                    if (item.getTime() != null) {
                        holder.mMessageTime.setVisibility(View.VISIBLE);
                        LogUtil.e("AllMsgTime  = "+TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())) +  "  time  = " + item.getTime());
                        holder.mMessageTime.setText(TimeUtil.getAllMsgTime(mContext,TimeUtil.chnToLocalTimestamp(item.getTime())));
                    } else {
                        holder.mMessageTime.setVisibility(View.GONE);
                    }
                } else {
                    holder.mMessageContent.setVisibility(View.GONE);
                    holder.mMessageTime.setVisibility(View.GONE);
                }

                break;
            default:
                break;
        }

        holder.mMessageItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (messageType) {
                    case MESSAGE_TYPE_GROUP:
                        Intent voiceIntent = new Intent(mContext, GroupMessageActivity.class);
                        voiceIntent.putExtra(Const.KEY_WATCH_ID, item.getWatchEid());
                        mContext.startActivity(voiceIntent);
                        break;
                    case MESSAGE_TYPE_PRIVATE:
                        Intent privateIntent = new Intent(mContext, PrivateMessageActivity.class);
                        privateIntent.putExtra(Const.KEY_WATCH_ID, item.getWatchEid());
                        privateIntent.putExtra(CloudBridgeUtil.KEY_NAME_CONTACT_USER_GID, mApp.getWatchPrivateGid(item.getWatchEid()));
                        mContext.startActivity(privateIntent);
                        break;
                    case MESSAGE_TYPE_NOTICE:
                        Intent noticeIntent = new Intent(mContext, NoticeTypeActivity.class);
                        noticeIntent.putExtra(Const.KEY_WATCH_ID, watchEid);
                        noticeIntent.putExtra(AllMessageAdapter.MESSAGE_TYPE, AllMessageAdapter.MESSAGE_TYPE_ALL);
                        mContext.startActivity(noticeIntent);
                        break;
                    case MESSAGE_TYPE_CALLLOG:
                        Intent it = new Intent(mContext, CallLogActivity.class);
                        it.putExtra(Const.KEY_WATCH_ID, watchEid);
                        mContext.startActivity(it);
                        break;
                    case MESSAGE_TYPE_SYSTEM:
                    case MESSAGE_TYPE_LOCATION:
                    case MESSAGE_TYPE_BATTERY:
                    case MESSAGE_TYPE_FAMILY_MEMBER:
                    case MESSAGE_TYPE_STEPS:
                    case MESSAGE_TYPE_SMS:
                    case MESSAGE_TYPE_DOWNLOAD:
                        Intent noticeTypeIntent = new Intent(mContext, NoticeTypeActivity.class);
                        noticeTypeIntent.putExtra(Const.KEY_WATCH_ID, watchEid);
                        noticeTypeIntent.putExtra(AllMessageAdapter.MESSAGE_TYPE, messageType);
                        mContext.startActivity(noticeTypeIntent);
                        break;
                    default:
                        break;
                }
            }
        });
        holder.mMessageItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String title;
                final int top;
                if (item.getTop() > 0) {
                    title = mContext.getString(R.string.cancel_top) + item.getTitle();
                    top = 0;
                } else {
                    title = mContext.getString(R.string.set_top) + item.getTitle();
                    top = 1;
                }
                menuWindow = new ChatPopupWindow(mContext, title, new View.OnClickListener() {
                    public void onClick(View v) {
                        menuWindow.dismiss();
                        mApp.setValue(Const.SHARE_PREF_TOP_MESSAGE_INFO + mApp.getCurUser().getEid() + watchEid + item.getType(), top);
                        mContext.sendBroadcast(new Intent(Const.ACTION_REFRESH_MESSAGE_LIST));
                    }
                });
                menuWindow.showAtLocation(mContext.findViewById(R.id.activity_new_main), Gravity.CENTER, 0, 0);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private void setGroupMessageHead(ImageView imageView, WatchData watch) {
        FamilyData family = mApp.getCurUser().queryFamilyByGid(watch.getFamilyId());
        Bitmap[] bitmaps;
        Bitmap mask = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.head_0);
        Bitmap watchHead;
        try {
            watchHead = ImageUtil.getMaskBitmap(mask,
                    mApp.getHeadDrawableByFile(mContext.getResources(),
                            watch.getHeadPath(),
                            watch.getEid(),
                            R.drawable.default_head));
        }catch(Exception e){
            Drawable drawable = new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_head));
            watchHead = ImageUtil.getMaskBitmap(mask,drawable);
        }
        if (family == null) {
            imageView.setImageBitmap(watchHead);
            return;
        }

        if (family.getMemberList().size() == 1) {
            bitmaps = new Bitmap[2];
            bitmaps[0] = watchHead;
            int attri = mApp.getUserAttriByEid(watch.getEid(), family.getMemberList().get(0).getEid());
            String avatar = mApp.getUserAvatarByEid(watch.getEid(), family.getMemberList().get(0).getEid());
            try {
                Drawable drawable = mApp.getHeadDrawableByFile(mContext.getResources(),
                        Integer.toString(attri),
                        family.getMemberList().get(0).getEid(),
                        R.drawable.relation_custom);
                if (avatar != null) {
                    Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                        @Override
                        public void onImageDownload(String url, Bitmap bitmap) {
                        }
                    });
                    if (headBitmap != null) {
                        drawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                    }
                }
                bitmaps[1] = ImageUtil.getMaskBitmap(mask, drawable);
            } catch (Exception e) {
                return;
            }
        } else if (family.getMemberList().size() == 2) {
            bitmaps = new Bitmap[3];
            bitmaps[0] = watchHead;
            for (int i = 0; i < family.getMemberList().size(); i++) {
                int attri = mApp.getUserAttriByEid(watch.getEid(), family.getMemberList().get(i).getEid());
                String avatar = mApp.getUserAvatarByEid(watch.getEid(), family.getMemberList().get(i).getEid());
                try {
                    Drawable drawable = mApp.getHeadDrawableByFile(mContext.getResources(),
                            Integer.toString(attri),
                            family.getMemberList().get(i).getEid(),
                            R.drawable.relation_custom);
                    if (avatar != null) {
                        Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                            @Override
                            public void onImageDownload(String url, Bitmap bitmap) {
                            }
                        });
                        if (headBitmap != null) {
                            drawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                        }
                    }
                    bitmaps[i + 1] = ImageUtil.getMaskBitmap(mask, drawable);
                } catch (Exception e) {
                    return;
                }
            }
        } else {
            bitmaps = new Bitmap[4];
            bitmaps[0] = watchHead;
            for (int i = 0; i < 3; i++) {
                int attri = mApp.getUserAttriByEid(watch.getEid(), family.getMemberList().get(i).getEid());
                String avatar = mApp.getUserAvatarByEid(watch.getEid(), family.getMemberList().get(i).getEid());
                try {
                    Drawable drawable = mApp.getHeadDrawableByFile(mContext.getResources(),
                            Integer.toString(attri),
                            family.getMemberList().get(i).getEid(),
                            R.drawable.relation_custom);
                    if (avatar != null) {
                        Bitmap headBitmap = new ImageDownloadHelper(mContext).downloadImage(avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                            @Override
                            public void onImageDownload(String url, Bitmap bitmap) {
                            }
                        });
                        if (headBitmap != null) {
                            drawable = new BitmapDrawable(mContext.getResources(), headBitmap);
                        }
                    }
                    bitmaps[i + 1] = ImageUtil.getMaskBitmap(mask, drawable);
                } catch (Exception e) {
                    return;
                }
            }
        }
        ImageUtil.setGroupMaskImage(imageView, R.drawable.group_message_mask, bitmaps);
    }

    class AllMessageHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mMessageItemLayout;
        private ImageView mMessageHead;
        private TextView mNewMessageCount;
        private ImageView mNewMsgBackground;
        private TextView mMessageTitle;
        private TextView mMessageContent;
        private TextView mMessageTime;
        private ImageView mNoticeDisable;

        public AllMessageHolder(View itemView) {
            super(itemView);
            mMessageItemLayout = itemView.findViewById(R.id.layout_message_item);
            mMessageHead = itemView.findViewById(R.id.iv_message_head);
            mNewMessageCount = itemView.findViewById(R.id.tv_new_msg_count);
            mNewMsgBackground = itemView.findViewById(R.id.iv_new_msg_bg);
            mMessageTitle = itemView.findViewById(R.id.tv_message_title);
            mMessageContent = itemView.findViewById(R.id.tv_message_content);
            mMessageTime = itemView.findViewById(R.id.tv_message_time);
            mNoticeDisable = itemView.findViewById(R.id.iv_notice_disable);
        }
    }
    public static int noticeMsgTypeToNoticeType(int noticeMsgType) {
        if (noticeMsgType == NoticeMsgData.MSG_TYPE_FAMILY_CHANGE) {
            return MESSAGE_TYPE_FAMILY_MEMBER;
        } else if (noticeMsgType == NoticeMsgData.MSG_TYPE_SOS_LOCATION || noticeMsgType == NoticeMsgData.MSG_TYPE_SAFE_AREA ||noticeMsgType == NoticeMsgData.MSG_TYPE_SAFE_DANGER_DRAW
                || noticeMsgType == NoticeMsgData.MSG_TYPE_NAVIGATION) {
            return MESSAGE_TYPE_LOCATION;
        } else if (noticeMsgType == NoticeMsgData.MSG_TYPE_STAEPS || noticeMsgType == NoticeMsgData.MSG_TYPE_STAEPSRANKS) {
            return MESSAGE_TYPE_STEPS;
        } else if (noticeMsgType == NoticeMsgData.MSG_TYPE_SMS || noticeMsgType == NoticeMsgData.MSG_TYPE_CHANGE_SIM ||
                noticeMsgType == NoticeMsgData.MSG_TYPE_FLOWMETER || noticeMsgType == NoticeMsgData.MSG_TYPE_OTA_UPGRADE
                || noticeMsgType == NoticeMsgData.MSG_TYPE_OTA_UPGRADE_EX) {
            return MESSAGE_TYPE_SMS;
        } else if (noticeMsgType == NoticeMsgData.MSG_TYPE_BATTERY_WARNNING) {
            return MESSAGE_TYPE_BATTERY;
        } else if (noticeMsgType == NoticeMsgData.MSG_TYPE_DOWNLOAD || noticeMsgType == NoticeMsgData.MSG_TYPE_CLOUD_SPACE
                || noticeMsgType == NoticeMsgData.MSG_TYPE_STORY) {
            return MESSAGE_TYPE_DOWNLOAD;
        } else if (noticeMsgType == NoticeMsgData.MSG_TYPE_SYSTEM){
            return MESSAGE_TYPE_SYSTEM;
        } else {
            return MESSAGE_TYPE_ALL;
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
}
