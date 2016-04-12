package com.jtv.hrb.locationwork.domain;

import java.util.List;

public class CurveLineBean {

    /**
     * endtime : 18:00
     * lead : 长葛工区()
     * location : 京广-下(641.3-807),京广-上行-线(641.3-807)
     * locationdata : [{"assetnum":"PXL000001","classstructureid":"1322","description":"京广-下行-线","endmeasure":"643","line":[{"endmeasure":397.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[23.4,45.6,78.9,23.4],"qhxc":150,"qxbj":1800,"startmeasure":395.998,"xxqc":1023.9,"zhxc":150},{"endmeasure":381.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[23.4,45.6,78.9,23.4],"qhxc":0,"qxbj":15000,"startmeasure":380.998,"xxqc":82.39,"zhxc":0},{"endmeasure":382.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":0,"qxbj":14000,"startmeasure":381.998,"xxqc":80.77,"zhxc":0},{"endmeasure":383.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":130,"qxbj":2000,"startmeasure":382.998,"xxqc":1183.25,"zhxc":130},{"endmeasure":34.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":0,"qxbj":20000,"startmeasure":383.998,"xxqc":82.32,"zhxc":0},{"endmeasure":385.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":0,"qxbj":20000,"startmeasure":384.998,"xxqc":83.29,"zhxc":0}],"startmeasure":"641.3"},{"assetnum":"PXL000002","classstructureid":"1322","description":"京广-上行-线","endmeasure":"643","line":[],"startmeasure":"641.3"}]
     * planname : 设备检查(测试静态检查原始数据)
     * schedstartdate : 2016-03-14
     * starttime : 8:00
     * wonum : 17136
     * wotype : 天窗外
     */

    private String endtime;
    private String lead;
    private String location;
    private String planname;
    private String schedstartdate;
    private String starttime;
    private String wonum;
    private String wotype;
    /**
     * assetnum : PXL000001
     * classstructureid : 1322
     * description : 京广-下行-线
     * endmeasure : 643
     * line : [{"endmeasure":397.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[23.4,45.6,78.9,23.4],"qhxc":150,"qxbj":1800,"startmeasure":395.998,"xxqc":1023.9,"zhxc":150},{"endmeasure":381.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[23.4,45.6,78.9,23.4],"qhxc":0,"qxbj":15000,"startmeasure":380.998,"xxqc":82.39,"zhxc":0},{"endmeasure":382.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":0,"qxbj":14000,"startmeasure":381.998,"xxqc":80.77,"zhxc":0},{"endmeasure":383.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":130,"qxbj":2000,"startmeasure":382.998,"xxqc":1183.25,"zhxc":130},{"endmeasure":34.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":0,"qxbj":20000,"startmeasure":383.998,"xxqc":82.32,"zhxc":0},{"endmeasure":385.998,"label":"陇海,下行,起止千米(395.998,397.99),短链","planvalue":[],"qhxc":0,"qxbj":20000,"startmeasure":384.998,"xxqc":83.29,"zhxc":0}]
     * startmeasure : 641.3
     */

    private List<LocationdataBean> locationdata;

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public void setLead(String lead) {
        this.lead = lead;
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

    public void setLocationdata(List<LocationdataBean> locationdata) {
        this.locationdata = locationdata;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getLead() {
        return lead;
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

    public List<LocationdataBean> getLocationdata() {
        return locationdata;
    }

    public static class LocationdataBean {
        private String assetnum;
        private String classstructureid;
        private String description;
        private String endmeasure;
        private String startmeasure;
        /**
         * endmeasure : 397.998
         * label : 陇海,下行,起止千米(395.998,397.99),短链
         * planvalue : [23.4,45.6,78.9,23.4]
         * qhxc : 150
         * qxbj : 1800
         * startmeasure : 395.998
         * xxqc : 1023.9
         * zhxc : 150
         */

        private List<LineBean> line;

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

        public void setLine(List<LineBean> line) {
            this.line = line;
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

        public List<LineBean> getLine() {
            return line;
        }

        public static class LineBean {
            private double endmeasure;
            private String label;
            private int qhxc;
            private int qxbj;
            private double startmeasure;
            private double xxqc;
            private int zhxc;
            private List<Double> planvalue;

            public void setEndmeasure(double endmeasure) {
                this.endmeasure = endmeasure;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public void setQhxc(int qhxc) {
                this.qhxc = qhxc;
            }

            public void setQxbj(int qxbj) {
                this.qxbj = qxbj;
            }

            public void setStartmeasure(double startmeasure) {
                this.startmeasure = startmeasure;
            }

            public void setXxqc(double xxqc) {
                this.xxqc = xxqc;
            }

            public void setZhxc(int zhxc) {
                this.zhxc = zhxc;
            }

            public void setPlanvalue(List<Double> planvalue) {
                this.planvalue = planvalue;
            }

            public double getEndmeasure() {
                return endmeasure;
            }

            public String getLabel() {
                return label;
            }

            public int getQhxc() {
                return qhxc;
            }

            public int getQxbj() {
                return qxbj;
            }

            public double getStartmeasure() {
                return startmeasure;
            }

            public double getXxqc() {
                return xxqc;
            }

            public int getZhxc() {
                return zhxc;
            }

            public List<Double> getPlanvalue() {
                return planvalue;
            }
        }
    }
}