package com.xiaoxun.xun.region.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ThirdUser implements Parcelable {
    private String openId;
    private String unionId;
    private String nickname;
    private String phoneNumber;
    private String accessToken;
    private int type;
    private String headIcon;  //当下只有小米账号登录，该参数做了赋值

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(openId);
        dest.writeString(unionId);
        dest.writeString(nickname);
        dest.writeString(phoneNumber);
        dest.writeString(accessToken);
        dest.writeInt(type);
    }

    public static final Creator<ThirdUser> CREATOR = new Creator<ThirdUser>() {
        @Override
        public ThirdUser createFromParcel(Parcel source) {
            ThirdUser data = new ThirdUser();
            data.openId = source.readString();
            data.unionId = source.readString();
            data.nickname = source.readString();
            data.phoneNumber = source.readString();
            data.accessToken = source.readString();
            data.type = source.readInt();
            return data;
        }

        @Override
        public ThirdUser[] newArray(int size) {
            return new ThirdUser[size];
        }
    };
}
