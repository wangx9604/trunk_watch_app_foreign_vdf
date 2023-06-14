/**
 * Creation Date:2015-5-15
 * 
 * Copyright 
 */
package com.xiaoxun.xun.beans;

import com.xiaoxun.xun.utils.CloudBridgeUtil;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.util.ArrayList;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-5-15
 * 
 */
public class CustomData {
    public String headkey;//
    public String relation;
    private JSONObject joRelation = new JSONObject();

    public String toJsonStr(){//生成json 
        JSONObject jo = new JSONObject();
        jo.put(CloudBridgeUtil.KEY_NAME_CUSTOM_HEADKEY, headkey);
        jo.put(CloudBridgeUtil.KEY_NAME_CUSTOM_RELATION, joRelation.toString());        
        
        return jo.toJSONString();
        
    }
    
    public String getRelation(String eid) {
        String str = "家长";
        if (joRelation!=null){
            str =  (String) joRelation.get(eid);
         }
         if (str == null||str.isEmpty())
             str = "家长";
         return str;
    }

    public void setRelation(String relation, String eid) {
        if (joRelation == null){
            joRelation = new JSONObject();
        }
        joRelation.put(eid, relation);
    }
    //需要重新刷新过滤custom字段下的relation，去除不再群组中的关系名
    public void reloadRelation(ArrayList<WatchData> watchList){
        JSONObject newJo = new JSONObject();
        if (watchList!=null&&watchList.size()>0){
            for (WatchData watch:watchList){
                String relation =  (String)joRelation.get(watch.getEid());
                if (relation!=null) {
                    newJo.put(watch.getEid(),relation);
                }
            }
        }else{
           // joRelation = newJo;
        }
        joRelation = newJo;
    }
    public String getHeadkey() {
        return headkey;
    }

    public void setHeadkey(String headkey) {
        this.headkey = headkey;
    }

    public CustomData(String json) {
        super();
        setFromJsonStr(json);
    }

    public CustomData() {
        // TODO Auto-generated constructor stub
    }

    public void setFromJsonStr(String json){
        if (json!=null && json.length() > 0){
        JSONObject jo = (JSONObject) JSONValue.parse(json);
        try {
            headkey = (String) jo.get(CloudBridgeUtil.KEY_NAME_CUSTOM_HEADKEY);  
            relation = (String) jo.get(CloudBridgeUtil.KEY_NAME_CUSTOM_RELATION); 
            if (relation!=null){
                try {
                    joRelation = (JSONObject) JSONValue.parse(relation);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        }
    }
}
