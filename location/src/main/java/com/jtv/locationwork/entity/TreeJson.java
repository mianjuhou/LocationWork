package com.jtv.locationwork.entity;

public class TreeJson {
	// {"title":"驻地安全卡控","parmeter":1,"level":1,"createdate":"2015-10-10
	// 17:54:35.0","siteid":"S01116","parentid":0,"nodeid":1}

	private String title;
	private String parmeter;
	private String siteid;
	private String parentid;
	private String nodeid;
	private String tabletype;

	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParmeter() {
		return parmeter;
	}

	public void setParmeter(String parmeter) {
		this.parmeter = parmeter;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getTabletype() {
		return tabletype;
	}

	public void setTabletype(String tabletype) {
		this.tabletype = tabletype;
	}


}
