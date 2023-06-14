package com.xiaoxun.xun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.xiaoxun.xun.R;

/**
 * Uesr：yaoyonghui on 2019/11/19 11:02
 * Email：yaoyonghui@loogcheer.com
 * Project: trunk_watch_app
 */
public class InfoWinAdapter implements GoogleMap.InfoWindowAdapter, View.OnClickListener {

    private LatLng latLng;
    private String snippet;
    private Context mContext;
    private TextView tv_security_distance;

    @Override
    public void onClick(View v) {

    }

    public InfoWinAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        snippet = marker.getSnippet();
    }

    @NonNull
    private View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_security_pop, null);
        tv_security_distance = (TextView) view.findViewById(R.id.tv_security_distance);

        tv_security_distance.setText(snippet);

        return view;
    }
}
