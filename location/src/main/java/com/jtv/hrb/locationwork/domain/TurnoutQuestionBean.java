package com.jtv.hrb.locationwork.domain;

public class TurnoutQuestionBean {
	private int _id;
	private int turnoutId;
	private int partType;//1,2,3
	private String assetnum;//
	private String questDesc;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getTurnoutId() {
		return turnoutId;
	}

	public void setTurnoutId(int turnoutId) {
		this.turnoutId = turnoutId;
	}

	public int getPartType() {
		return partType;
	}

	public String getAssetnum() {
		return assetnum;
	}

	public void setAssetnum(String assetnum) {
		this.assetnum = assetnum;
	}

	public void setPartType(int partType) {
		this.partType = partType;
	}

	public String getQuestDesc() {
		return questDesc;
	}

	public void setQuestDesc(String questDesc) {
		this.questDesc = questDesc;
	}

}
