package com.xiaoxun.xun.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.xiaoxun.xun.Const;
import com.xiaoxun.xun.R;

public class LocationAnimUtils {

    public static MioAsyncTask<String, String, String> getWatchNormalAnimTask(final Context context,final Marker marker){

        return new MioAsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String location_png_type = "coordinate_0";
                try {
                    while (true) {
                        if (location_png_type.equals("coordinate_0")) {
                            Thread.sleep(Const.NORMAl_ANIM_PERIOD_TIME);
                            location_png_type = "coordinate_1";
                        } else if (location_png_type.equals("coordinate_1")) {
                            Thread.sleep(150);
                            location_png_type = "coordinate_0";
                        }
                        publishProgress(location_png_type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onCancelled() {
                super.onCancelled();
            }

            protected void onProgressUpdate(String... values) {
                if (values[0].equals("coordinate_0")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.coordinate_0)));
                } else if (values[0].equals("coordinate_1")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.coordinate_1)));
                }
            }
        };
    }

    public static MioAsyncTask<String, String, String> getWatchWalkAnim(final Context context,final Marker marker) {

        return new MioAsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String location_png_type = "location1";
                try {
                    while (true) {
                        if (location_png_type.equals("location1")) {
                            Thread.sleep(150);
                            location_png_type = "location2";
                        } else if (location_png_type.equals("location2")) {
                            Thread.sleep(150);
                            location_png_type = "location3";
                        } else if (location_png_type.equals("location3")) {
                            Thread.sleep(150);
                            location_png_type = "location4";
                        } else if (location_png_type.equals("location4")) {
                            Thread.sleep(150);
                            location_png_type = "location5";
                        } else if (location_png_type.equals("location5")) {
                            Thread.sleep(150);
                            location_png_type = "location6";
                        } else if (location_png_type.equals("location6")) {
                            Thread.sleep(150);
                            location_png_type = "location7";
                        } else if (location_png_type.equals("location7")) {
                            Thread.sleep(150);
                            location_png_type = "location8";
                        } else if (location_png_type.equals("location8")) {
                            Thread.sleep(150);
                            location_png_type = "location1";
                        }
                        publishProgress(location_png_type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onCancelled() {
                super.onCancelled();
            }

            protected void onPostExecute(String result) {

            }

            protected void onProgressUpdate(String... values) {


                if (values[0].equals("location1")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location1)));
                } else if (values[0].equals("location2")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location2)));
                } else if (values[0].equals("location3")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location3)));
                } else if (values[0].equals("location4")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location4)));
                } else if (values[0].equals("location5")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location5)));
                } else if (values[0].equals("location6")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location6)));
                } else if (values[0].equals("location7")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location7)));
                } else if (values[0].equals("location8")) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.location8)));
                }
            }
        };
    }

}
