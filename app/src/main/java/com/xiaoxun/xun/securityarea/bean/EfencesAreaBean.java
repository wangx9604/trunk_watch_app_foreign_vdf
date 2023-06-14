package com.xiaoxun.xun.securityarea.bean;

import java.io.Serializable;

public class EfencesAreaBean implements Serializable {
    private String efid;
    private String etype;
    private String efname;
    private String timestamp;
    private String[] points;
    private String desc;

    public String getEfid() {
        return efid;
    }

    public void setEfid(String efid) {
        this.efid = efid;
    }

    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public String getEfname() {
        return efname;
    }

    public void setEfname(String efname) {
        this.efname = efname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String[] getPoints() {
        return points;
    }

    public void setPoints(String[] points) {
        this.points = points;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
