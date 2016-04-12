package com.jtv.hrb.locationwork.domain;

public class QuestionBean {
	private int _id;
	private int qd_id;
	private String questDesc;

	public QuestionBean() {
	}

	public QuestionBean(String questDesc) {
		this.questDesc = questDesc;
	}

	public int getQd_id() {
		return qd_id;
	}

	public void setQd_id(int qd_id) {
		this.qd_id = qd_id;
	}

	public QuestionBean(int _id, String questDesc) {
		this._id = _id;
		this.questDesc = questDesc;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getQuestDesc() {
		return questDesc;
	}

	public void setQuestDesc(String questDesc) {
		this.questDesc = questDesc;
	}

	@Override
	public String toString() {
		return "QuestionBean [_id=" + _id + ", questDesc=" + questDesc + "]";
	}
}