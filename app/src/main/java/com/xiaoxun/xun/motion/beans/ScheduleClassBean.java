package com.xiaoxun.xun.motion.beans;

public class ScheduleClassBean {
    private String mClassName;
    private boolean isCanDelete;
    private int classType = 1;//1：课程条目   2：添加课程

    public ScheduleClassBean(String mClassName, boolean isCanDelete, int isType) {
        this.mClassName = mClassName;
        this.isCanDelete = isCanDelete;
        classType = isType;
    }

    public String getmClassName() {
        return mClassName;
    }

    public void setmClassName(String mClassName) {
        this.mClassName = mClassName;
    }

    public ScheduleClassBean() {
    }

    public boolean isCanDelete() {
        return isCanDelete;
    }

    public void setCanDelete(boolean canDelete) {
        isCanDelete = canDelete;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }
}
