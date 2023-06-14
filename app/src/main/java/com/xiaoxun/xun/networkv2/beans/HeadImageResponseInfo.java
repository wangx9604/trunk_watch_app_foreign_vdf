package com.xiaoxun.xun.networkv2.beans;

public class HeadImageResponseInfo extends BaseResponseInfo{

    String nickname;
    String head_image_date;

    public HeadImageResponseInfo(int code) {
        super(code);
    }

    public HeadImageResponseInfo(int code, String nickname, String head_image_date) {
        super(code);
        this.nickname = nickname;
        this.head_image_date = head_image_date;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_image_date() {
        return head_image_date;
    }

    public void setHead_image_date(String head_image_date) {
        this.head_image_date = head_image_date;
    }
}
