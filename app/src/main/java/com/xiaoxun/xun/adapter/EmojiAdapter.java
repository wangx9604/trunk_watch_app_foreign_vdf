package com.xiaoxun.xun.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoxun.xun.R;
import com.xiaoxun.xun.interfaces.OnItemClickListener;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder> {

    private int[] emojiIds;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public EmojiAdapter(Context context, int[] emojiIds) {
        this.context = context;
        this.emojiIds = emojiIds;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emoji_item, parent, false);
        return new EmojiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder emojiViewHolder, final int position) {
        int emojiId = emojiIds[position];
        emojiViewHolder.emojiView.setImageResource(emojiId);
        emojiViewHolder.emojiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return emojiIds.length;
    }

    class EmojiViewHolder extends RecyclerView.ViewHolder {
        ImageView emojiView;
        EmojiViewHolder(View itemView) {
            super(itemView);
            emojiView = itemView.findViewById(R.id.iv_emoji);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }
}
