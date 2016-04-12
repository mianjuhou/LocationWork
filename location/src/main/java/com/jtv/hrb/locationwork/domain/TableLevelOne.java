package com.jtv.hrb.locationwork.domain;

/**
 * Created by fangdean on 2016/3/23.
 */
public class TableLevelOne {

    /**
     * enterby : 6LW15LiJ5qOS
     * hasld : 0
     * orgid : ORG2
     * siteid : S05118
     * wonum : 31530
     */

    private String enterby;
    private int hasld;
    private String orgid;
    private String siteid;
    private String wonum;

    public void setEnterby(String enterby) {
        this.enterby = enterby;
    }

    public void setHasld(int hasld) {
        this.hasld = hasld;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public void setWonum(String wonum) {
        this.wonum = wonum;
    }

    public String getEnterby() {
        return enterby;
    }

    public int getHasld() {
        return hasld;
    }

    public String getOrgid() {
        return orgid;
    }

    public String getSiteid() {
        return siteid;
    }

    public String getWonum() {
        return wonum;
    }
}
