package com.jtv.hrb.locationwork.domain;

import java.util.ArrayList;
import java.util.List;

public class RailNumBean {
	private int _id;
	private int gh;
	private String xvalue;
	private String yvalue;
	private String time;
	private List<LineNumBean> lineNumBeans = new ArrayList<LineNumBean>();
	private List<QuestionBean> questionBeans = new ArrayList<QuestionBean>();

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getGh() {
		return gh;
	}

	public void setGh(int gh) {
		this.gh = gh;
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

	public List<LineNumBean> getLineNumBeans() {
		return lineNumBeans;
	}

	public void setLineNumBeans(List<LineNumBean> lineNumBeans) {
		this.lineNumBeans = lineNumBeans;
	}

	public List<QuestionBean> getQuestionBeans() {
		return questionBeans;
	}

	public void setQuestionBeans(List<QuestionBean> questionBeans) {
		this.questionBeans = questionBeans;
	}
}