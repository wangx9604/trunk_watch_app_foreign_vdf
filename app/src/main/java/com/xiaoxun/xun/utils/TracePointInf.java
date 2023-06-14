package com.xiaoxun.xun.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xilvkang on 2016/6/23.
 */
public class TracePointInf implements Parcelable {
    public Double mLat;
    public Double mlng;
    public String mAddressDesc;
    public String mTimeStamp;
    public int iSosType;
    public int iEFIDType;
    // public boolean bGpsPoint;
    public int radius;
    public String type;
    public String efenceName;
    public int inteval = -1;
    public int loctype = 0;
    public Double angle;

    public TracePointInf(){}

    protected TracePointInf(Parcel in) {
        mLat = in.readDouble();
        mlng = in.readDouble();
        mAddressDesc = in.readString();
        mTimeStamp = in.readString();
        iSosType = in.readInt();
        iEFIDType = in.readInt();
        radius = in.readInt();
        type = in.readString();
        efenceName = in.readString();
        inteval = in.readInt();
        loctype = in.readInt();
        angle = in.readDouble();
    }

    public static final Creator<TracePointInf> CREATOR = new Creator<TracePointInf>() {
        @Override
        public TracePointInf createFromParcel(Parcel in) {
            return new TracePointInf(in);
        }

        @Override
        public TracePointInf[] newArray(int size) {
            return new TracePointInf[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(mLat);
        parcel.writeDouble(mlng);
        parcel.writeString(mAddressDesc);
        parcel.writeString(mTimeStamp);
        parcel.writeInt(iSosType);
        parcel.writeInt(iEFIDType);
        parcel.writeInt(radius);
        parcel.writeString(type);
        parcel.writeString(efenceName);
        parcel.writeInt(inteval);
        parcel.writeInt(loctype);
        parcel.writeDouble(angle);
    }
}
