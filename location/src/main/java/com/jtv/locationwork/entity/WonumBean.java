package com.jtv.locationwork.entity;

import com.jtv.dbentity.annotation.Column;
import com.jtv.dbentity.annotation.Id;
import com.jtv.dbentity.annotation.Table;

@Table(name = "wonum")
public class WonumBean {
	@Id
	@Column(name = "id")
	private int ID;

	@Column(name = "content")
	private String content;

	@Column(name = "wonum")
	private String wonum;

	@Column(name = "type")
	private String type;

	@Column(name = "starttime")
	private String starttime;

	@Column(name = "endtime")
	private String endtime;

	@Column(name = "other")
	private String other;

	@Column(name = "inserttime")
	private long insertTime;
	
	@Column(name = "tabletype")
	private String tableType;
	
	@Column(name = "level")
	private String level;
	
	@Column(name = "empty")
	private String empty;
	
	public String getTableType() {
		return tableType;
	}

	public void setTableType(String table) {
		this.tableType = table;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWonum() {
		return wonum;
	}

	public void setWonum(String wonum) {
		this.wonum = wonum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
	
	
	
	
	
}
