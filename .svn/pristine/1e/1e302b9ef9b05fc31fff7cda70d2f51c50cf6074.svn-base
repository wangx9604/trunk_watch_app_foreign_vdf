package com.xiaoxun.xun.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;


/**
 * Created by huangqilin on 2017/3/30.
 */

public class SensorMarkerHelper{

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastTime = 0;
    private final int TIME_SENSOR = 100;
    private float mAngle;
    private Context mContext;
    /**
     * 隐藏here sdk
     */
//    private MapLocalModel mMarker;
    private com.google.android.gms.maps.model.Marker mGooglePhoneMarker;
    private SensorEventListener eventListener;

    public SensorMarkerHelper(final Context context) {
        mContext = context;
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
                    return;
                }
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ORIENTATION: {
                        float x = event.values[0];
                        x += getScreenRotationOnPhone(mContext);
                        x %= 360.0F;
                        if (x > 180.0F)
                            x -= 360.0F;
                        else if (x < -180.0F)
                            x += 360.0F;

                        if (Math.abs(mAngle - x) < 3.0f) {
                            break;
                        }
                        mAngle = Float.isNaN(x) ? 0 : x;
                        /**
                         * 隐藏here sdk
                         */
//                        if (mMarker != null) {
//
//                            mMarker.setYaw(360-mAngle);
//                        }
                        if(mGooglePhoneMarker != null){
                            mGooglePhoneMarker.setRotation(360-mAngle);
                        }
                        lastTime = System.currentTimeMillis();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public void registerSensorListener() {
        mSensorManager.registerListener(eventListener, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterSensorListener() {
        mSensorManager.unregisterListener(eventListener, mSensor);
    }

    /**
     * 隐藏here sdk
     */
//    public void setCurrentMarker(MapLocalModel marker) {
//        mMarker = marker;
//    }

    public void setCurrentGoogleMarker(com.google.android.gms.maps.model.Marker marker) {
        mGooglePhoneMarker = marker;
    }
    public static int getScreenRotationOnPhone(Context context) {
        final Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;

            case Surface.ROTATION_90:
                return 90;

            case Surface.ROTATION_180:
                return 180;

            case Surface.ROTATION_270:
                return -90;
        }
        return 0;
    }
}
