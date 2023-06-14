package com.xiaoxun.xun.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NewMainActivity;

import java.util.ArrayList;

/**
 * @author cuiyufeng
 * @Description: HorizontalListViewAdapter
 * @date 2018/10/18 15:01
 */
public class HorizontalListViewAdapter extends BaseAdapter {

    private final Context context;
    private final int screenWidth;
    ArrayList<NewMainActivity.PageData> mPageData;
    private int selectIndex = -1;

    public HorizontalListViewAdapter(Context applicationContext,int screenWidth, ArrayList<NewMainActivity.PageData> mPageData) {
        this.context = applicationContext;
        this.mPageData = mPageData;
        this.screenWidth=screenWidth;
    }

    @Override
    public int getCount() {
        return mPageData.size();
    }

    @Override
    public Object getItem(int position) {
        return mPageData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context.getApplicationContext(), R.layout.main_page_item, null);
            holder = new ViewHolder();
            holder.rout_main_pagetab= convertView.findViewById(R.id.rout_main_pagetab);
            holder.item_main_icon = convertView.findViewById(R.id.item_main_icon);
            holder.iv_new_msg_bg = convertView.findViewById(R.id.iv_new_msg_bg);
            holder.tv_new_msg_count = convertView.findViewById(R.id.tv_new_msg_count);
            holder.title = convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //计算每条的宽度
        ViewGroup.LayoutParams layoutParams = holder.rout_main_pagetab.getLayoutParams();
        layoutParams.width = screenWidth/mPageData.size();  // / 5 * 2
        holder.rout_main_pagetab.setLayoutParams(layoutParams);
        NewMainActivity.PageData pageData=mPageData.get(position);

        if(position == selectIndex){
            holder.item_main_icon.setBackgroundResource(pageData.mSeResourceId);
        }else{
            holder.item_main_icon.setBackgroundResource(pageData.mUnseResourceId);
        }

        if(position == position_setting && islogin){
            if(isShowSettingRedPoint){
                holder.iv_new_msg_bg.setVisibility(View.VISIBLE);
                holder.iv_new_msg_bg.setBackgroundResource(R.drawable.red_single);
            }else {
                holder.iv_new_msg_bg.setVisibility(View.GONE);
            }
        }

        if(position == position_find && islogin){
            if (redDotCount_find != 0) {
                holder.iv_new_msg_bg.setVisibility(View.VISIBLE);
                holder.iv_new_msg_bg.setBackgroundResource(R.drawable.red_single);
            } else {
                holder.iv_new_msg_bg.setVisibility(View.GONE);
            }
        }

        if(position == position_concact && islogin){
            if (redDotCount_concact != 0) {
                holder.tv_new_msg_count.setVisibility(View.VISIBLE);
                holder.iv_new_msg_bg.setVisibility(View.VISIBLE);
                holder.tv_new_msg_count.setText(redDotCount_concact + "");
                if (redDotCount_concact > 99) {
                    holder.tv_new_msg_count.setText("");
                    holder.iv_new_msg_bg.setBackgroundResource(R.drawable.red_more);
                } else if (redDotCount_concact > 9) {
                    holder.iv_new_msg_bg.setBackgroundResource(R.drawable.red_double);
                } else {
                    holder.iv_new_msg_bg.setBackgroundResource(R.drawable.red_single);
                }
            } else {
                holder.tv_new_msg_count.setVisibility(View.GONE);
                holder.iv_new_msg_bg.setVisibility(View.GONE);
            }
        }

        holder.title.setText(pageData.mTitle);
        return convertView;
    }

    class ViewHolder {
        ImageView item_main_icon,iv_new_msg_bg;
        TextView tv_new_msg_count,title;
        RelativeLayout rout_main_pagetab;
    }

    public void setSelectIndex(int i){
        selectIndex = i;
    }

    //发现小红点
    private int position_find = -1;
    private int redDotCount_find = 0;
    private boolean islogin;

    public void setRedDotFind(int position_find ,int redDotCount_find , boolean islogin){
        this.position_find=position_find;
        this.redDotCount_find=redDotCount_find;
        this.islogin= islogin;
        notifyDataSetChanged();
    }

    //联系小红点
    private int position_concact = -1;
    private int redDotCount_concact = 0;
    public void SetRedDotConcact(int position_concact,int redDotCount_concact, boolean islogin){
        this.position_concact=position_concact;
        this.redDotCount_concact=redDotCount_concact;
        this.islogin= islogin;
        notifyDataSetChanged();
    }

    /**
     * position_setting 红点位置
     * isShowSettingRedPoint 是否显示
     * islogin 是否登录
     */
    private int position_setting = -1;
    private boolean isShowSettingRedPoint;
    public void settingRedPoint(int position_setting ,boolean isShowSettingRedPoint, boolean islogin){
        this.position_setting=position_setting;
        this.isShowSettingRedPoint=isShowSettingRedPoint;
        this.islogin= islogin;
        notifyDataSetChanged();
    }

}

