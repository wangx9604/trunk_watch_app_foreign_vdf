/**
 * Creation Date:2015-1-19
 *
 * Copyright 
 */
package com.xiaoxun.xun.beans;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-1-19
 *
 */
public class MemberUserData extends UserData{
    private String familyId;
    //    private String relation;
    public String getFamilyId() {
        return familyId;
    }
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
//    public String getRelation() {
//        return relation;
//    }
//    public void setRelation(String relation) {
//        this.relation = relation;
//    }

    private boolean isSelect = false;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
