package com.xiaoxun.xun.utils;

import android.app.Service;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorUtil {


    public static void startVibrate(Context context) {

        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator())
            return;
        long[] pattern = new long[]{1000, 1000, 1000, 1000};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, 0);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build();
            vibrator.vibrate(vibrationEffect, audioAttributes);
        } else {
            vibrator.vibrate(pattern, 0);
        }
    }

    public static void cancelVibrate(Context context) {

        Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if(vibrator==null || !vibrator.hasVibrator())
            return;
        vibrator.cancel();
    }
}
