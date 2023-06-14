package com.xiaoxun.xun.beans;

public class PhoneNumber {
    public String id;
    public String number;
    public String subNumber;
    public int attri;
    public String timeStampId;
    public String userEid;
    public String userGid;
    public String ring;//提示音的c2e的key
    public String avatar;
    public String nickname;
    public int weight = 0;//权重，越大越优先
    public int contactType = 0;
    @Override
    public String toString() {
        return ("id:" + id + " " +
                "number:" + number + " " +
                "weight:" + weight + " " +
                "attri:" + attri + " " +
                "nickname:" + nickname + " " +
                "userEid:" + userEid + " " +
                "userGid:" + userGid + " " +
                "ring:" + ring + " " +
                "nickname:" + nickname + " " +
                "timeStampId:" + timeStampId +
                "contactType:" + contactType);
    }
}
