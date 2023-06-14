package com.xiaoxun.xun.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;

import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;

public class EmojiUtil {

    public static CharSequence getEmojiSequence(final Context context, String emoji) {
        String html = "<img src='" + emoji + "'/>";
        CharSequence charSequence = Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                Drawable drawable;
                if (source.equals(Constants.EMOJI_CRY)) {
                    drawable = context.getResources().getDrawable(R.drawable.cry);
                } else if (source.equals(Constants.EMOJI_NOSE)) {
                    drawable = context.getResources().getDrawable(R.drawable.nose);
                } else if (source.equals(Constants.EMOJI_BASKETBALL)) {
                    drawable = context.getResources().getDrawable(R.drawable.basketball);
                } else if (source.equals(Constants.EMOJI_SNOT)) {
                    drawable = context.getResources().getDrawable(R.drawable.snot);
                } else if (source.equals(Constants.EMOJI_NAUGHTY)) {
                    drawable = context.getResources().getDrawable(R.drawable.naughty);
                } else if (source.equals(Constants.EMOJI_SMILE)) {
                    drawable = context.getResources().getDrawable(R.drawable.smile);
                } else if (source.equals(Constants.EMOJI_FLOWER)) {
                    drawable = context.getResources().getDrawable(R.drawable.flower);
                } else if (source.equals(Constants.EMOJI_LAUGH)) {
                    drawable = context.getResources().getDrawable(R.drawable.laugh);
                } else if (source.equals(Constants.EMOJI_MOON)) {
                    drawable = context.getResources().getDrawable(R.drawable.moon);
                } else if (source.equals(Constants.EMOJI_RABBIT_ANGER)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_anger);
                } else if (source.equals(Constants.EMOJI_RABBIT_AWKWARD)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_awkward);
                } else if (source.equals(Constants.EMOJI_RABBIT_BAD_LAUGH)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_bad_laugh);
                } else if (source.equals(Constants.EMOJI_RABBIT_CRY)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_cry);
                } else if (source.equals(Constants.EMOJI_RABBIT_CUTE)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_cute);
                } else if (source.equals(Constants.EMOJI_RABBIT_DESPISE)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_despise);
                } else if (source.equals(Constants.EMOJI_RABBIT_HAPPY)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_happy);
                } else if (source.equals(Constants.EMOJI_RABBIT_LOVE)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_love);
                } else if (source.equals(Constants.EMOJI_RABBIT_SHY)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_shy);
                } else if (source.equals(Constants.EMOJI_RABBIT_SMILE)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_smile);
                } else if (source.equals(Constants.EMOJI_RABBIT_SURPRISED)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_surprised);
                } else if (source.equals(Constants.EMOJI_RABBIT_ZIBI)) {
                    drawable = context.getResources().getDrawable(R.drawable.rabbit_zibi);
                } else {
                    drawable = context.getResources().getDrawable(R.drawable.smile);
                }
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
        }, null);
        return charSequence;
    }

    public static String getEmojiText(int id) {
        String emojiText = "";
        switch (id) {
            case R.drawable.rabbit_anger:
                emojiText = Constants.EMOJI_RABBIT_ANGER;
                break;
            case R.drawable.rabbit_awkward:
                emojiText = Constants.EMOJI_RABBIT_AWKWARD;
                break;
            case R.drawable.rabbit_bad_laugh:
                emojiText = Constants.EMOJI_RABBIT_BAD_LAUGH;
                break;
            case R.drawable.rabbit_cry:
                emojiText = Constants.EMOJI_RABBIT_CRY;
                break;
            case R.drawable.rabbit_cute:
                emojiText = Constants.EMOJI_RABBIT_CUTE;
                break;
            case R.drawable.rabbit_despise:
                emojiText = Constants.EMOJI_RABBIT_DESPISE;
                break;
            case R.drawable.rabbit_happy:
                emojiText = Constants.EMOJI_RABBIT_HAPPY;
                break;
            case R.drawable.rabbit_love:
                emojiText = Constants.EMOJI_RABBIT_LOVE;
                break;
            case R.drawable.rabbit_shy:
                emojiText = Constants.EMOJI_RABBIT_SHY;
                break;
            case R.drawable.rabbit_smile:
                emojiText = Constants.EMOJI_RABBIT_SMILE;
                break;
            case R.drawable.rabbit_surprised:
                emojiText = Constants.EMOJI_RABBIT_SURPRISED;
                break;
            case R.drawable.rabbit_zibi:
                emojiText = Constants.EMOJI_RABBIT_ZIBI;
                break;
            case R.drawable.cry:
                emojiText = Constants.EMOJI_CRY;
                break;
            case R.drawable.nose:
                emojiText = Constants.EMOJI_NOSE;
                break;
            case R.drawable.basketball:
                emojiText = Constants.EMOJI_BASKETBALL;
                break;
            case R.drawable.snot:
                emojiText = Constants.EMOJI_SNOT;
                break;
            case R.drawable.naughty:
                emojiText = Constants.EMOJI_NAUGHTY;
                break;
            case R.drawable.smile:
                emojiText = Constants.EMOJI_SMILE;
                break;
            case R.drawable.flower:
                emojiText = Constants.EMOJI_FLOWER;
                break;
            case R.drawable.laugh:
                emojiText = Constants.EMOJI_LAUGH;
                break;
            case R.drawable.moon:
                emojiText = Constants.EMOJI_MOON;
                break;
        }
        return emojiText;
    }

    public static boolean isEmojiText(String text) {
        switch (text) {
            case  Constants.EMOJI_CRY:
            case  Constants.EMOJI_NOSE:
            case  Constants.EMOJI_BASKETBALL:
            case  Constants.EMOJI_SNOT:
            case  Constants.EMOJI_NAUGHTY:
            case  Constants.EMOJI_SMILE:
            case  Constants.EMOJI_FLOWER:
            case  Constants.EMOJI_LAUGH:
            case  Constants.EMOJI_MOON:
            case  Constants.EMOJI_RABBIT_ANGER:
            case  Constants.EMOJI_RABBIT_AWKWARD:
            case  Constants.EMOJI_RABBIT_BAD_LAUGH:
            case  Constants.EMOJI_RABBIT_CRY:
            case  Constants.EMOJI_RABBIT_CUTE:
            case  Constants.EMOJI_RABBIT_DESPISE:
            case  Constants.EMOJI_RABBIT_HAPPY:
            case  Constants.EMOJI_RABBIT_LOVE:
            case  Constants.EMOJI_RABBIT_SHY:
            case  Constants.EMOJI_RABBIT_SMILE:
            case  Constants.EMOJI_RABBIT_SURPRISED:
            case  Constants.EMOJI_RABBIT_ZIBI:
                return true;
        }
        return false;
    }
}
