package com.xiaoxun.xun.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;

import java.util.LinkedList;

/**
 * Created by huangyouyang on 2017/6/28.
 */

public class NotificationUtils {

    public static boolean isDarkNotificationTheme(Context context) {
        return !isSimilarColor(Color.BLACK, getNotificationColor(context));
    }

    /**
     * 计算颜色接近黑色还是白色
     * R、G、B 的 色差平方根 与 180 相比
     */
    private static boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        //接近黑色
        return value < 180.0;
    }

    /**
     * 获取通知栏颜色
     *
     * @param context
     * @return
     */
    private static int getNotificationColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        int layoutId = notification.contentView.getLayoutId();
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null, false);
        if (viewGroup.findViewById(android.R.id.title) != null) {
            return ((TextView) viewGroup.findViewById(android.R.id.title)).getCurrentTextColor();
        }
        /**
         * TextView textView= (TextView) viewGroup.findViewById(android.R.id.title);
         * 可能在有的手机上获取textview为空，
         * 所以我想notification的文本区域，系统默认肯定有一个值的，那我就直接遍历找到这个值即可
         */
        return findColor(viewGroup);
    }

    public static void startNotificationSetActivity(Activity context) {

        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }

        // 个别机型打不开设置页面，这里加下判断
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            ToastUtil.show(context, context.getString(R.string.able_read_notification_desc_manual));
        }
    }

    public static boolean checkShowNotificationDialog(ImibabyApp mApp) {

        String lastStamp = mApp.getStringValue(Const.SHARE_PREF_FIELD_SHOW_NOTIFY_SET_DIALOG, "");
        if (TextUtils.isEmpty(lastStamp) || TimeUtil.getChatTime(lastStamp) >= 24 * 60 * 60 * 1000) {
            mApp.setValue(Const.SHARE_PREF_FIELD_SHOW_NOTIFY_SET_DIALOG, TimeUtil.getTimeStampLocal());
            return true;
        }
        return false;
    }
    public static boolean checkNotificationAbled(Context context) {

        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
    private static int findColor(ViewGroup viewGroupSource) {
        int color = Color.TRANSPARENT;
        LinkedList<ViewGroup> viewGroups = new LinkedList<>();
        viewGroups.add(viewGroupSource);
        while (viewGroups.size() > 0) {
            ViewGroup viewGroup1 = viewGroups.getFirst();
            for (int i = 0; i < viewGroup1.getChildCount(); i++) {
                if (viewGroup1.getChildAt(i) instanceof ViewGroup) {
                    viewGroups.add((ViewGroup) viewGroup1.getChildAt(i));
                } else if (viewGroup1.getChildAt(i) instanceof TextView) {
                    if (((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor() != -1) {
                        color = ((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor();
                    }
                }
            }
            viewGroups.remove(viewGroup1);
        }
        return color;
    }
}
