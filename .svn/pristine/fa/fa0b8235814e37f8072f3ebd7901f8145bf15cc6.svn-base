/**
 * Creation Date:2015-1-29
 * <p>
 * Copyright
 */
package com.xiaoxun.xun.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.FamilyMemberActivity;
import com.xiaoxun.xun.beans.GeneralMember;
import com.xiaoxun.xun.beans.WatchAllMemberData;
import com.xiaoxun.xun.utils.ImageDownloadHelper;
import com.xiaoxun.xun.utils.ImageUtil;

import static com.xiaoxun.xun.R.string.me;

/**
 * Description Of The Class<br>
 *
 * @author liutianxiang
 * @version 1.000, 2015-1-29
 */
public class FamilyMemberAdapter extends BaseAdapter {
    private WatchAllMemberData mAllMember;
    private LayoutInflater mInflater;
    private FamilyMemberActivity parentContext;

    public FamilyMemberAdapter(WatchAllMemberData allmember, FamilyMemberActivity parent) {
        // TODO Auto-generated constructor stub
        parentContext = parent;
        mInflater = (LayoutInflater) parentContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAllMember = allmember;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int membersize = 0;
        if (mAllMember != null && mAllMember.mGeneralMemberList != null) {
            membersize = mAllMember.mGeneralMemberList.size();
        }
        return membersize;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //需要区分watch 和user

        FamilyMemberView pView = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.family_member_item, null);

            pView = new FamilyMemberView();
            pView.iMemberImg = convertView
                    .findViewById(R.id.iv_faimliy_head);
            pView.iMemberType = convertView
                    .findViewById(R.id.iv_member_type);

            pView.sMemberName = convertView
                    .findViewById(R.id.tv_family_name);
            pView.sMemberPhone = convertView
                    .findViewById(R.id.tv_info);
            pView.btnEdit = convertView
                    .findViewById(R.id.btn_edit);

            pView.titleLayer = convertView.findViewById(R.id.layer_title);
            pView.tilteTxt = convertView.findViewById(R.id.tv_title);
            convertView.setTag(pView);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            pView = (FamilyMemberView) convertView.getTag();
        }
        final GeneralMember memberInfo = mAllMember.mGeneralMemberList.get(position);
        if (memberInfo.type == 0 || memberInfo.type == 1) {
            String name = memberInfo.nickname;
            if (name != null && name.length() > 18) {
                name = name.substring(0, 18) + "...";
            }
            if (memberInfo.eid != null && memberInfo.eid.equals(parentContext.mApp.getCurUser().getEid())) {
                name += "(" + parentContext.getText(me) + ")";
            }

            pView.sMemberName.setText(name);
            //处理主副号码
            StringBuilder bufcell = new StringBuilder();
            if (memberInfo.subnum == null || memberInfo.subnum.length() == 0) {
                if (memberInfo.cellnum != null)
                    bufcell.append(memberInfo.cellnum);
            } else {
                bufcell.append(parentContext.getText(R.string.main_num_tag));
                bufcell.append(memberInfo.cellnum);
                bufcell.append(parentContext.getText(R.string.sprit));
                bufcell.append(parentContext.getText(R.string.sub_num_tag));
                bufcell.append(memberInfo.subnum);
            }
            pView.sMemberPhone.setText(bufcell.toString());
            if (memberInfo.isAdmin == true) {
                pView.iMemberType.setImageResource(R.drawable.administrators_0);
            } else if (memberInfo.eid != null) {
                pView.iMemberType.setImageResource(R.drawable.group_members_0);
            } else {
                pView.iMemberType.setImageResource(R.drawable.call_member_0);
            }

            if (memberInfo.titleStr != null && memberInfo.titleStr.length() > 0) {
                pView.titleLayer.setVisibility(View.VISIBLE);
                pView.tilteTxt.setText(memberInfo.titleStr);
            } else {
                pView.titleLayer.setVisibility(View.GONE);
            }

            if (!memberInfo.isEdit) {
                pView.btnEdit.setVisibility(View.INVISIBLE);
            } else {
                pView.btnEdit.setVisibility(View.VISIBLE);
                pView.btnEdit.setClickable(false);
            }

            final ImageView ivAvatar = pView.iMemberImg;
            if (memberInfo.avatar != null) {
                Bitmap headBitmap = new ImageDownloadHelper(parentContext).downloadImage(memberInfo.avatar, new ImageDownloadHelper.OnImageDownloadListener() {
                    @Override
                    public void onImageDownload(String url, Bitmap bitmap) {
                        Drawable headDrawable = new BitmapDrawable(parentContext.getResources(), bitmap);
                        ImageUtil.setMaskImage(ivAvatar, R.drawable.head_2, headDrawable);
                    }
                });
                if (headBitmap != null) {
                    Drawable headDrawable = new BitmapDrawable(parentContext.getResources(), headBitmap);
                    ImageUtil.setMaskImage(pView.iMemberImg, R.drawable.head_2, headDrawable);
                } else {
                    ImageUtil.setMaskImage(pView.iMemberImg, R.drawable.mask, ((ImibabyApp) parentContext.getApplicationContext()).getHeadDrawableByFile(parentContext.getResources(), Integer.toString(memberInfo.attri), memberInfo.eid, R.drawable.relation_custom));
                }
            } else {
                ImageUtil.setMaskImage(pView.iMemberImg, R.drawable.mask, ((ImibabyApp) parentContext.getApplicationContext()).getHeadDrawableByFile(parentContext.getResources(), Integer.toString(memberInfo.attri), memberInfo.eid, R.drawable.relation_custom));
            }
        } else if (memberInfo.type == 2) {
            if (memberInfo.nickname == null)
                memberInfo.nickname = parentContext.getString(R.string.wechat_notice_bind_nickname);
            pView.sMemberName.setText(memberInfo.nickname);
            pView.sMemberPhone.setText(memberInfo.cellnum);
            if (memberInfo.titleStr != null && memberInfo.titleStr.length() > 0) {
                pView.titleLayer.setVisibility(View.VISIBLE);
                pView.tilteTxt.setText(memberInfo.titleStr);
            } else {
                pView.titleLayer.setVisibility(View.GONE);
            }
            pView.iMemberType.setImageResource(R.drawable.watch_members_0);
            ImageUtil.setMaskImage(pView.iMemberImg, R.drawable.mask, parentContext.getApplicationContext().getResources().getDrawable(R.drawable.default_head));
            if (memberInfo.isEdit) {
                pView.btnEdit.setVisibility(View.VISIBLE);
                pView.btnEdit.setClickable(false);
            } else {
                pView.btnEdit.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    class FamilyMemberView {
        ImageView iMemberImg;
        ImageView iMemberType;
        TextView sMemberName;
        TextView sMemberPhone;

        View titleLayer;
        TextView tilteTxt;
        Button btnEdit;
    }
}
