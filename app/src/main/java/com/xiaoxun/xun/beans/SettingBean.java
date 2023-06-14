package com.xiaoxun.xun.beans;

/**
 * @author cuiyufeng
 * @Description: SettingBean
 * @date 2018/12/17 11:49
 */
public class SettingBean {
    private String name;
    private int picture;
    private boolean isShowRed;

    public SettingBean(String name, int picture){
        this.name=name;
        this.picture=picture;
        this.isShowRed=false;
    }

    public SettingBean(String name, int picture, boolean isShowRed){
        this.name=name;
        this.picture=picture;
        this.isShowRed=isShowRed;
    }

    public String getName() {
        return name;
    }


    public int getPicture() {
        return picture;
    }


    public boolean isShowRed() {
        return isShowRed;
    }

}
