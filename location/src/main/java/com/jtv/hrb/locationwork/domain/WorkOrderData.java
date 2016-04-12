package com.jtv.hrb.locationwork.domain;

import java.util.List;
/**
 * Created by fangdean on 2016/3/12.
 */
public class WorkOrderData {

    /**
     * endtime : 18:00,
     * lead : 吴仲华(13608677801)
     * linename : 京广
     * linetype : 双
     * location : 京广-上行-线(724.5-725),京广-下(724.5-725)
     * locationdata : [{"line":[""],"turnout":[""],"assetnum":"PXL000001","classstructureid":"1322","description":"京广-下行-线","endmeasure":"725","startmeasure":"724.5"},{"turnout":[""],"assetnum":"PXL000002","classstructureid":"1322","description":"京广-上行-线","endmeasure":"725","startmeasure":"724.5"}]
     * planname : 整理外观(整理外观、)
     * schedstartdate : 2016-03-12
     * starttime : 08:00
     * wonum : zz31465
     * wotype : 天窗外
     */

    private String endtime;
    private String lead;
    private String linename;
    private String linetype;
    private String location;
    private String planname;
    private String schedstartdate;
    private String starttime;
    private String wonum;
    private String wotype;
    /**
     * line : [""]
     * turnout : [""]
     * assetnum : PXL000001
     * classstructureid : 1322
     * description : 京广-下行-线
     * endmeasure : 725
     * startmeasure : 724.5
     */

    private List<LocationdataEntity> locationdata;

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public void setLinename(String linename) {
        this.linename = linename;
    }

    public void setLinetype(String linetype) {
        this.linetype = linetype;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public void setSchedstartdate(String schedstartdate) {
        this.schedstartdate = schedstartdate;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public void setWonum(String wonum) {
        this.wonum = wonum;
    }

    public void setWotype(String wotype) {
        this.wotype = wotype;
    }

    public void setLocationdata(List<LocationdataEntity> locationdata) {
        this.locationdata = locationdata;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getLead() {
        return lead;
    }

    public String getLinename() {
        return linename;
    }

    public String getLinetype() {
        return linetype;
    }

    public String getLocation() {
        return location;
    }

    public String getPlanname() {
        return planname;
    }

    public String getSchedstartdate() {
        return schedstartdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getWonum() {
        return wonum;
    }

    public String getWotype() {
        return wotype;
    }

    public List<LocationdataEntity> getLocationdata() {
        return locationdata;
    }

    public static class LocationdataEntity {
        private String assetnum;
        private String classstructureid;
        private String description;
        private String endmeasure;
        private String startmeasure;
        private List<String> line;
        private List<String> turnout;

        public void setAssetnum(String assetnum) {
            this.assetnum = assetnum;
        }

        public void setClassstructureid(String classstructureid) {
            this.classstructureid = classstructureid;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setEndmeasure(String endmeasure) {
            this.endmeasure = endmeasure;
        }

        public void setStartmeasure(String startmeasure) {
            this.startmeasure = startmeasure;
        }

        public void setLine(List<String> line) {
            this.line = line;
        }

        public void setTurnout(List<String> turnout) {
            this.turnout = turnout;
        }

        public String getAssetnum() {
            return assetnum;
        }

        public String getClassstructureid() {
            return classstructureid;
        }

        public String getDescription() {
            return description;
        }

        public String getEndmeasure() {
            return endmeasure;
        }

        public String getStartmeasure() {
            return startmeasure;
        }

        public List<String> getLine() {
            return line;
        }

        public List<String> getTurnout() {
            return turnout;
        }
    }
}
