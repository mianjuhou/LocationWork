package com.jtv.hrb.locationwork.domain;

public class LineNumBean {
	private int _id;
	private int gh_id;
	private int linenum;
	private int value1;
	private int value2;
	
	public LineNumBean() {
	}

	public LineNumBean(int linenum, int value1, int value2) {
		this.linenum = linenum;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public LineNumBean(int _id, int linenum, int value1, int value2) {
		this._id = _id;
		this.linenum = linenum;
		this.value1 = value1;
		this.value2 = value2;
	}

	public int getGh_id() {
		return gh_id;
	}

	public void setGh_id(int gh_id) {
		this.gh_id = gh_id;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getLinenum() {
		return linenum;
	}

	public void setLinenum(int linenum) {
		this.linenum = linenum;
	}

	public int getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(int value2) {
		this.value2 = value2;
	}

	@Override
	public String toString() {
		return "LineNumBean [_id=" + _id + ", linenum=" + linenum + ", value1=" + value1 + ", value2=" + value2 + "]";
	}

}