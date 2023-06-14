/**
 * Creation Date:2015-1-12
 * 
 * Copyright 
 */
package com.xiaoxun.xun.beans;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-1-12
 * 
 */
public class UserData {
    private String uid;//用户登陆名
    private String xiaomiId;//小米账号id，
    private String nickname;//保存手机号
    private String headPath = "0";// 0,1,2,3,4 表示id， 其他表示headmd5，从网上同步图片，本地索引缓存
    private String eid;//用户系统中使用的id
    private String cellNum;//手机号
    private String alias;//别名，就是和每个watch的关系 使用jsonobject表示[{"eid1":"father","eid2":"uncle"},]
    private CustomData customData = new CustomData();//自定义数据
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getXiaomiId() {
        return xiaomiId;
    }

    public void setXiaomiId(String xiaomiId) {
        this.xiaomiId = xiaomiId;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    //使用custom字段保存headpath
    public String getHeadPath() {
       // return headPath;
        return customData.getHeadkey();
    }
    public void setHeadPath(String headpath) {
     //   this.headPath = headpath;
        customData.setHeadkey(headpath);
    }
    public String getEid() {
        return eid;
    }
    public void setEid(String eid) {
        this.eid = eid;
    }
    private String getAlias() {
        return alias;
    }
    private void setAlias(String alias) {
        this.alias = alias;
    }
    public void setRelation(String eid,String relation){
       customData.setRelation(relation, eid);
    }
    //get relation from alias
    public String getRelation(String eid){
        return customData.getRelation(eid);
    }
    
//    public void parseRelationStr(String joStr){
//        try {
//            joRelation = (JSONObject) JSONValue.parse(joStr);           
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//    }
    
//    private String genRelationStr(){
//        return joRelation.toJSONString();
//    }
    public CustomData getCustomData() {
        return customData;
    }

    public String getCellNum() {
        return cellNum;
    }

    public void setCellNum(String cellNum) {
        this.cellNum = cellNum;
    }
//    public void setCustomData(CustomData customData) {
//        this.customData = customData;
//    }
}
