package com.jtv.locationwork.entity;

import java.util.List;

/**
 * 线路检查的json实体
 * <p>
 *
 * @author 更生
 * @version 2016年3月13日
 */
public class LineCheckJson {

	private String endtime;

	private String linename;

	private String linetype;

	private String wotype;

	private String starttime;

	private String planname;

	private String location;

	private List<Locationdata> locationdata;
	
	private String lead;

	private String schedstartdate;

	private String wonum;

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getEndtime() {
		return this.endtime;
	}

	
	
	public void setLinename(String linename) {
		this.linename = linename;
	}

	public String getLinename() {
		return this.linename;
	}

	public void setLinetype(String linetype) {
		this.linetype = linetype;
	}

	public String getLinetype() {
		return this.linetype;
	}

	public void setWotype(String wotype) {
		this.wotype = wotype;
	}

	public String getWotype() {
		return this.wotype;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getStarttime() {
		return this.starttime;
	}

	public void setPlanname(String planname) {
		this.planname = planname;
	}

	public String getPlanname() {
		return this.planname;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocationdata(List<Locationdata> locationdata) {
		this.locationdata = locationdata;
	}

	public List<Locationdata> getLocationdata() {
		return this.locationdata;
	}
	
	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getLead() {
		return this.lead;
	}

	public void setSchedstartdate(String schedstartdate) {
		this.schedstartdate = schedstartdate;
	}

	public String getSchedstartdate() {
		return this.schedstartdate;
	}

	public void setWonum(String wonum) {
		this.wonum = wonum;
	}

	public String getWonum() {
		return this.wonum;
	}

}