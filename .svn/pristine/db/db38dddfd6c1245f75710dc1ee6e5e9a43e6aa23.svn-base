package com.xiaoxun.xun.networkv2.beans;
/*
{"msg":"操作成功","code":1,"data":[{"data":{"distance":"2000","etime":"20220418185153000","avgspeed":"0","calorie":"180","stime":"20220418185015000","velocity":"300","istest":"1","testscore":"80","type":5},"time":"20220418185015000","type":5},{"data":{"etime":"20220418185153000","continuous":"20","count":"100","avgspeed":"0.2","calorie":"180","stime":"20220418185015000","istest":"1","testscore":"80","type":6},"time":"20220418185015000","type":6},{"data":{"distance":"2000","etime":"20220418185153000","calorie":"180","stime":"20220418185015000","type":7},"time":"20220418185015000","type":7},{"data":{"distance":"2000","etime":"20220418185153000","calorie":"180","avgspeed":"0","stime":"20220418185015000","type":8},"time":"20220418185015000","type":8},{"data":{"distance":"2000","etime":"20220418185153000","calorie":"180","avgspeed":"0","stime":"20220418185015000","velocity":"300","type":9},"time":"20220418185015000","type":9},{"data":{"distance":"2000","etime":"20220418185153000","calorie":"180","avgspeed":"0","stime":"20220418185015000","downhigh":"57","type":10,"uphigh":"50"},"time":"20220418185015000","type":10},{"data":{"etime":"20220418185153000","count":"100","avgspeed":"0.2","calorie":"180","stime":"20220418185015000","istest":"1","testscore":"80","type":11},"time":"20220418185015000","type":11},{"data":{"etime":"20220418185153000","calorie":"180","stime":"20220418185015000","type":101},"time":"20220418185015000","type":101},{"data":{"etime":"20220418185153000","calorie":"180","stime":"20220418185015000","type":104},"time":"20220418185015000","type":104},{"data":{"etime":"20220418185153000","calorie":"180","stime":"20220418185015000","type":103},"time":"20220418185015000","type":103},{"data":{"etime":"20220418185153000","calorie":"180","stime":"20220418185015000","type":102},"time":"20220418185015000","type":102},{"data":{"etime":"20220418185153000","calorie":"180","stime":"20220418185015000","type":105},"time":"20220418185015000","type":105},{"data":{"etime":"20220418185153000","calorie":"180","stime":"20220418185015000","type":106},"time":"20220418185015000","type":106}]}
*/

import com.xiaoxun.xun.networkv2.retrofitclient.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MotionSportRecordBeans {
    @Id
    private Long id;
    String recordmonth;
    String eid;

    String time;
    int type;

    //类型,7游泳  类型,8骑行  类型,101羽毛球,102篮球,103足球,104舞蹈,105高尔夫,106马术
    String stime;//开始时间
    String etime;//结束时间
    String avgspeed;//平均速度公里/小时
    String testscore;//测试分数
    String istest="0";//是否测试模式,1是,0否
    String calorie;//卡路里
    //类型,5户外跑  类型,9键走
    String distance;//距离,公里
    String velocity;//配速
    //类型,6跳绳  类型,11仰卧起坐
    String count;
    String continuous;
    //类型,10骑行
    String uphigh;//上升高度
    String downhigh;//下降高度
    String duration;//运动持续时间

    String loc;//定位点
    String heartRate;//心率数据点
    String heartIntvl;//心率区间 com日常,hot热身,fat燃脂,aer有氧,atp无氧,lim极限

    String mTemp0;
    String mTemp1;
    String mTemp2;
    String mTemp3;

    public MotionSportRecordBeans(String eid, String recordmonth,String time, int type, String stime,
                                  String etime, String avgspeed, String testscore,
                                  String istest, String calorie, String distance,
                                  String velocity, String count, String continuous,
                                  String uphigh, String downhigh) {
        this.eid = eid;
        this.recordmonth = recordmonth;
        this.time = time;
        this.type = type;
        this.stime = stime;
        this.etime = etime;
        this.avgspeed = avgspeed;
        this.testscore = testscore;
        this.istest = istest;
        this.calorie = calorie;
        this.distance = distance;
        this.velocity = velocity;
        this.count = count;
        this.continuous = continuous;
        this.uphigh = uphigh;
        this.downhigh = downhigh;
    }


    @Generated(hash = 550074359)
    public MotionSportRecordBeans(Long id, String recordmonth, String eid, String time, int type, String stime, String etime, String avgspeed, String testscore, String istest, String calorie, String distance, String velocity, String count, String continuous, String uphigh, String downhigh, String duration, String loc, String heartRate, String heartIntvl, String mTemp0, String mTemp1, String mTemp2, String mTemp3) {
        this.id = id;
        this.recordmonth = recordmonth;
        this.eid = eid;
        this.time = time;
        this.type = type;
        this.stime = stime;
        this.etime = etime;
        this.avgspeed = avgspeed;
        this.testscore = testscore;
        this.istest = istest;
        this.calorie = calorie;
        this.distance = distance;
        this.velocity = velocity;
        this.count = count;
        this.continuous = continuous;
        this.uphigh = uphigh;
        this.downhigh = downhigh;
        this.duration = duration;
        this.loc = loc;
        this.heartRate = heartRate;
        this.heartIntvl = heartIntvl;
        this.mTemp0 = mTemp0;
        this.mTemp1 = mTemp1;
        this.mTemp2 = mTemp2;
        this.mTemp3 = mTemp3;
    }


    @Generated(hash = 1400474004)
    public MotionSportRecordBeans() {
    }


    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getStime() {
        return this.stime;
    }
    public void setStime(String stime) {
        this.stime = stime;
    }
    public String getEtime() {
        return this.etime;
    }
    public void setEtime(String etime) {
        this.etime = etime;
    }
    public String getAvgspeed() {
        return this.avgspeed;
    }
    public void setAvgspeed(String avgspeed) {
        this.avgspeed = avgspeed;
    }
    public String getTestscore() {
        return this.testscore;
    }
    public void setTestscore(String testscore) {
        this.testscore = testscore;
    }
    public String getIstest() {
        return this.istest;
    }
    public void setIstest(String istest) {
        this.istest = istest;
    }
    public String getCalorie() {
        return this.calorie;
    }
    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }
    public String getDistance() {
        return this.distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }
    public String getVelocity() {
        return this.velocity;
    }
    public void setVelocity(String velocity) {
        this.velocity = velocity;
    }
    public String getCount() {
        return this.count;
    }
    public void setCount(String count) {
        this.count = count;
    }
    public String getContinuous() {
        return this.continuous;
    }
    public void setContinuous(String continuous) {
        this.continuous = continuous;
    }
    public String getUphigh() {
        return this.uphigh;
    }
    public void setUphigh(String uphigh) {
        this.uphigh = uphigh;
    }
    public String getDownhigh() {
        return this.downhigh;
    }
    public void setDownhigh(String downhigh) {
        this.downhigh = downhigh;
    }
    public String getRecordmonth() {
        return this.recordmonth;
    }
    public void setRecordmonth(String recordmonth) {
        this.recordmonth = recordmonth;
    }
    public String getEid() {
        return this.eid;
    }
    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getmTemp0() {
        return mTemp0;
    }

    public void setmTemp0(String mTemp0) {
        this.mTemp0 = mTemp0;
    }

    public String getmTemp1() {
        return mTemp1;
    }

    public void setmTemp1(String mTemp1) {
        this.mTemp1 = mTemp1;
    }

    public String getmTemp2() {
        return mTemp2;
    }

    public void setmTemp2(String mTemp2) {
        this.mTemp2 = mTemp2;
    }

    public String getmTemp3() {
        return mTemp3;
    }

    public void setmTemp3(String mTemp3) {
        this.mTemp3 = mTemp3;
    }

    public String getLoc() {
        return this.loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getHeartRate() {
        return this.heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getHeartIntvl() {
        return this.heartIntvl;
    }

    public void setHeartIntvl(String heartIntvl) {
        this.heartIntvl = heartIntvl;
    }


    public String getMTemp0() {
        return this.mTemp0;
    }


    public void setMTemp0(String mTemp0) {
        this.mTemp0 = mTemp0;
    }


    public String getMTemp1() {
        return this.mTemp1;
    }


    public void setMTemp1(String mTemp1) {
        this.mTemp1 = mTemp1;
    }


    public String getMTemp2() {
        return this.mTemp2;
    }


    public void setMTemp2(String mTemp2) {
        this.mTemp2 = mTemp2;
    }


    public String getMTemp3() {
        return this.mTemp3;
    }


    public void setMTemp3(String mTemp3) {
        this.mTemp3 = mTemp3;
    }

}
