package com.xiaoxun.xun.ScheduleCard.beans;

public class ScheduleCardItemBean {
    private int mItemType; //1：课程节  2：课程名  3：编辑课程  4:擦除按钮   5:添加按钮
    private String mScheduleName;
    private String mScheduleDesc;
    private boolean isEditMode;  //进入编辑状态
    private boolean isEditOperate;//是否可操作
    private boolean isSelect;//是否是当前选中状态

    public ScheduleCardItemBean(int mItemType, String mScheduleName, String mScheduleDesc, boolean isEditMode, boolean isEditOperate, boolean isSelect) {
        this.mItemType = mItemType;
        this.mScheduleName = mScheduleName;
        this.mScheduleDesc = mScheduleDesc;
        this.isEditMode = isEditMode;
        this.isEditOperate = isEditOperate;
        this.isSelect = isSelect;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public boolean isEditOperate() {
        return isEditOperate;
    }

    public void setEditOperate(boolean editOperate) {
        isEditOperate = editOperate;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getmItemType() {
        return mItemType;
    }

    public void setmItemType(int mItemType) {
        this.mItemType = mItemType;
    }

    public String getmScheduleName() {
        return mScheduleName;
    }

    public void setmScheduleName(String mScheduleName) {
        this.mScheduleName = mScheduleName;
    }

    public String getmScheduleDesc() {
        return mScheduleDesc;
    }

    public void setmScheduleDesc(String mScheduleDesc) {
        this.mScheduleDesc = mScheduleDesc;
    }
}
