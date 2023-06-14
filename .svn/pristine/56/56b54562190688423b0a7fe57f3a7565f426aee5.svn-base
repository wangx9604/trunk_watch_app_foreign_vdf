package com.xiaoxun.xun.ScheduleCard.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxun.xun.R;

import java.util.List;

public class MenuPopUpWindow extends PopupWindow {
    List<ItemData> itemList;
    int mLayoutType;

    public MenuPopUpWindow(Context context, List<ItemData> list, int mLayoutType){
        super();
        itemList = list;
        this.mLayoutType = mLayoutType;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView;
        if(mLayoutType == 0) {
            menuView = inflater.inflate(R.layout.menu_popup_layout, null);
        }else{
            menuView = inflater.inflate(R.layout.schedule_week_popup_layout, null);
        }
        RecyclerView menu_list = menuView.findViewById(R.id.menu_list);
        MenuAdapter adapter = new MenuAdapter(context,itemList);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        menu_list.setLayoutManager(manager);
        menu_list.setAdapter(adapter);

        this.setContentView(menuView);//设置PopupWindow的View
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);//设置PopupWindow弹出窗体的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);//设置PopupWindow弹出窗体的高
        this.setTouchable(true);//设置PopupWindow弹出窗体可点击，默认true
        this.setFocusable(true);// 设置PopupWindow具备获取焦点能力，默认false
    }

    class MenuAdapter extends RecyclerView.Adapter{
        Context context;
        LayoutInflater mInflater;
        List<ItemData> items;
        public MenuAdapter(Context context,List<ItemData> list){
            this.context = context;
            items = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            mInflater = LayoutInflater.from(context);
            RecyclerView.ViewHolder holder;
            if(mLayoutType == 0) {
                holder = new itemHolder(mInflater.inflate(R.layout.menu_popup_item, viewGroup, false));
            }else{
                holder = new itemHolder(mInflater.inflate(R.layout.schedule_week_popup_item, viewGroup, false));
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final ItemData itemData = items.get(i);
            itemHolder holder = (itemHolder)viewHolder;
            if(itemData.getHead_icon() != null) {
                holder.head_icon.setImageDrawable(itemData.getHead_icon());
            }else{
                holder.head_icon.setVisibility(View.GONE);
            }
            holder.title.setText(itemData.getTitle());
            if(itemData.getTail_icon() != null) {
                holder.tail_icon.setImageDrawable(itemData.getTail_icon());
            }else{
                holder.tail_icon.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemData.getListener().onClick();
                }
            });
            if(i == getItemCount()-1){
                holder.divide_line.setVisibility(View.GONE);
            }else{
                holder.divide_line.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return (items == null ? 0 : items.size());
        }
    }

    class itemHolder extends RecyclerView.ViewHolder{
        ImageView head_icon;
        TextView title;
        ImageView tail_icon;
        ImageView divide_line;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            head_icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            tail_icon = itemView.findViewById(R.id.tail_icon);
            divide_line = itemView.findViewById(R.id.divide_line);
        }
    }

    public interface OnItemClickListener{
        void onClick();
    }

    public static class ItemData{
        String title;
        Drawable head_icon;
        Drawable tail_icon;
        OnItemClickListener listener;

        public ItemData(String title,Drawable hId,Drawable tId,OnItemClickListener listener){
            this.title = title;
            this.head_icon = hId;
            this.tail_icon = tId;
            this.listener = listener;
        }

        public String getTitle(){
            return title;
        }
        public Drawable getHead_icon(){
            return head_icon;
        }
        public Drawable getTail_icon(){
            return tail_icon;
        }

        public OnItemClickListener getListener() {
            return listener;
        }
    }
}
