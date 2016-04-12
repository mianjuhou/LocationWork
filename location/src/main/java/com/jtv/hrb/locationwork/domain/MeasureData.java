package com.jtv.hrb.locationwork.domain;

public class MeasureData {
	private int id;
	private String tag;
	private String siteid;
	private String orgid;
	private String gh;
	private double startmeasure;
	private double endmeasure;
	private int linenum;
	private double value1;
	private double value2;
	private int hasld;
	private String assetattrid;
	private String assetnum;
	private int assetfeatureid;
	private String wonum;
	private String nametype;
	private String status;
	private String defectclass;
	private double fvalue;
	private String flag_v;
	private String xvalue;
	private String yvalue;
	private String inspdate;
	private String cp1nanumcfgrownum;
	private int measuretype;
	private int syncstate;
	
	public int getMeasuretype() {
		return measuretype;
	}
	public void setMeasuretype(int measuretype) {
		this.measuretype = measuretype;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public double getFvalue() {
		return fvalue;
	}
	public void setFvalue(double fvalue) {
		this.fvalue = fvalue;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getSiteid() {
		return siteid;
	}
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getGh() {
		return gh;
	}
	public void setGh(String gh) {
		this.gh = gh;
	}
	public double getStartmeasure() {
		return startmeasure;
	}
	public void setStartmeasure(double startmeasure) {
		this.startmeasure = startmeasure;
	}
	public double getEndmeasure() {
		return endmeasure;
	}
	public void setEndmeasure(double endmeasure) {
		this.endmeasure = endmeasure;
	}
	public int getLinenum() {
		return linenum;
	}
	public void setLinenum(int linenum) {
		this.linenum = linenum;
	}
	public double getValue1() {
		return value1;
	}
	public void setValue1(double value1) {
		this.value1 = value1;
	}
	public double getValue2() {
		return value2;
	}
	public void setValue2(double value2) {
		this.value2 = value2;
	}
	public int getHasld() {
		return hasld;
	}
	public void setHasld(int hasld) {
		this.hasld = hasld;
	}
	public String getAssetattrid() {
		return assetattrid;
	}
	public void setAssetattrid(String assetattrid) {
		this.assetattrid = assetattrid;
	}
	public String getAssetnum() {
		return assetnum;
	}
	public void setAssetnum(String assetnum) {
		this.assetnum = assetnum;
	}
	public int getAssetfeatureid() {
		return assetfeatureid;
	}
	public void setAssetfeatureid(int assetfeatureid) {
		this.assetfeatureid = assetfeatureid;
	}
	public String getWonum() {
		return wonum;
	}
	public void setWonum(String wonum) {
		this.wonum = wonum;
	}
	public String getNametype() {
		return nametype;
	}
	public void setNametype(String nametype) {
		this.nametype = nametype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDefectclass() {
		return defectclass;
	}
	public void setDefectclass(String defectclass) {
		this.defectclass = defectclass;
	}
	public String getFlag_v() {
		return flag_v;
	}
	public void setFlag_v(String flag_v) {
		this.flag_v = flag_v;
	}
	public String getXvalue() {
		return xvalue;
	}
	public void setXvalue(String xvalue) {
		this.xvalue = xvalue;
	}
	public String getYvalue() {
		return yvalue;
	}
	public void setYvalue(String yvalue) {
		this.yvalue = yvalue;
	}
	public String getInspdate() {
		return inspdate;
	}
	public void setInspdate(String inspdate) {
		this.inspdate = inspdate;
	}
	public String getCp1nanumcfgrownum() {
		return cp1nanumcfgrownum;
	}
	public void setCp1nanumcfgrownum(String cp1nanumcfgrownum) {
		this.cp1nanumcfgrownum = cp1nanumcfgrownum;
	}
	public int getSyncstate() {
		return syncstate;
	}
	public void setSyncstate(int syncstate) {
		this.syncstate = syncstate;
	}
	@Override
	public String toString() {
		return "DataBean [id=" + id + ", siteid=" + siteid + ", orgid=" + orgid + ", gh=" + gh + ", startmeasure=" + startmeasure + ", endmeasure="
				+ endmeasure + ", linenum=" + linenum + ", value1=" + value1 + ", hasld=" + hasld + ", assetattrid=" + assetattrid + ", assetnum="
				+ assetnum + ", assetfeatureid=" + assetfeatureid + ", wonum=" + wonum + ", nametype=" + nametype + ", status=" + status
				+ ", defectclass=" + defectclass + ", flag_v=" + flag_v + ", xvalue=" + xvalue + ", yvalue=" + yvalue + ", inspdate=" + inspdate
				+ ", cp1nanumcfgrownum=" + cp1nanumcfgrownum + "]";
	}
	
}
